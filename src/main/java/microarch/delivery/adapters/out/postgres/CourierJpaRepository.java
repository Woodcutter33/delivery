package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierJpaRepository extends JpaRepository<Courier, UUID> {

    Optional<Courier> findById(UUID id);

    List<Courier> findAll();

}
