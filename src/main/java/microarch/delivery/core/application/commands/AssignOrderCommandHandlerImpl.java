package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.services.PurposeOrderDomainService;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignOrderCommandHandlerImpl implements AssignOrderCommandHandler {
    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final PurposeOrderDomainService service;

    @Override
    @Transactional
    public UnitResult<Error> handle() {
        Optional<Order> createdOrder = orderRepository.findCreated();
        if (createdOrder.isEmpty()) {
            return UnitResult
                    .failure(Error.of("Orders.With.Status.Created.Not.Found", "Orders with status created not found"));
        }

        Order order = createdOrder.get();
        Result<Courier, Error> selectedCourier = service.purposeOrder(order, courierRepository.findAll());
        if (selectedCourier.isFailure()) {
            CommandTransactions.rollbackOnFailure();
            return UnitResult.failure(selectedCourier.getError());
        }

        courierRepository.update(selectedCourier.getValue());
        orderRepository.update(order);
        return UnitResult.success();
    }
}
