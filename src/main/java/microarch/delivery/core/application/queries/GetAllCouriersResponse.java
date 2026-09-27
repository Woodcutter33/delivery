package microarch.delivery.core.application.queries;

import microarch.delivery.core.domain.model.Location;

import java.util.UUID;

public record GetAllCouriersResponse(UUID courierId, String courierName, Location courierLocation) {
}
