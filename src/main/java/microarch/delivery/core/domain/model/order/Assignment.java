package microarch.delivery.core.domain.model.order;

import jakarta.persistence.*;
import libs.ddd.BaseEntity;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "assigment")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Assignment extends BaseEntity<UUID> {

    @Getter
    @Column(name = "order_Id", nullable = false)
    private final UUID orderId;

    @Getter
    @Embedded
    @Column(name = "volume", nullable = false)
    private final Volume volume;

    @Getter
    @Embedded
    @Column(name = "location")
    private final Location location;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssignmentStatus status;

    private Assignment(UUID id, UUID orderId, Volume volume, Location location) {
        super(id);
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = AssignmentStatus.ASSIGNED;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {
        Error err = Guard.combine(Guard.againstNullOrEmpty(orderId, "orderId"),
                Guard.againstNullOrEmpty(volume, "volume"), Guard.againstNullOrEmpty(location, "location"));

        if (err != null)
            return Result.failure(err);

        return Result.success(new Assignment(UUID.randomUUID(), orderId, volume, location));
    }

    public UnitResult<Error> complete(Location courierLocation) {

        Objects.requireNonNull(courierLocation, "courierLocation");

        if (status == AssignmentStatus.COMPLETED) {
            return UnitResult.failure(Errors.assignmentAlreadyCompleted());
        }

        int distance = location.distanceTo(courierLocation);

        if (distance > 1) {
            return UnitResult.failure(Errors.courierTooFar(distance));
        }

        this.status = AssignmentStatus.COMPLETED;

        return UnitResult.success();
    }

    public static final class Errors {

        public static Error assignmentAlreadyCompleted() {
            return Error.of("assignment.already.completed", "Assignment is already completed");
        }

        public static Error courierTooFar(int distance) {
            return Error.of("assignment.courier.too.far",
                    "Courier is too far from order location. Distance: " + distance);
        }
    }
}
