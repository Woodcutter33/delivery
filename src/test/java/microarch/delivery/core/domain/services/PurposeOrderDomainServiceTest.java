package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Assignment;
import microarch.delivery.core.domain.model.order.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PurposeOrderDomainServiceTest {

    private static final UUID ORDER_ID_1 = UUID.randomUUID();
    private static final UUID ORDER_ID_2 = UUID.randomUUID();
    private static final UUID ORDER_ID_3 = UUID.randomUUID();
    private static final UUID ORDER_ID_4 = UUID.randomUUID();

    @Test
    void shouldReturnSuccessWhenCourierTookOrder() {
        Volume volume1 = Volume.create(10).getValue();
        Volume volume2 = Volume.create(2).getValue();
        Volume volume3 = Volume.create(5).getValue();
        Volume volume4 = Volume.create(6).getValue();

        Location location1 = Location.create(1, 2).getValue();
        Location location2 = Location.create(5, 2).getValue();
        Location location3 = Location.create(1, 3).getValue();
        Location location4 = Location.create(1, 3).getValue();

        Courier courier1 = Courier.create("Ivan", location1).getValue();
        courier1.takeOrder(ORDER_ID_1, volume1, location1);
        courier1.takeOrder(ORDER_ID_2, volume2, location2);
        Courier courier2 = Courier.create("NeIvan", location2).getValue();

        Order newOrder = Order.create(ORDER_ID_3, volume3, location3).getValue();

        List<Courier> couriers = List.of(courier1, courier2);

        PurposeOrderDomainService service = new PurposeOrderDomainServiceImpl();
        Result<Courier, Error> result1 = service.purposeOrder(newOrder, couriers);

        assertTrue(result1.isSuccess());
        assertEquals(3, result1.getValue().getAssignments().size());

        Order newNewOrder = Order.create(ORDER_ID_4, volume4, location4).getValue();

        Result<Courier, Error> result2 = service.purposeOrder(newNewOrder, couriers);

        assertTrue(result2.isSuccess());
        assertEquals(1, result2.getValue().getAssignments().size());
    }

    @Test
    void shouldReturnErrorWhenCouriersNotFound() {
        Volume volume1 = Volume.create(10).getValue();
        Volume volume2 = Volume.create(9).getValue();
        Volume volume3 = Volume.create(2).getValue();

        Location location1 = Location.create(1, 2).getValue();
        Location location2 = Location.create(3, 4).getValue();
        Location location3 = Location.create(1, 3).getValue();

        Order order3 = Order.create(ORDER_ID_3, volume3, location3).getValue();

        Courier courier = Courier.create("Ivan", location1).getValue();
        courier.takeOrder(ORDER_ID_1, volume1, location1).getValue();
        courier.takeOrder(ORDER_ID_2, volume2, location2).getValue();

        List<Courier> couriers = List.of(courier);

        PurposeOrderDomainService service = new PurposeOrderDomainServiceImpl();
        Result<Courier, Error> result = service.purposeOrder(order3, couriers);

        assertTrue(result.isFailure());
    }
}
