package ru.snsin.apisec.authentication;

import java.util.List;

public record UserProperties(String name, List<String> claims) {

}
