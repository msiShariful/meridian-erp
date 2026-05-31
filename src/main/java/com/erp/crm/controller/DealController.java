package com.erp.crm.controller;

import com.erp.crm.entity.Deal;
import com.erp.crm.enums.DealStage;
import com.erp.crm.service.CompanyService;
import com.erp.crm.service.ContactService;
import com.erp.crm.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/crm/deals")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CRM_VIEW')")
public class DealController {

    private final DealService dealService;
    private final ContactService contactService;
    private final CompanyService companyService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("deals", dealService.search(q, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "value"))));
        model.addAttribute("q", q);
        model.addAttribute("pageTitle", "Deals");
        return "crm/deals/list";
    }

    @GetMapping("/kanban")
    public String kanban(Model model) {
        Map<DealStage, List<Deal>> board = new LinkedHashMap<>();
        Map<DealStage, BigDecimal> totals = new LinkedHashMap<>();
        for (DealStage s : DealStage.pipeline()) {
            List<Deal> deals = dealService.byStage(s);
            board.put(s, deals);
            totals.put(s, deals.stream().map(Deal::getValue).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        model.addAttribute("board", board);
        model.addAttribute("totals", totals);
        model.addAttribute("pageTitle", "Deal Pipeline");
        return "crm/deals/kanban";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("deal", new Deal());
        prepareForm(model);
        model.addAttribute("pageTitle", "New Deal");
        return "crm/deals/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("deal", dealService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Deal");
        return "crm/deals/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String save(@Valid @ModelAttribute("deal") Deal deal, BindingResult result,
                       @RequestParam(value = "contactId", required = false) UUID contactId,
                       @RequestParam(value = "companyId", required = false) UUID companyId,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", deal.isNew() ? "New Deal" : "Edit Deal");
            return "crm/deals/form";
        }
        deal.setContact(contactId != null ? contactService.get(contactId) : null);
        deal.setCompany(companyId != null ? companyService.get(companyId) : null);
        dealService.save(deal);
        ra.addFlashAttribute("successMessage", "Deal saved successfully.");
        return "redirect:/crm/deals";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Deal deal = dealService.get(id);
        model.addAttribute("deal", deal);
        model.addAttribute("pageTitle", deal.getTitle());
        return "crm/deals/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/stage")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    @ResponseBody
    public String updateStage(@PathVariable UUID id, @RequestParam DealStage stage) {
        dealService.updateStage(id, stage);
        return "ok";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('CRM_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        dealService.delete(id);
        ra.addFlashAttribute("successMessage", "Deal deleted.");
        return "redirect:/crm/deals";
    }

    private void prepareForm(Model model) {
        model.addAttribute("stages", DealStage.values());
        model.addAttribute("contacts", contactService.all());
        model.addAttribute("companies", companyService.all());
    }
}
