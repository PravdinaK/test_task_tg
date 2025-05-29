package telegram_webapp_auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import telegram_webapp_auth.dto.UserViewDto;
import telegram_webapp_auth.service.AuthWebService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthApiController {

    private final AuthWebService authService;

    @PostMapping("/auth")
    public ResponseEntity<UserViewDto> authenticate(@RequestParam String initData) throws Exception {
        UserViewDto userDto = authService.createUser(initData);
        return ResponseEntity.ok(userDto);
    }
}