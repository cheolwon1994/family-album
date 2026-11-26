package com.example.springserver.share;

import com.example.springserver.account.AccountService;
import com.example.springserver.media.MediaItem;
import com.example.springserver.media.MediaItemRepository;
import com.example.springserver.media.MediaService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ShareController {
    private final MediaItemRepository media;
    private final MediaService mediaService;
    private final AccountService accounts;
    private final ShareService shares;
    private final Duration lifetime;

    public ShareController(MediaItemRepository media, MediaService mediaService, AccountService accounts,
                           ShareService shares, @Value("${app.share.expiration:PT24H}") Duration lifetime) {
        this.media = media;
        this.mediaService = mediaService;
        this.accounts = accounts;
        this.shares = shares;
        this.lifetime = lifetime;
    }

    @PostMapping("/media/{id}/shares")
    public String create(@PathVariable long id, Authentication authentication, RedirectAttributes redirect) {
        accounts.findRequiredByEmail(authentication.getName());
        MediaItem item = media.findById(id).orElseThrow();
        String token = shares.create(item, lifetime);
        String shareUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/shared/{token}").buildAndExpand(token).toUriString();
        redirect.addFlashAttribute("shareUrl", shareUrl);
        redirect.addFlashAttribute("success", "24시간 동안 사용할 수 있는 공유 링크를 만들었어요.");
        return "redirect:/";
    }

    @GetMapping("/shared/{token}")
    public ResponseEntity<Resource> show(@PathVariable String token) throws IOException {
        final MediaItem item;
        try {
            item = shares.resolve(token).getMedia();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(item.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(item.getOriginalName(), StandardCharsets.UTF_8).build().toString())
                .body(mediaService.load(item));
    }
}
