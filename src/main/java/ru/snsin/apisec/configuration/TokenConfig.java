package ru.snsin.apisec.configuration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.snsin.apisec.authentication.TokenParams;

import java.security.SecureRandom;

@Configuration
public class TokenConfig {

    @Bean
    public TokenParams authTokenParams() {
        SecureRandom random = new SecureRandom();
        byte[] sharedSecret = new byte[32];
        random.nextBytes(sharedSecret);

        JWSSigner signer;
        try {
            signer = new MACSigner(sharedSecret);
        } catch (KeyLengthException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return TokenParams.builder()
                .signer(signer)
                .algorithm(JWSAlgorithm.HS256)
                .duration(600)
                .issuer("apisec-app")
                .build();
    }
}
