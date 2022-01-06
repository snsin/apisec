package ru.snsin.apisec.authentication;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenParams authTokenParams;

    @Override
    public Map<String, Object> issueToken(String subject, List<String> claims) {

        if (!StringUtils.hasText(subject) || CollectionUtils.isEmpty(claims)) {
            return Collections.singletonMap("error", "Bad request");
        }

        Instant expirationDate = Instant.now().plusSeconds(authTokenParams.getDuration());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(authTokenParams.getIssuer())
                .expirationTime(Date.from(expirationDate))
                .claim("roles", claims)
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(authTokenParams.getAlgorithm()), claimsSet);
        try {
            signedJWT.sign(authTokenParams.getSigner());
        } catch (JOSEException e) {
            e.printStackTrace();
            return Collections.singletonMap("error", e.getMessage());
        }

        return Collections.singletonMap("access_token", signedJWT.serialize());
    }
}
