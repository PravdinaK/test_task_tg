package telegram_webapp_auth.bot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telegram_webapp_auth.exception.TelegramBotRegistrationException;

@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramWebAppBotInitializer {

    private final TelegramWebAppBot telegramBot;

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
            log.info("Telegram WebApp Bot успешно зарегистрирован и запущен!");
        } catch (TelegramApiException e) {
            throw new TelegramBotRegistrationException(e);
        }
    }
}