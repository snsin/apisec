package ru.snsin.apisec.authentication;

import java.util.List;
import java.util.Map;

public interface TokenService {

    Map<String, Object> issueToken(String subject, List<String> claims);
}
