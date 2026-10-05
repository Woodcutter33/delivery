package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetCouriersApi;
import microarch.delivery.adapters.in.http.mappers.CourierMapper;
import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.core.application.queries.GetAllCouriersQueryHandler;
import microarch.delivery.core.application.queries.GetAllCouriersResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetCouriersController implements GetCouriersApi {

    private final GetAllCouriersQueryHandler queryHandler;
    private final CourierMapper mapper;

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {

        Result<List<GetAllCouriersResponse>, Error> handle = queryHandler.handle();
        if (handle.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        List<GetAllCouriersResponse> allCouriersResponses = handle.getValue();

        List<Courier> couriersList = mapper.toCouriers(allCouriersResponses);

        return ResponseEntity.ok(couriersList);
    }
}
