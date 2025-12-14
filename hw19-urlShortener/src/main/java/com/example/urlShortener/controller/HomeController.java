package com.example.urlShortener.controller;

import com.example.urlShortener.dto.CreateLinkRequest;
import com.example.urlShortener.dto.LinkResponse;
import com.example.urlShortener.service.LinkService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("createLinkRequest", new CreateLinkRequest());
        List<LinkResponse> links = linkService.getAllLinks();
        model.addAttribute("links", links);
        return "home";
    }

    @PostMapping("/create-link")
    public String createShortLink(@ModelAttribute CreateLinkRequest request, RedirectAttributes redirectAttributes) {
        try {
            LinkResponse linkResponse = linkService.createShortLink(request.getOriginalUrl());
            redirectAttributes.addFlashAttribute("success", "Создана ссылка: " + linkResponse.getShortUrl());
            redirectAttributes.addFlashAttribute("newLink", linkResponse);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }

        return "redirect:/";
    }
}
