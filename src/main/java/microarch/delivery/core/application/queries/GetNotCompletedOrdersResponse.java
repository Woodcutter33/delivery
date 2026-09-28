package microarch.delivery.core.application.queries;

import java.util.UUID;

public record GetNotCompletedOrdersResponse(UUID orderId, int x, int y) {
}
