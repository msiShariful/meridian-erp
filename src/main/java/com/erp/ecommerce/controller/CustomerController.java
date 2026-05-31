package com.erp.ecommerce.controller;

import com.erp.ecommerce.entity.Customer;
import com.erp.ecommerce.service.CustomerService;
import com.erp.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/ecommerce/customers")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ECOMMERCE_VIEW')")
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("customers", customerService.search(q,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "totalSpent"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Customers");
        return "ecommerce/customers/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("pageTitle", "New Customer");
        return "ecommerce/customers/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("customer", customerService.get(id));
        model.addAttribute("pageTitle", "Edit Customer");
        return "ecommerce/customers/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String save(@ModelAttribute("customer") Customer customer, RedirectAttributes ra) {
        customerService.save(customer);
        ra.addFlashAttribute("successMessage", "Customer saved successfully.");
        return "redirect:/ecommerce/customers";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Customer customer = customerService.get(id);
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orderService.byCustomerEmail(customer.getEmail()));
        model.addAttribute("pageTitle", customer.getName());
        return "ecommerce/customers/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('ECOMMERCE_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        customerService.delete(id);
        ra.addFlashAttribute("successMessage", "Customer deleted.");
        return "redirect:/ecommerce/customers";
    }
}
