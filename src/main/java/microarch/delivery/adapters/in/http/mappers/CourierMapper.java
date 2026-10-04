package microarch.delivery.adapters.in.http.mappers;

import microarch.delivery.adapters.in.http.model.Courier;
import microarch.delivery.core.application.queries.GetAllCouriersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourierMapper {

    @Mapping(target = "id", source = "courierId")
    @Mapping(target = "name", source = "courierName")
    @Mapping(target = "location.x", source = "x")
    @Mapping(target = "location.y", source = "y")
    Courier toCourier(GetAllCouriersResponse source);

    List<Courier> toCouriers(List<GetAllCouriersResponse> source);
}
