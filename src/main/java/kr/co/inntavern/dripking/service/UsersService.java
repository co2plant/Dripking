package kr.co.inntavern.dripking.service;

import kr.co.inntavern.dripking.model.Users;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {
    private static final String EXISTING_EMAIL = "test@test.com";

    public Optional<Users> findByEmail(String email) {
        if (! EXISTING_EMAIL.equalsIgnoreCase(email)) return Optional.empty();

        var user = new Users();
        user.setId(1L);
        user.setEmail(EXISTING_EMAIL);
        user.setPassword("$2a$12$phGOFjE6gXYMWSOgSj2qFe6CuYhH7v5KWF8mmyp01FGXJ4KtfSSxi"); // test
        user.setRole("ROLE_ADMIN");
        return Optional.of(user);
    }
}
