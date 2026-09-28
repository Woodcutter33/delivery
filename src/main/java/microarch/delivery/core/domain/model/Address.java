package microarch.delivery.core.domain.model;

import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Address extends ValueObject<Address> {

    private final String country;

    private final String city;

    private final String street;

    private final String house;

    private final String apartment;

    public static Result<Address, Error> create(String country, String city, String street, String house,
            String apartment) {
        Error err = Guard.combine(Guard.againstNullOrEmpty(country, "country"), Guard.againstNullOrEmpty(city, "city"),
                Guard.againstNullOrEmpty(street, "street"), Guard.againstNullOrEmpty(house, "house"),
                Guard.againstNullOrEmpty(apartment, "apartment"));
        if (err != null)
            return Result.failure(err);

        return Result.success(new Address(country, city, street, house, apartment));
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(country, city, street, house, apartment);
    }
}
