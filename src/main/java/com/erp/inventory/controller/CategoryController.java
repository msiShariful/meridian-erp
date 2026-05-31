package com.erp.inventory.controller;

import com.erp.inventory.entity.ProductCategory;
import com.erp.inventory.service.ProductCategoryService;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/inventory/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('INVENTORY_VIEW')")
public class CategoryController {

    private final ProductCategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        Page<ProductCategory> categories = categoryService.search(q, PageRequest.of(page, 20, Sort.by("name")));
        Map<UUID, Long> counts = new LinkedHashMap<>();
        for (ProductCategory c : categories.getContent()) {
            counts.put(c.getId(), categoryService.productCount(c));
        }
        model.addAttribute("categories", categories);
        model.addAttribute("counts", counts);
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Categories");
        return "inventory/categories/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("category", new ProductCategory());
        model.addAttribute("parents", categoryService.all());
        model.addAttribute("pageTitle", "New Category");
        return "inventory/categories/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("category", categoryService.get(id));
        model.addAttribute("parents", categoryService.all());
        model.addAttribute("pageTitle", "Edit Category");
        return "inventory/categories/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String save(@Valid @ModelAttribute("category") ProductCategory category, BindingResult result,
                       @RequestParam(value = "parentId", required = false) UUID parentId,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("parents", categoryService.all());
            model.addAttribute("pageTitle", category.isNew() ? "New Category" : "Edit Category");
            return "inventory/categories/form";
        }
        category.setParent(parentId != null ? categoryService.get(parentId) : null);
        categoryService.save(category);
        ra.addFlashAttribute("successMessage", "Category saved successfully.");
        return "redirect:/inventory/categories";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        categoryService.delete(id);
        ra.addFlashAttribute("successMessage", "Category deleted.");
        return "redirect:/inventory/categories";
    }
}
