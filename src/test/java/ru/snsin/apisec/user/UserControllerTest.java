package ru.snsin.apisec.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    void shouldCreateUserIfParamsAreValid() throws Exception {
        Mockito.when(userService.createUser(Mockito.any())).thenReturn(9L);

        mockMvc.perform(post("/api/v1/users")
                        .content("{\"email\":\"test@example.com\", \"password\":\"aA1i&iiii\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("user")))
                .andExpect(status().isCreated());
    }

    @Test
    void  shouldReturnBadRequestIfAnyParamIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .content("{\"email\":\"test@example.com\", \"password\":\"12345679\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("user")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenIfUserExists() throws Exception {
        Mockito.when(userService.createUser(Mockito.any()))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        mockMvc.perform(post("/api/v1/users")
                        .content("{\"email\":\"test@example.com\", \"password\":\"aA1i&iiii\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("user")))
                .andExpect(status().isForbidden());
    }
}