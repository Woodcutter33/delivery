package libs.ddd;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredOnUtc;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOnUtc = Instant.now();
    }
}