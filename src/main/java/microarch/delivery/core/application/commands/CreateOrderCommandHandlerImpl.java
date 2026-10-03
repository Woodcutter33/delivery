package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository repository;

    @Override
    @Transactional
    public Result<UUID, Error> handle(CreateOrderCommand command) {
        Optional<Order> orderOpt = repository.findById(command.getOrderID());
        if (orderOpt.isEmpty()) {

            // Временная локация до интеграции с geo-service
            Location location = Location
                    .create(ThreadLocalRandom.current().nextInt(1, 11), ThreadLocalRandom.current().nextInt(1, 11))
                    .getValue();

            Result<Order, Error> orderCreateResult = Order.create(command.getOrderID(), command.getVolume(), location);

            if (orderCreateResult.isFailure())
                return Result.failure(orderCreateResult.getError());

            repository.save(orderCreateResult.getValue());
            return Result.success(orderCreateResult.getValue().getId());
        }

        return Result.success(orderOpt.get().getId());
    }
}
