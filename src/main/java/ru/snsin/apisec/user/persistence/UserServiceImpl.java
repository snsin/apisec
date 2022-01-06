package ru.snsin.apisec.user.persistence;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.snsin.apisec.authentication.UserProperties;
import ru.snsin.apisec.user.UserDto;
import ru.snsin.apisec.user.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;
    private final Validator validator;
    private PasswordEncoder encoder;

    @Override
    @Transactional
    public long createUser(UserDto user) {
        final Set<ConstraintViolation<UserDto>> validationResults = validator.validate(user);
        if(!validationResults.isEmpty()) {
            var errorMessage = validationResults.stream()
                    .map(ConstraintViolation::getMessage).collect(Collectors.joining());
            throw new RuntimeException("Couldn't create user " + errorMessage);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(user.getEmail());
        final UserEntity savedUser = userRepository.save(userEntity);
        UserCredentials credentials = new UserCredentials();
        credentials.setPassword(encoder.encode(user.getPassword()));
        credentials.setUserName(savedUser.getEmail());
        credentials.setStatus("ACTIVE");
        credentialsRepository.save(credentials);
        return savedUser.getId();
    }

    @Override
    public UserProperties getUserProperties(UserDto user) {

        if (user == null || !validator.validate(user).isEmpty()) {
            throw new IllegalArgumentException("Bad user params");
        }
        UserCredentials credentials = credentialsRepository.findByUserName(user.getEmail())
                .orElseThrow();

        var isPasswordCorrect = encoder.matches(user.getPassword(), credentials.getPassword());

        if (!isPasswordCorrect || !"ACTIVE".equals(credentials.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        UserEntity userEntity = userRepository.getByEmail(user.getEmail())
                .orElseThrow();

        return new UserProperties(userEntity.getEmail(), Collections.singletonList("USER"));
    }
}
