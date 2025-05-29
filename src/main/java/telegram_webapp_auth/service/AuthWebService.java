package telegram_webapp_auth.service;

import telegram_webapp_auth.dto.UserViewDto;

public interface AuthWebService {

    UserViewDto createUser(String initDataRaw);
}