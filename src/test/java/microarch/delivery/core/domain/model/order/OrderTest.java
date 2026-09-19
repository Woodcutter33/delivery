package microarch.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderTest {

    private static final UUID ORDER_ID = UUID.randomUUID();

    @Test
    void derivedAggregate() {
        assertThat(Order.class.getSuperclass().getSimpleName()).isEqualTo("Aggregate");
    }

    @Test
    void shouldCreateOrderWhenCorrectValue() {
        Volume volume = Volume.create(3).getValue();
        Location location = Location.create(3, 4).getValue();

        Order result = Order.create(ORDER_ID, volume, location).getValue();

        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
        assertEquals(3, result.getVolume().getValue());
    }

    @Test
    void shouldReturnErrorWhenIncorrectValue() {
        Volume volume = Volume.create(3).getValue();
        Location location = Location.create(3, 4).getValue();

        Result<Order, Error> result = Order.create(null, volume, location);

        assertNotNull(result);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldAssignThenCompleteWhenOrderStatusCorrect() {
        Volume volume = Volume.create(3).getValue();
        Location location = Location.create(3, 4).getValue();

        Order result = Order.create(ORDER_ID, volume, location).getValue();
        result.assign();

        assertNotNull(result);
        assertEquals(OrderStatus.ASSIGNED, result.getStatus());

        result.complete();

        assertEquals(OrderStatus.COMPLETED, result.getStatus());
    }

}
