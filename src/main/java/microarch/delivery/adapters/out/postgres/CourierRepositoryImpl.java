package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CourierRepositoryImpl implements CourierRepository {

    private final CourierJpaRepository jpa;

    public CourierRepositoryImpl(CourierJpaRepository jpa) {
        this.jpa = jpa;
    }

    /**
     * Методы save и update в текущей реализации не подразумевают нескольких инстансов приложения
     */
    @Override
    public void save(Courier courier) {
        jpa.save(courier);
    }

    @Override
    public void update(Courier courier) {
        jpa.save(courier);
    }

    @Override
    public Optional<Courier> findById(UUID courierId) {
        return jpa.findById(courierId);
    }

    @Override
    public List<Courier> findAll() {
        return jpa.findAll();
    }
}
