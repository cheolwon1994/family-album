package com.example.springserver.invite;

import com.example.springserver.account.UserAccount;
import com.example.springserver.account.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InviteController {
    private final InviteService invites;
    private final AccountService accounts;

    public InviteController(InviteService invites, AccountService accounts) {
        this.invites = invites;
        this.accounts = accounts;
    }

    @PostMapping("/admin/invites")
    public String create(Authentication authentication, RedirectAttributes redirect) {
        UserAccount current = accounts.findRequiredByEmail(authentication.getName());
        redirect.addFlashAttribute("newInviteCode", invites.create(current));
        return "redirect:/";
    }
}
