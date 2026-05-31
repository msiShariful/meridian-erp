package com.erp.inventory.controller;

import com.erp.common.util.FileStorageService;
import com.erp.inventory.entity.Product;
import com.erp.inventory.service.ProductCategoryService;
import com.erp.inventory.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/inventory/products")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('INVENTORY_VIEW')")
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService categoryService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("products", productService.search(q, PageRequest.of(page, 20, Sort.by("name"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Products");
        return "inventory/products/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.all());
        model.addAttribute("nextSku", productService.nextSku());
        model.addAttribute("pageTitle", "New Product");
        return "inventory/products/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("product", productService.get(id));
        model.addAttribute("categories", categoryService.all());
        model.addAttribute("pageTitle", "Edit Product");
        return "inventory/products/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String save(@Valid @ModelAttribute("product") Product product, BindingResult result,
                       @RequestParam(value = "categoryId", required = false) UUID categoryId,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.all());
            model.addAttribute("nextSku", productService.nextSku());
            model.addAttribute("pageTitle", product.isNew() ? "New Product" : "Edit Product");
            return "inventory/products/form";
        }
        product.setCategory(categoryId != null ? categoryService.get(categoryId) : null);
        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImage(fileStorageService.store(imageFile, "products"));
        }
        productService.save(product);
        ra.addFlashAttribute("successMessage", "Product saved successfully.");
        return "redirect:/inventory/products";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Product product = productService.get(id);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", product.getName());
        return "inventory/products/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('INVENTORY_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        productService.delete(id);
        ra.addFlashAttribute("successMessage", "Product deleted.");
        return "redirect:/inventory/products";
    }
}
