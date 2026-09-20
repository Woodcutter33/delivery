package microarch.delivery.core.domain.model.courier;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Assignment;
import microarch.delivery.core.domain.model.order.AssignmentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CourierTest {

    private static final UUID ORDER_ID = UUID.randomUUID();

    @Test
    void derivedAggregate() {
        assertThat(Courier.class.getSuperclass().getSimpleName()).isEqualTo("Aggregate");
    }

    @Test
    void shouldCreateCourierWhenCorrectValue() {
        Location location = Location.create(3, 4).getValue();

        Courier result = Courier.create("John", location).getValue();

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(20, result.getVolume().getValue());
    }

    @Test
    void shouldReturnErrorWhenIncorrectValue() {
        Result<Courier, Error> result = Courier.create("John", null);

        assertNotNull(result);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldReturnTrueWhenVolumeLessMax() {
        Location location = Location.create(3, 4).getValue();
        Volume volume = Volume.create(10).getValue();
        Assignment assignment = Assignment.create(ORDER_ID, volume, location).getValue();

        Courier result = Courier.create("John", location).getValue();
        boolean isTakeOrder = result.canTakeOrder(assignment.getVolume());

        assertNotNull(result);
        Assertions.assertTrue(isTakeOrder);
    }

    @Test
    void shouldReturnFalseWhenVolumeBiggerMax() {
        Location location = Location.create(3, 4).getValue();
        Volume volume = Volume.create(30).getValue();
        Assignment assignment = Assignment.create(ORDER_ID, volume, location).getValue();

        Courier result = Courier.create("John", location).getValue();
        boolean isTakeOrder = result.canTakeOrder(assignment.getVolume());

        assertNotNull(result);
        Assertions.assertFalse(isTakeOrder);
    }

    @Test
    void shouldTakeNewOrderWhenVolumeLessMax() {
        Location location = Location.create(3, 4).getValue();
        Volume volume = Volume.create(10).getValue();
        Courier courier = Courier.create("John", location).getValue();

        Assignment result = courier.takeOrder(ORDER_ID, volume, location).getValue();

        assertNotNull(result);
        assertEquals(ORDER_ID, result.getOrderId());
        assertEquals(volume, result.getVolume());
        assertEquals(location, result.getLocation());
    }

    @Test
    void shouldCompleteAssigment() {
        Location location1 = Location.create(3, 4).getValue();
        Volume volume1 = Volume.create(10).getValue();

        UUID orderId2 = UUID.randomUUID();
        Location location2 = Location.create(1, 4).getValue();
        Volume volume2 = Volume.create(2).getValue();

        Courier courier = Courier.create("John", location1).getValue();

        Assignment result1 = courier.takeOrder(ORDER_ID, volume1, location1).getValue();
        Assignment result2 = courier.takeOrder(orderId2, volume2, location2).getValue();

        assertEquals(2, courier.getAssignments().size());

        courier.completeAssigment(result1.getOrderId());
        courier.completeAssigment(result1.getOrderId());

        assertEquals(AssignmentStatus.COMPLETED, result1.getStatus());
        assertEquals(AssignmentStatus.ASSIGNED, result2.getStatus());
    }

    @Test
    void shouldMoveToOneStep() {
        Location location = Location.create(3, 4).getValue();
        Courier courier = Courier.create("John", location).getValue();
        Location newLocation = Location.create(4, 4).getValue();

        courier.moveTo(newLocation);

        assertEquals(newLocation, courier.getLocation());
    }

}
