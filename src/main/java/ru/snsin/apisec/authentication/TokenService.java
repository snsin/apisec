package ru.snsin.apisec.authentication;

import java.util.List;

public interface TokenService {

    String issueAccessToken(String subject, List<String> claims);
}
