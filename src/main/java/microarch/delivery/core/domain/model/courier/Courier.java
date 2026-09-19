package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.*;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Assignment;
import microarch.delivery.core.domain.model.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "couriers")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Courier extends Aggregate<UUID> {

    private static final double MAX_VOLUME = 20.0;

    @Column(name = "name", nullable = false)
    private final String name;

    @Embedded
    private Location location;

    @Embedded
    private Volume volume;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private final List<Assignment> assignments = new ArrayList<>();

    private Courier(UUID id, String name, Location location) {
        super(id);
        this.name = name;
        this.location = location;
        this.volume = Volume.create(MAX_VOLUME).getValue();
    }

    public static Result<Courier, Error> create(String name, Location location) {
        Error err = Guard.combine(Guard.againstNullOrEmpty(name, "name"),
                Guard.againstNullOrEmpty(location, "location"));

        if (err != null)
            return Result.failure(err);

        return Result.success(new Courier(UUID.randomUUID(), name, location));
    }

    public boolean canTakeOrder(Volume newOrderVolume) {
        Objects.requireNonNull(newOrderVolume, "newOrderVolume");

        double currentVolume = assignments.stream().filter(Assignment::isActive)
                .mapToDouble(assignment -> assignment.getVolume().getValue()).sum();

        return currentVolume + newOrderVolume.getValue() < volume.getValue();
    }

    Result<Assignment, Error> takeOrder(Order order) {
        Objects.requireNonNull(order, "order");

        if (!canTakeOrder(order.getVolume())) {
            return Result.failure(Errors.notEnoughCapacity());
        }

        Assignment assignment = Assignment.create(order.getId(), order.getVolume(), order.getLocation()).getValue();

        assignments.add(assignment);

        return Result.success(assignment);
    }

    public UnitResult<Error> completeAssigment(UUID assigmentId) {
        Objects.requireNonNull(assigmentId, "assigmentId");

        Assignment assignment = assignments.stream().filter(a -> a.getId().equals(assigmentId)).findFirst()
                .orElse(null);

        if (assignment == null)
            return UnitResult.failure(Errors.assignmentNotFound(assigmentId));

        return assignment.complete(location);
    }

    public UnitResult<Error> moveTo(Location newLocation) {
        Objects.requireNonNull(newLocation, "newLocation");

        int distance = location.distanceTo(newLocation);

        if (distance > 1) {
            return UnitResult.failure(Errors.cannotMoveThatFar(distance));
        }

        this.location = newLocation;

        return UnitResult.success();
    }

    public static final class Errors {

        public static Error notEnoughCapacity() {
            return Error.of("courier.not.enough.capacity", "Courier does not have enough capacity");
        }

        public static Error assignmentNotFound(UUID assignmentId) {
            return Error.of("courier.assignment.not.found", "Assignment not found: " + assignmentId);
        }

        public static Error cannotMoveThatFar(int distance) {
            return Error.of("courier.cannot.move.that.far", "Courier can move only one step. Distance: " + distance);
        }
    }

}
