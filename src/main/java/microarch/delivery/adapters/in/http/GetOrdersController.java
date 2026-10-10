package microarch.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import microarch.delivery.adapters.in.http.api.GetOrdersApi;
import microarch.delivery.adapters.in.http.mappers.OrderMapper;
import microarch.delivery.adapters.in.http.model.Order;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersQueryHandler;
import microarch.delivery.core.application.queries.GetNotCompletedOrdersResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetOrdersController implements GetOrdersApi {

    private final GetNotCompletedOrdersQueryHandler queryHandler;
    private final OrderMapper mapper;

    @Override
    public ResponseEntity<List<Order>> getOrders() {

        Result<List<GetNotCompletedOrdersResponse>, Error> handle = queryHandler.handle();
        if (handle.isFailure())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        List<GetNotCompletedOrdersResponse> notCompletedOrders = handle.getValue();
        if (notCompletedOrders.isEmpty())
            ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<Order> orderList = mapper.toOrders(notCompletedOrders);

        return ResponseEntity.ok(orderList);
    }
}
