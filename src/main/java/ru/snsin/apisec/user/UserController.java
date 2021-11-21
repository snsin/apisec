package ru.snsin.apisec.user;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createUser(@RequestBody @Valid UserDto user) {
        final long userId = userService.createUser(user);
        return Collections.singletonMap("id", userId);
    }

    @PostMapping(value = "/authenticate")
    public Map<String, Object> authenticate(@RequestBody @Valid UserDto user) {
        return Collections.singletonMap("token",
                "JWT for %s will be there".formatted(user.getEmail()));
    }
}
