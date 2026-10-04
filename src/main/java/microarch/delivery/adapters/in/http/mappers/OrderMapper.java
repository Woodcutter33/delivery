package microarch.delivery.adapters.in.http.mappers;

import microarch.delivery.core.application.queries.GetNotCompletedOrdersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", source = "orderId")
    @Mapping(target = "location.x", source = "x")
    @Mapping(target = "location.y", source = "y")
    microarch.delivery.adapters.in.http.model.Order toOrder(GetNotCompletedOrdersResponse source);

    List<microarch.delivery.adapters.in.http.model.Order> toOrders(List<GetNotCompletedOrdersResponse> source);
}
