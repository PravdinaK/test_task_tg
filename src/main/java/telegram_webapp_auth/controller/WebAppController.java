package telegram_webapp_auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebAppController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}