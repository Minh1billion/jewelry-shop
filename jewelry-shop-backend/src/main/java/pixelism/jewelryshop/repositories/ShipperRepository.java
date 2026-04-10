package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.entities.Shipper;
import java.util.List;
import java.util.Optional;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    Optional<Shipper> findByPhone(String phone);
    Optional<Shipper> findByEmail(String email);
    List<Shipper> findByStatus(Shipper.ShipperStatus status);
}