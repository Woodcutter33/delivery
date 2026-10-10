package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoveCourierCommandHandlerImpl implements MoveCourierCommandHandler {

    private final CourierRepository repository;

    @Override
    @Transactional
    public UnitResult<Error> handle(MoveCourierCommand command) {

        Result<Location, Error> locationResult = Location.create(command.getX(), command.getY());
        if (locationResult.isFailure())
            return UnitResult.failure(locationResult.getError());

        Optional<Courier> courierOpt = repository.findById(command.getCourierId());

        if (courierOpt.isPresent()) {
            Courier courier = courierOpt.get();
            UnitResult<Error> moveResult = courier.moveTo(locationResult.getValue());

            if (moveResult.isFailure())
                return moveResult;

            repository.update(courier);
            return UnitResult.success();

        } else {

            return UnitResult.failure(GeneralErrors.notFound("courier", command.getCourierId()));
        }
    }
}
