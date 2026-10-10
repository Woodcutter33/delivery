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
public final class MoveCourierCommand {

    private final UUID courierId;

    private final int x;

    private final int y;

    public static Result<MoveCourierCommand, Error> create(UUID courierId, Integer x, Integer y) {
        Error err = Guard.combine(Guard.againstNullOrEmpty(courierId, "courierId"), Guard.againstLessThan(x, 1, "x"),
                Guard.againstLessThan(y, 1, "y"));
        if (err != null)
            return Result.failure(err);

        return Result.success(new MoveCourierCommand(courierId, x, y));
    }
}
