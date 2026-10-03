package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllCouriersQueryHandlerImpl implements GetAllCouriersQueryHandler {
    private final CourierRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Result<List<GetAllCouriersResponse>, Error> handle() {
        List<GetAllCouriersResponse> allCouriersResponse = repository.findAll().stream()
                .map(courier -> new GetAllCouriersResponse(courier.getId(), courier.getName(),
                        courier.getLocation().getX(), courier.getLocation().getY()))
                .toList();

        return Result.success(allCouriersResponse);
    }
}
