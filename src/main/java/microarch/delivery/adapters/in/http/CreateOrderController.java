package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.CreateOrderApi;
import microarch.delivery.adapters.in.http.model.CreateOrderResponse;
import microarch.delivery.adapters.in.http.model.NewOrder;
import microarch.delivery.core.application.commands.CreateOrderCommand;
import microarch.delivery.core.application.commands.CreateOrderCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateOrderController implements CreateOrderApi {

    private final CreateOrderCommandHandler commandHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(NewOrder newOrder) {
        Result<CreateOrderCommand, Error> createOrderResponseErrorResult = CreateOrderCommand.create(newOrder.getId(),
                newOrder.getAddress().getCountry(), newOrder.getAddress().getCity(), newOrder.getAddress().getStreet(),
                newOrder.getAddress().getHouse(), newOrder.getAddress().getApartment(), newOrder.getVolume());
        if (createOrderResponseErrorResult.isFailure())
            return ResponseEntity.badRequest().build();

        CreateOrderCommand command = createOrderResponseErrorResult.getValue();

        Result<UUID, Error> handle = commandHandler.handle(command);
        if (handle.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        CreateOrderResponse response = new CreateOrderResponse(handle.getValue());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
