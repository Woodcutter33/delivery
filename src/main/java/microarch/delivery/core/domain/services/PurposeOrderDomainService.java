package microarch.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import java.util.List;

public interface PurposeOrderDomainService {

    Result<Courier, Error> purposeOrder (Order order, List<Courier> couriers);
}
