package com.example.springserver.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {
    private final AccountService accounts;

    public AccountController(AccountService accounts) { this.accounts = accounts; }

    @GetMapping("/login")
    public String login() { return "account/login"; }

    @GetMapping("/signup")
    public String signup() { return "account/signup"; }

    @PostMapping("/signup")
    public String register(@RequestParam String email, @RequestParam String password,
                           @RequestParam String inviteCode, RedirectAttributes redirect) {
        try {
            accounts.register(email, password, inviteCode);
            redirect.addFlashAttribute("success", "가입이 완료됐어요. 로그인해 주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("signupError", exception.getMessage());
            redirect.addFlashAttribute("email", email);
            return "redirect:/signup";
        }
    }
}
