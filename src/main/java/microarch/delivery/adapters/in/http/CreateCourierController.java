package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateCourierApi;
import microarch.delivery.adapters.in.http.model.CreateCourierResponse;
import microarch.delivery.adapters.in.http.model.NewCourier;
import microarch.delivery.core.application.commands.CreateCourierCommand;
import microarch.delivery.core.application.commands.CreateCourierCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateCourierController implements CreateCourierApi {

    private final CreateCourierCommandHandler commandHandler;

    @Override
    public ResponseEntity<CreateCourierResponse> createCourier(NewCourier newCourier) {
        Result<CreateCourierCommand, Error> createCourierCommandResult = CreateCourierCommand
                .create(newCourier.getName());
        if (createCourierCommandResult.isFailure())
            return ResponseEntity.badRequest().build();

        CreateCourierCommand command = createCourierCommandResult.getValue();
        Result<UUID, Error> handle = commandHandler.handle(command);

        if (handle.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        CreateCourierResponse response = new CreateCourierResponse(handle.getValue());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
