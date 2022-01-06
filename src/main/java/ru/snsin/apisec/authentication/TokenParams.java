package ru.snsin.apisec.authentication;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenParams {

    JWSSigner signer;
    JWSAlgorithm algorithm;
    int duration;
    String issuer;
}
