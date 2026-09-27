package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository repository;

    @Override
    @Transactional
    public Result<UUID, Error> handle(CreateCourierCommand command) {

        Location location = Location.create(1, 1).getValue();

        Result<Courier, Error> courierCreateResult = Courier.create(command.getName(), location);
        if (courierCreateResult.isFailure())
            return Result.failure(courierCreateResult.getError());

        repository.save(courierCreateResult.getValue());

        return Result.success(courierCreateResult.getValue().getId());

    }
}
