package pixelism.jewelryshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pixelism.jewelryshop.Shipper;

import java.util.List;
import java.util.Optional;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    List<Shipper> findByShipperStatus(Shipper.ShipperStatus shipperStatus);
    Optional<Shipper> findByPhone(String phone);
    Optional<Shipper> findByEmail(String email);
}