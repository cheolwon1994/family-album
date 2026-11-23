package com.example.springserver.media;

import com.example.springserver.account.AccountService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MediaController {
    private final MediaService service;
    private final MediaItemRepository media;
    private final AccountService accounts;

    public MediaController(MediaService service, MediaItemRepository media, AccountService accounts) {
        this.service = service;
        this.media = media;
        this.accounts = accounts;
    }

    @PostMapping("/media")
    public String upload(@RequestParam("file") MultipartFile file, Authentication authentication,
                         RedirectAttributes redirect) {
        try {
            service.store(file, accounts.findRequiredByEmail(authentication.getName()));
            redirect.addFlashAttribute("success", "파일을 올렸어요.");
        } catch (IllegalArgumentException | IOException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<Resource> show(@PathVariable long id) throws IOException {
        MediaItem item = media.findById(id).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(item.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(item.getOriginalName(), StandardCharsets.UTF_8).build().toString())
                .body(service.load(item));
    }
}
