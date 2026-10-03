package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompleteOrderCommandHandlerImpl implements CompleteOrderCommandHandler {
    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public UnitResult<Error> handle(CompleteOrderCommand command) {
        Optional<Courier> courierResult = courierRepository.findById(command.getCourierId());
        if (courierResult.isEmpty())
            return UnitResult.failure(GeneralErrors.notFound("courier", command.getCourierId()));

        Optional<Order> orderResult = orderRepository.findById(command.getOrderId());
        if (orderResult.isEmpty())
            return UnitResult.failure(GeneralErrors.notFound("order", command.getOrderId()));

        Courier courier = courierResult.get();
        Order order = orderResult.get();
        UnitResult<Error> assignmentResult = courier.completeAssigment(order.getId());
        if (assignmentResult.isFailure()) {
            CommandTransactions.rollbackOnFailure();
            return assignmentResult;
        }

        UnitResult<Error> completionResult = order.complete();
        if (completionResult.isFailure()) {
            CommandTransactions.rollbackOnFailure();
            return completionResult;
        }

        courierRepository.update(courier);
        orderRepository.update(order);
        return UnitResult.success();
    }
}
