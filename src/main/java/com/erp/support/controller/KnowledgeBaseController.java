package com.erp.support.controller;

import com.erp.support.entity.KBArticle;
import com.erp.support.service.KBArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/support/knowledge-base")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SUPPORT_VIEW')")
public class KnowledgeBaseController {

    private final KBArticleService articleService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        Page<KBArticle> articles = articleService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("articles", articles);
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Knowledge Base");
        return "support/knowledge-base/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("article", new KBArticle());
        model.addAttribute("pageTitle", "New Article");
        return "support/knowledge-base/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("article", articleService.get(id));
        model.addAttribute("pageTitle", "Edit Article");
        return "support/knowledge-base/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String save(@Valid @ModelAttribute("article") KBArticle article, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", article.isNew() ? "New Article" : "Edit Article");
            return "support/knowledge-base/form";
        }
        articleService.save(article);
        ra.addFlashAttribute("successMessage", "Article saved successfully.");
        return "redirect:/support/knowledge-base";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        KBArticle article = articleService.get(id);
        model.addAttribute("article", article);
        model.addAttribute("pageTitle", article.getTitle());
        return "support/knowledge-base/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/vote")
    @PreAuthorize("hasAuthority('SUPPORT_VIEW')")
    public String vote(@PathVariable UUID id, @RequestParam boolean helpful, RedirectAttributes ra) {
        articleService.vote(id, helpful);
        ra.addFlashAttribute("successMessage", "Thanks for your feedback.");
        return "redirect:/support/knowledge-base/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        articleService.delete(id);
        ra.addFlashAttribute("successMessage", "Article deleted.");
        return "redirect:/support/knowledge-base";
    }
}
