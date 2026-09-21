package microarch.delivery.core.domain.model.order;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Assignment;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AssignmentTest {

    private static final UUID ORDER_ID = UUID.randomUUID();

    @Test
    void derivedAggregate() {
        assertThat(Assignment.class.getSuperclass().getSimpleName()).isEqualTo("BaseEntity");
    }

    @Test
    void shouldCreateAssignmentWhenCorrectValue() {
        Volume volume = Volume.create(3).getValue();
        Location location = Location.create(3, 4).getValue();

        Assignment result = Assignment.create(ORDER_ID, volume, location).getValue();

        assertNotNull(result);
    }

    @Test
    void shouldReturnErrorWhenIncorrectValue() {
        Volume volume = Volume.create(3).getValue();
        Location location = Location.create(3, 4).getValue();

        Result<Assignment, Error> result = Assignment.create(null, volume, location);

        assertNotNull(result);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldCompleteWhenMinDistance() {
        Location courierLocation = Location.create(3, 6).getValue();
        Location targetLocation = Location.create(3, 5).getValue();
        Volume volume = Volume.create(3).getValue();
        Assignment assignment = Assignment.create(ORDER_ID, volume, courierLocation).getValue();

        UnitResult<Error> complete = assignment.complete(targetLocation);

        assertNotNull(complete);
        assertThat(complete.isSuccess()).isTrue();
    }

    @Test
    void shouldReturnFailureWhenBigDistance() {
        Location courierLocation = Location.create(1, 6).getValue();
        Location targetLocation = Location.create(3, 5).getValue();
        Volume volume = Volume.create(3).getValue();
        Assignment assignment = Assignment.create(ORDER_ID, volume, courierLocation).getValue();

        UnitResult<Error> complete = assignment.complete(targetLocation);

        assertNotNull(complete);
        assertThat(complete.isFailure()).isTrue();
    }
}
