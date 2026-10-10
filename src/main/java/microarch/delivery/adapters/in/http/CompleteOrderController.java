package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CompleteOrderApi;
import microarch.delivery.core.application.commands.CompleteOrderCommand;
import microarch.delivery.core.application.commands.CompleteOrderCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CompleteOrderController implements CompleteOrderApi {

    private final CompleteOrderCommandHandler commandHandler;

    @Override
    public ResponseEntity<Void> completeOrder(UUID courierId, UUID orderId) {
        Result<CompleteOrderCommand, Error> completeOrderCommandResult = CompleteOrderCommand.create(courierId,
                orderId);
        if (completeOrderCommandResult.isFailure())
            return ResponseEntity.badRequest().build();

        CompleteOrderCommand command = completeOrderCommandResult.getValue();
        UnitResult<Error> handle = commandHandler.handle(command);
        if (handle.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        return ResponseEntity.ok().build();
    }
}
