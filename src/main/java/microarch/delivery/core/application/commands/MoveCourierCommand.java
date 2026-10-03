package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.Location;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveCourierCommand {

    private final UUID courierId;

    private final Location location;

    public static Result<MoveCourierCommand, Error> create(UUID courierId, Location location) {
        Error err = Guard.combine(Guard.againstNullOrEmpty(courierId, "courierId"),
                Guard.againstNullOrEmpty(location, "location"));
        if (err != null)
            return Result.failure(err);

        return Result.success(new MoveCourierCommand(courierId, location));
    }
}
