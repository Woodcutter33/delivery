package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Assignment;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurposeOrderDomainServiceImpl implements PurposeOrderDomainService {

    @Override
    public Result<Courier, Error> purposeOrder(Order order, List<Courier> couriers) {
        Error err = Guard.combine(
                Guard.againstNullOrEmpty(order, "order"),
                Guard.againstNullOrEmpty(couriers, "couriers")
        );

        if (err != null)
            return Result.failure(err);

        if (OrderStatus.CREATED != order.getStatus())
            return Result.failure(Errors.notValidStatus(order.getStatus()));

        Optional<Courier> selectedCourier = couriers.stream()
                .filter(courier -> courier.canTakeOrder(order.getVolume()))
                .min(Comparator.comparingInt(
                        courier -> courier.getLocation().distanceTo(order.getLocation())
                ));

        if (selectedCourier.isEmpty())
            return Result.failure(Errors.noFreeCouriers(order.getId()));

        Courier courier = selectedCourier.get();

        Result<Assignment, Error> takeOrderResult = courier.takeOrder(
                order.getId(),
                order.getVolume(),
                order.getLocation()
        );

        if (takeOrderResult.isFailure())
            return Result.failure(takeOrderResult.getError());

        return Result.success(selectedCourier.get());
    }

    public static final class Errors {

        public static Error notValidStatus(OrderStatus status) {
            return Error.of("not.valid.order.tatus", "Order status must be CREATED, but " + status);
        }

        public static Error noFreeCouriers(UUID orderId) {
            return Error.of("no.free.couriers", "Free couriers not found for orderId " + orderId);
        }
    }
}
