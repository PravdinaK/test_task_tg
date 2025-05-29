package test_task.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import test_task.service.LocalTunnelService;

import java.util.Collections;

@Component
@Slf4j
public class TelegramWebAppBot extends TelegramLongPollingBot {

    private final LocalTunnelService localTunnelService;
    private final String botUsername;

    public TelegramWebAppBot(@Value("${telegram.bot.token}") String botToken,
                             @Value("${telegram.bot.username}") String botUsername,
                             LocalTunnelService localTunnelService) {
        super(botToken);
        this.botUsername = botUsername;
        this.localTunnelService = localTunnelService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Message message = update.getMessage();
        String chatId = message.getChatId().toString();

        String webAppUrl = localTunnelService.getPublicUrl();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("""
            🔽 Нажмите кнопку ниже, чтобы приступить к использованию WebApp.
            """);

        WebAppInfo webAppInfo = new WebAppInfo(webAppUrl);

        InlineKeyboardButton webAppButton = new InlineKeyboardButton();
        webAppButton.setText("Перейти в WebApp");
        webAppButton.setWebApp(webAppInfo);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(Collections.singletonList(webAppButton)));

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: ", e);
        }
    }
}