package telegram_webapp_auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import telegram_webapp_auth.bot.TelegramAuthChecker;
import telegram_webapp_auth.dto.UserViewDto;
import telegram_webapp_auth.exception.InvalidAuthException;
import telegram_webapp_auth.model.User;
import telegram_webapp_auth.repository.UserRepository;
import telegram_webapp_auth.service.AuthWebService;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@Testcontainers
@DisplayName("Интеграционные тесты для AuthWebService")
class AuthWebServiceIntegrationTest {

    @Autowired
    private AuthWebService authWebService;

    @Autowired
    private UserRepository userRepository;

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void loadEnv() {
        io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.load();
        System.setProperty("TELEGRAM_BOT_TOKEN", dotenv.get("TELEGRAM_BOT_TOKEN"));
    }

    @MockitoBean
    private TelegramAuthChecker telegramAuthChecker;

    @Test
    @DisplayName("createUser() — сохраняет пользователя при валидных данных")
    void createUser_withValidInitData_shouldSaveUser() throws Exception {
        String initDataRaw = "valid_init_data";

        when(telegramAuthChecker.isValid(initDataRaw)).thenReturn(true);

        String userJson = """
            {
              "id": 1,
              "first_name": "Alice",
              "last_name": "Smith",
              "username": "alice",
              "language_code": "ru",
              "photo_url": "http://photo.url/alice.png",
              "allows_write_to_pm": "true"
            }
        """;

        Map<String, String> params = Map.of("user", userJson);
        when(telegramAuthChecker.getDecodedParams(initDataRaw)).thenReturn(params);

        UserViewDto dto = authWebService.createUser(initDataRaw);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Alice", dto.firstName());
        assertEquals("Smith", dto.lastName());

        Optional<User> userOpt = userRepository.findById(1L);
        assertTrue(userOpt.isPresent());
        User user = userOpt.get();
        assertEquals("alice", user.getUsername());
    }

    @Test
    @DisplayName("createUser() — выбрасывает InvalidAuthException при невалидных данных")
    void createUser_withInvalidInitData_shouldThrow() {
        String initDataRaw = "bad_data";
        when(telegramAuthChecker.isValid(initDataRaw)).thenReturn(false);

        assertThrows(InvalidAuthException.class, () -> authWebService.createUser(initDataRaw));

        assertTrue(userRepository.findAll().isEmpty());
    }
}