package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {

    private final UUID orderID;

    private final String country;

    private final String city;

    private final String street;

    private final String house;

    private final String apartment;

    private final int volume;

    public static Result<CreateOrderCommand, Error> create(UUID orderID, String country, String city, String street,
            String house, String apartment, int volume) {
        Error err = Guard.combine(Guard.againstNullOrEmpty(orderID, "orderId"),
                Guard.againstLessOrEqual(volume, 0, "volume"));
        if (err != null)
            return Result.failure(err);

        return Result.success(new CreateOrderCommand(orderID, country, city, street, house, apartment, volume));

    }
}
