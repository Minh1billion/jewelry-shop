package pixelism.jewelryshop;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Getter @Setter
public class Admin extends User {
}