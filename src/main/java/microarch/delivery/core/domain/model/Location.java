package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {

    private static final int MAX_VALUE = 10;
    private static final int MIN_VALUE = 1;

    @Column(name = "coordinate_x")
    private final int x;
    @Column(name = "coordinate_y")
    private final int y;

    public static Result<Location, Error> create(int x, int y) {
        Error err = Guard.combine(Guard.againstOutOfRange(x, MIN_VALUE, MAX_VALUE, "x"),
                Guard.againstOutOfRange(y, MIN_VALUE, MAX_VALUE, "y"));
        if (err != null)
            return Result.failure(err);

        return Result.success(new Location(x, y));
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }

    public int distanceTo(Location target) {
        Objects.requireNonNull(target, "target");

        int stepsX = Math.abs(target.x - this.x);
        int stepsY = Math.abs(target.y - this.y);

        return stepsX + stepsY;
    }

}
