package ru.snsin.apisec.user;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.Instant;
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

        SecureRandom random = new SecureRandom();
        byte[] sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);

        JWSSigner signer;
        try {
            signer = new MACSigner(sharedSecret);
        } catch (KeyLengthException e) {
            e.printStackTrace();
            return Collections.singletonMap("error", e.getMessage());
        }
        Instant expirationDate = Instant.now().plusSeconds(600);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("anonymous")
                .issuer("apisec-app")
                .expirationTime(Date.from(expirationDate))
                .claim("roles", Collections.singletonList("USER"))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            e.printStackTrace();
            return Collections.singletonMap("error", e.getMessage());
        }

        return Collections.singletonMap("access_token", signedJWT.serialize());
    }
}
