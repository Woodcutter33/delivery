package microarch.delivery.core.domain.model.order.event;

import libs.ddd.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;

import java.util.UUID;

@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public final class OrderCompletedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final OrderStatus status;

    public OrderCompletedDomainEvent(Order order) {
        this.orderId = order.getId();
        this.status = order.getStatus();
    }

}
