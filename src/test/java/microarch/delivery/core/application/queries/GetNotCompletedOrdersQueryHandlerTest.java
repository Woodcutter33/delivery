package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetNotCompletedOrdersQueryHandlerTest {

    @Mock
    private OrderRepository repository;

    private GetNotCompletedOrdersQueryHandlerImpl handler;

    @BeforeEach
    void setUp() {
        handler = new GetNotCompletedOrdersQueryHandlerImpl(repository);
    }

    @Test
    void mapsCreatedAndAssignedOrdersWithoutChangingThem() {
        Order created = Order.create(UUID.randomUUID(), Volume.create(1).getValue(), Location.create(1, 2).getValue())
                .getValue();

        Order assigned = Order.create(UUID.randomUUID(), Volume.create(2).getValue(), Location.create(3, 4).getValue())
                .getValue();

        assigned.assign();

        when(repository.findNotCompleted()).thenReturn(List.of(created, assigned));

        Result<List<GetNotCompletedOrdersResponse>, Error> result = handler.handle();

        assertTrue(result.isSuccess());
        assertEquals(
                List.of(new GetNotCompletedOrdersResponse(created.getId(), created.getLocation()),
                        new GetNotCompletedOrdersResponse(assigned.getId(), assigned.getLocation())),
                result.getValue());
        assertEquals(OrderStatus.CREATED, created.getStatus());
        assertEquals(OrderStatus.ASSIGNED, assigned.getStatus());

        verify(repository).findNotCompleted();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void returnsSuccessfulEmptyList() {
        when(repository.findNotCompleted()).thenReturn(List.of());

        Result<List<GetNotCompletedOrdersResponse>, Error> result = handler.handle();

        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isEmpty());

        verify(repository).findNotCompleted();
        verifyNoMoreInteractions(repository);
    }
}
