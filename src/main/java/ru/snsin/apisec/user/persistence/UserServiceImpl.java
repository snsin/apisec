package ru.snsin.apisec.user.persistence;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.snsin.apisec.user.UserDto;
import ru.snsin.apisec.user.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
}
