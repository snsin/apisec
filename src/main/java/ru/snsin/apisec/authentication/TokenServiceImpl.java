package ru.snsin.apisec.authentication;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenParams authTokenParams;

    @Override
    public String issueAccessToken(String subject, List<String> claims) {

        if (!StringUtils.hasText(subject) || CollectionUtils.isEmpty(claims)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return signedJWT.serialize();
    }
}
