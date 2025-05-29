package telegram_webapp_auth;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import telegram_webapp_auth.bot.TelegramAuthChecker;
import telegram_webapp_auth.dto.UserViewDto;
import telegram_webapp_auth.exception.InvalidAuthException;
import telegram_webapp_auth.exception.UserDataProcessingException;
import telegram_webapp_auth.model.User;
import telegram_webapp_auth.repository.UserRepository;
import telegram_webapp_auth.service.AuthWebServiceImpl;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты для AuthWebServiceImpl")
class AuthWebServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TelegramAuthChecker authChecker;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthWebServiceImpl authWebService;

    @Test
    @DisplayName("createUser() — выбрасывает InvalidAuthException при невалидных данных")
    void createUser_whenInitDataInvalid_thenThrowsInvalidAuthException() {
        String initDataRaw = "invalid_data";

        when(authChecker.isValid(initDataRaw)).thenReturn(false);

        assertThrows(InvalidAuthException.class, () -> authWebService.createUser(initDataRaw));
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("createUser() — сохраняет пользователя и возвращает DTO при валидных данных")
    void createUser_whenValidInitData_thenSaveUserAndReturnDto() throws Exception {
        String initDataRaw = "valid_data";
        when(authChecker.isValid(initDataRaw)).thenReturn(true);

        Map<String, String> params = new HashMap<>();
        String userJson = """
            {
              "id": 123,
              "first_name": "John",
              "last_name": "Doe",
              "username": "johndoe",
              "language_code": "en",
              "photo_url": "http://photo.url",
              "allows_write_to_pm": "true"
            }
        """;
        params.put("user", userJson);
        when(authChecker.getDecodedParams(initDataRaw)).thenReturn(params);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", 123);
        userMap.put("first_name", "John");
        userMap.put("last_name", "Doe");
        userMap.put("username", "johndoe");
        userMap.put("language_code", "en");
        userMap.put("photo_url", "http://photo.url");
        userMap.put("allows_write_to_pm", "true");

        when(objectMapper.readValue(eq(userJson), any(TypeReference.class))).thenReturn(userMap);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserViewDto result = authWebService.createUser(initDataRaw);

        assertEquals(123L, result.id());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("johndoe", result.username());
        assertEquals("en", result.languageCode());
        assertEquals("http://photo.url", result.photoUrl());
        assertTrue(result.allowsWriteToPm());

        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals(123L, savedUser.getId());
    }

    @Test
    @DisplayName("createUser() — выбрасывает UserDataProcessingException при ошибке в обработке")
    void createUser_whenExceptionDuringProcessing_thenThrowsUserDataProcessingException() throws Exception {
        String initDataRaw = "valid_data";
        when(authChecker.isValid(initDataRaw)).thenReturn(true);
        when(authChecker.getDecodedParams(initDataRaw)).thenThrow(new RuntimeException("fail"));

        assertThrows(UserDataProcessingException.class, () -> authWebService.createUser(initDataRaw));
        verifyNoInteractions(userRepository);
    }
}