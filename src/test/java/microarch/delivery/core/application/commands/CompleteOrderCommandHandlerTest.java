package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Assignment;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteOrderCommandHandlerTest {

    @Mock
    private CourierRepository couriers;

    @Mock
    private OrderRepository orders;

    private CompleteOrderCommandHandlerImpl handler;
    private Courier courier;
    private Order order;

    @BeforeEach
    void setUp() {
        handler = new CompleteOrderCommandHandlerImpl(couriers, orders);
        Location location = Location.create(1, 1).getValue();
        courier = Courier.create("Ivan", location).getValue();
        order = Order.create(UUID.randomUUID(), Volume.create(2).getValue(), location).getValue();
    }

    @Test
    void completesAssignmentAndOrderAndSavesBoth() {
        loadAggregates();
        assign();
        assertTrue(handle().isSuccess());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertFalse(courier.getAssignments().getFirst().isActive());
        verify(couriers).update(courier);
        verify(orders).update(order);
    }

    @Test
    void returnsErrorWhenCourierIsMissing() {
        when(couriers.findById(courier.getId())).thenReturn(Optional.empty());
        assertFailureWithoutWrites(handle(), GeneralErrors.notFound("courier", courier.getId()));
        verifyNoInteractions(orders);
    }

    @Test
    void returnsErrorWhenOrderIsMissing() {
        when(couriers.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orders.findById(order.getId())).thenReturn(Optional.empty());
        assertFailureWithoutWrites(handle(), GeneralErrors.notFound("order", order.getId()));
    }

    @Test
    void returnsErrorWhenCourierIsTooFar() {
        order = Order.create(UUID.randomUUID(), Volume.create(2).getValue(), Location.create(3, 1).getValue())
                .getValue();
        loadAggregates();
        assign();
        assertFailureWithoutWrites(handle(), Assignment.Errors.courierTooFar(2));
        assertTrue(courier.getAssignments().getFirst().isActive());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
    }

    @Test
    void completesWhenCourierIsOneStepAway() {
        order = Order.create(UUID.randomUUID(), Volume.create(2).getValue(), Location.create(2, 1).getValue())
                .getValue();
        loadAggregates();
        assign();
        assertTrue(handle().isSuccess());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertFalse(courier.getAssignments().getFirst().isActive());
        verify(couriers).update(courier);
        verify(orders).update(order);
    }

    private void loadAggregates() {
        when(couriers.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(orders.findById(order.getId())).thenReturn(Optional.of(order));
    }

    private void assign() {
        assertTrue(courier.takeOrder(order.getId(), order.getVolume(), order.getLocation()).isSuccess());
        assertTrue(order.assign().isSuccess());
    }

    private UnitResult<Error> handle() {
        return handler.handle(CompleteOrderCommand.create(courier.getId(), order.getId()).getValue());
    }

    private void assertFailureWithoutWrites(UnitResult<Error> result, Error expected) {
        assertTrue(result.isFailure());
        assertEquals(expected, result.getError());
        verify(couriers, never()).save(any());
        verify(couriers, never()).update(any());
        verify(orders, never()).save(any());
        verify(orders, never()).update(any());
    }
}
