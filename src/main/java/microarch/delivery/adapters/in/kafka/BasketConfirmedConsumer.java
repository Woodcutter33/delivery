package microarch.delivery.adapters.in.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.commands.CreateOrderCommand;
import microarch.delivery.core.application.commands.CreateOrderCommandHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import queues.basket.events.BasketEventsProto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasketConfirmedConsumer {

    private final CreateOrderCommandHandler handler;

    @KafkaListener(topics = "${app.kafka.basket-events-topic}")
    public void listen(byte[] message) {


        try {
            BasketEventsProto.BasketConfirmedIntegrationEvent event = BasketEventsProto.BasketConfirmedIntegrationEvent.parseFrom(message);

            Result<CreateOrderCommand, Error> commandResult = CreateOrderCommand.create(
                    UUID.fromString(event.getBasketId()),
                    event.getAddress().getCountry(),
                    event.getAddress().getCity(),
                    event.getAddress().getStreet(),
                    event.getAddress().getHouse(),
                    event.getAddress().getApartment(),
                    event.getVolume()
            );

            if (commandResult.isFailure())
                throw new RuntimeException("Invalid command: " + commandResult.getError());

            CreateOrderCommand command = commandResult.getValue();

            Result<UUID, Error> handleResult = handler.handle(command);
            if (handleResult.isFailure())
                throw new RuntimeException("Failed to handle command: " + handleResult.getError());

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Failed to parse protobuf message", e);
        }
    }
}
