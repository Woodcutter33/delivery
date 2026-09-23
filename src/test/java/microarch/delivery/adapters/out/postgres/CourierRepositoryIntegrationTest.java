package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourierRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    CourierRepository repository;

    @Test
    void shouldSaveCourierAndGetById() {
        Location location = Location.create(2, 3).getValue();
        Courier courier = Courier.create("Ivan", location).getValue();

        repository.save(courier);

        Optional<Courier> loaded = repository.findById(courier.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getLocation()).isEqualTo(location);
        assertThat(loaded.get().getName()).isEqualTo("Ivan");
    }

    @Test
    void shouldUpdateCourierAndGetAll() {
        Location location1 = Location.create(1, 2).getValue();
        Courier courier1 = Courier.create("Ivan", location1).getValue();

        Location location2 = Location.create(3, 4).getValue();
        Courier courier2 = Courier.create("Boris", location2).getValue();

        repository.save(courier1);
        repository.save(courier2);

        Optional<Courier> courierToUpdateOpt = repository.findById(courier2.getId());
        Courier courierToUpdate = courierToUpdateOpt.get();
        Location location3 = Location.create(4, 4).getValue();
        courierToUpdate.moveTo(location3);

        repository.update(courierToUpdate);

        List<Courier> couriers = repository.findAll();

        assertEquals(2, couriers.size());
        assertEquals(location3, couriers.get(1).getLocation());
    }

}