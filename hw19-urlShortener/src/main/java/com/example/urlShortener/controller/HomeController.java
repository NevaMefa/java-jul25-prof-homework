package com.example.urlShortener.controller;

import com.example.urlShortener.dto.CreateLinkRequest;
import com.example.urlShortener.dto.LinkResponse;
import com.example.urlShortener.service.LinkService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final LinkService linkService;
    private final CacheManager cacheManager;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("createLinkRequest", new CreateLinkRequest());

        List<LinkResponse> links = linkService.getAllLinks();
        model.addAttribute("links", links);

        // Простая статистика без кастинга
        model.addAttribute("cacheInfo", getSimpleCacheInfo());

        return "home";
    }

    @PostMapping("/create-link")
    public String createShortLink(@ModelAttribute CreateLinkRequest request, RedirectAttributes redirectAttributes) {
        try {
            LinkResponse linkResponse = linkService.createShortLink(request.getOriginalUrl());
            redirectAttributes.addFlashAttribute("success", "Создана ссылка: " + linkResponse.getShortUrl());
            redirectAttributes.addFlashAttribute("newLink", linkResponse);

            redirectAttributes.addFlashAttribute("cacheInfo", "Ссылка добавлена в кеш. Кеш all-links инвалидирован.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/";
    }

    private String getSimpleCacheInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Доступные кеши:\n");
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            info.append(" - ").append(name);
            if (cache != null) {
                info.append(" (активен)");
            }
            info.append("\n");
        });
        return info.toString();
    }
}
