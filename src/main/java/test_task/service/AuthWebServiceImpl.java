package test_task.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import test_task.bot.TelegramAuthChecker;
import test_task.dto.UserViewDto;
import test_task.exception.InvalidAuthException;
import test_task.model.User;
import test_task.repository.UserRepository;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthWebServiceImpl implements AuthWebService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final TelegramAuthChecker authChecker;

    @Override
    public UserViewDto createUser(String initDataRaw) {
        log.info("Исходные initData: {}", initDataRaw);

        if (!authChecker.isValid(initDataRaw)) {
            throw new InvalidAuthException("Недействительные данные авторизации Telegram");
        }

        try {
            Map<String, String> params = authChecker.getDecodedParams(initDataRaw);
            String userJson = params.get("user");
            log.info("Декодированный JSON пользователя: {}", userJson);

            Map<String, Object> userMap = objectMapper.readValue(userJson, new TypeReference<>() {});

            User user = new User();
            user.setId(Long.parseLong(userMap.get("id").toString()));
            user.setFirstName((String) userMap.get("first_name"));
            user.setLastName((String) userMap.getOrDefault("last_name", ""));
            user.setUsername((String) userMap.getOrDefault("username", ""));
            user.setLanguageCode((String) userMap.getOrDefault("language_code", ""));
            user.setPhotoUrl((String) userMap.getOrDefault("photo_url", ""));
            user.setAllowsWriteToPm(Boolean.parseBoolean(
                    String.valueOf(userMap.getOrDefault("allows_write_to_pm", "false")))
            );

            userRepository.save(user);

            return new UserViewDto(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getUsername(),
                    user.getLanguageCode(),
                    user.getPhotoUrl(),
                    user.getAllowsWriteToPm()
            );

        } catch (Exception e) {
            log.error("Ошибка при разборе или сохранении данных пользователя", e);
            throw new RuntimeException("Не удалось разобрать данные пользователя Telegram", e);
        }
    }
}