package pixelism.jewelryshop.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pixelism.jewelryshop.entities.Cart;
import pixelism.jewelryshop.entities.User;
import pixelism.jewelryshop.repositories.CartRepository;
import pixelism.jewelryshop.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public User register(String username, String email, String password, String fullName, String phone) {
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
                .role(User.Role.USER)
                .build();

        user = userRepository.save(user);

        Cart cart = Cart.builder()
                .user(user)
                .build();

        cartRepository.save(cart);

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password))
            throw new RuntimeException("Invalid password");

        return user;
    }
}