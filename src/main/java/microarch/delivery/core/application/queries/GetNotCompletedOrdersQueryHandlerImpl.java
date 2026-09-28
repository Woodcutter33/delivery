package microarch.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetNotCompletedOrdersQueryHandlerImpl implements GetNotCompletedOrdersQueryHandler {

    private final OrderRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Result<List<GetNotCompletedOrdersResponse>, Error> handle() {

        List<GetNotCompletedOrdersResponse> ordersResponses = repository.findNotCompleted().stream()
                .map(order -> new GetNotCompletedOrdersResponse(order.getId(), order.getLocation().getX(),
                        order.getLocation().getY()))
                .toList();

        return Result.success(ordersResponses);
    }
}
