package microarch.delivery.core.application.queries;

import java.util.UUID;

public record GetAllCouriersResponse(UUID courierId, String courierName, int x, int y) {
}
