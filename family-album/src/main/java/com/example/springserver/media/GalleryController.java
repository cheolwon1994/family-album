package com.example.springserver.media;

import com.example.springserver.account.UserAccount;
import com.example.springserver.account.AccountService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GalleryController {
    private static final ZoneId ALBUM_ZONE = ZoneId.of("Asia/Seoul");
    private final AccountService accounts;
    private final MediaItemRepository media;

    public GalleryController(AccountService accounts, MediaItemRepository media) {
        this.accounts = accounts;
        this.media = media;
    }

    @GetMapping("/")
    public String gallery(Authentication authentication, Model model) {
        UserAccount current = accounts.findRequiredByEmail(authentication.getName());
        Map<LocalDate, List<MediaItem>> albums = new LinkedHashMap<>();
        for (MediaItem item : media.findAllByOrderByUploadedAtDesc()) {
            LocalDate date = item.getUploadedAt().atZone(ALBUM_ZONE).toLocalDate();
            albums.computeIfAbsent(date, ignored -> new ArrayList<>()).add(item);
        }
        model.addAttribute("albums", albums);
        model.addAttribute("currentUser", current);
        return "media/gallery";
    }
}
