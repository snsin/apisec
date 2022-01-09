package ru.snsin.apisec.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.snsin.apisec.authentication.TokenService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createUser(@RequestBody @Valid UserDto user) {
        final long userId = userService.createUser(user);
        return Collections.singletonMap("id", userId);
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody @Valid UserDto user) {
        final var userProperties = userService.getUserProperties(user);
        var accessToken = tokenService.issueAccessToken(userProperties.name(),
                userProperties.claims());
        Map<String, Object> body = Collections.singletonMap("access_token", accessToken);
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(accessToken));
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
