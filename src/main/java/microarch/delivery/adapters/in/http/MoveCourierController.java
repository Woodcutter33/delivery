package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.MoveCourierApi;
import microarch.delivery.adapters.in.http.model.Location;
import microarch.delivery.core.application.commands.MoveCourierCommand;
import microarch.delivery.core.application.commands.MoveCourierCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MoveCourierController implements MoveCourierApi {

    private final MoveCourierCommandHandler commandHandler;

    @Override
    public ResponseEntity<Void> moveCourier(UUID courierId, Location location) {

        Result<microarch.delivery.core.domain.model.Location, Error> locationVOResult = microarch.delivery.core.domain.model.Location
                .create(location.getX(), location.getY());
        if (locationVOResult.isFailure())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        microarch.delivery.core.domain.model.Location locationVO = locationVOResult.getValue();

        Result<MoveCourierCommand, Error> moveCourierCommandResult = MoveCourierCommand.create(courierId, locationVO);
        if (moveCourierCommandResult.isFailure())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        MoveCourierCommand command = moveCourierCommandResult.getValue();
        UnitResult<Error> handle = commandHandler.handle(command);
        if (handle.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.ok().build();
    }
}
