package pixelism.jewelryshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import pixelism.jewelryshop.repositories.CartRepository;
import pixelism.jewelryshop.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("USER")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;
    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserBehavior> behaviors;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Role {
        USER, ADMIN
    }

    public User register(String username, String email, String password, String fullName, String phone,
                         UserRepository userRepository, CartRepository cartRepository) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email already exists");

        User user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .fullName(fullName)
                .phone(phone)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        cartRepository.save(Cart.builder().user(user).build());
        return userRepository.save(user);
    }

    public User login(String username, String password, UserRepository userRepository) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getPassword().equals(password))
            throw new RuntimeException("Invalid password");
        return user;
    }
}