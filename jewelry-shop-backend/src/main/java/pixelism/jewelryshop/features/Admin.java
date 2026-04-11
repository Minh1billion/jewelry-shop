package pixelism.jewelryshop.features;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Getter @Setter
public class Admin extends User {
}