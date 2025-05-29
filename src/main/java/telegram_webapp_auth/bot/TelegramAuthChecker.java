package telegram_webapp_auth.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tg.auth.TelegramAuth;
import telegram_webapp_auth.exception.TelegramInitDataParseException;
import telegram_webapp_auth.exception.TelegramSignatureVerificationException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

@Component
@Slf4j
public class TelegramAuthChecker {

    @Value("${telegram.bot.token}")
    private String botToken;

    public boolean isValid(String initDataRaw) {
        try {
            boolean valid = TelegramAuth.isValid(initDataRaw, botToken);
            if (!valid) {
                log.warn("Неверная подпись initData");
            }
            return valid;
        } catch (Exception e) {
            throw new TelegramSignatureVerificationException(e);
        }
    }

    public Map<String, String> getDecodedParams(String initDataRaw) {
        try {
            return parseQueryString(initDataRaw);
        } catch (Exception e) {
            throw new TelegramInitDataParseException(e);
        }
    }

    private static Map<String, String> parseQueryString(String queryString) throws UnsupportedEncodingException {
        Map<String, String> parameters = new TreeMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8) : pair;
            String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8) : null;
            parameters.put(key, value);
        }
        return parameters;
    }
}