package microarch.delivery.core.domain.model.order;

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

import java.util.UUID;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Order extends Aggregate<UUID> {

    @Embedded
    private final Volume volume;

    @Embedded
    private final Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    private Order(UUID id, Volume volume, Location location) {
        super(id);
        this.volume = volume;
        this.location = location;
        this.status = OrderStatus.CREATED;
    }

    public static Result<Order, Error> create(UUID id, Volume volume, Location location) {
        Error err = Guard.combine(Guard.againstNullOrEmpty(id, "id"), Guard.againstNullOrEmpty(volume, "volume"),
                Guard.againstNullOrEmpty(location, "location"));

        if (err != null)
            return Result.failure(err);

        return Result.success(new Order(id, volume, location));
    }

    public UnitResult<Error> assign() {
        if (status != OrderStatus.CREATED)
            return UnitResult.failure(Errors.cannotAssign(status));

        status = OrderStatus.ASSIGNED;

        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (status != OrderStatus.ASSIGNED)
            return UnitResult.failure(Errors.cannotComplete(status));

        status = OrderStatus.COMPLETED;

        return UnitResult.success();
    }

    public static final class Errors {

        public static Error cannotAssign(OrderStatus currentStatus) {
            return Error.of("order.cannot.assign",
                    "Order can be assigned only from CREATED status. Current status: " + currentStatus);
        }

        public static Error cannotComplete(OrderStatus currentStatus) {
            return Error.of("order.cannot.complete",
                    "Order can be completed only from ASSIGNED status. Current status: " + currentStatus);
        }
    }

}
