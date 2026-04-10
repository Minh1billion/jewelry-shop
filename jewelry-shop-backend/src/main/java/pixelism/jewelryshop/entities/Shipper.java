package pixelism.jewelryshop.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "shippers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipperStatus status;

    @OneToMany(mappedBy = "shipper", fetch = FetchType.LAZY)
    private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) this.status = ShipperStatus.ACTIVE;
    }

    public enum ShipperStatus {
        ACTIVE, INACTIVE
    }
}