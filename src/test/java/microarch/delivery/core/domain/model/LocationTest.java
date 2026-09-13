package microarch.delivery.core.domain.model;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void derivedAggregate() {
        assertThat(Location.class.getSuperclass().getSimpleName()).isEqualTo("ValueObject");
    }

    @Test
    void shouldCreateLocationWhenCorrectValue() {
        Location result = Location.create(1, 2).getValue();

        assertNotNull(result);
        assertEquals(1, result.getX());
        assertEquals(2, result.getY());
    }

    @Test
    void shouldReturnErrorWhenIncorrectValue() {
        Result<Location, Error> result = Location.create(-1, 0);

        assertNotNull(result);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isNotNull();
    }

    @Test
    void shouldBeEqualWhenLocationsHaveSameCoordinates() {
        Location location1 = Location.create(4, 9).getValue();
        Location location2 = Location.create(4, 9).getValue();

        assertEquals(location1, location2);
    }

    @Test
    void shouldNotBeEqualWhenLocationsHaveDifferentCoordinates() {
        Location location1 = Location.create(4, 9).getValue();
        Location location2 = Location.create(2, 6).getValue();

        assertNotEquals(location1, location2);
    }

    @Test
    void shouldReturnDistanceWhenCalculateRout() {
        Location startPoint = Location.create(4, 9).getValue();
        Location finishPoint = Location.create(2, 6).getValue();

        int result = startPoint.distanceTo(finishPoint);

        assertEquals(5, result);
    }

}
