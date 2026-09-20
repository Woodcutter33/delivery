package microarch.delivery.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Volume extends ValueObject<Volume> {

    private static final double MIN_VOLUME = 0.0;

    @Column(name = "volume")
    private final double value;

    public static Result<Volume, Error> create(double volume) {

        Error err = Guard.againstLessOrEqual(volume, MIN_VOLUME, "volume");
        if (err != null)
            return Result.failure(err);

        return Result.success(new Volume(volume));
    }

    public Volume add(Volume addedVolume) {
        Objects.requireNonNull(addedVolume, "addedVolume");

        return new Volume(this.value + addedVolume.value);
    }

    public boolean isGreaterThen(Volume maxVolume) {
        Objects.requireNonNull(maxVolume, "maxVolume");

        return this.value > maxVolume.value;
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(value);
    }
}
