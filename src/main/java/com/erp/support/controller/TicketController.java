package com.erp.support.controller;

import com.erp.support.entity.Ticket;
import com.erp.support.entity.TicketMessage;
import com.erp.support.enums.TicketPriority;
import com.erp.support.enums.TicketStatus;
import com.erp.support.service.TicketCategoryService;
import com.erp.support.service.TicketService;
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
@RequestMapping("/support/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SUPPORT_VIEW')")
public class TicketController {

    private final TicketService ticketService;
    private final TicketCategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) TicketStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        Page<Ticket> tickets = ticketService.search(q, status,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("tickets", tickets);
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("pageTitle", "Tickets");
        return "support/tickets/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String createForm(Model model) {
        Ticket ticket = new Ticket();
        ticket.setSlaResponseHours(TicketPriority.MEDIUM.getDefaultResponseHours());
        ticket.setSlaResolutionHours(TicketPriority.MEDIUM.getDefaultResolutionHours());
        model.addAttribute("ticket", ticket);
        prepareForm(model);
        model.addAttribute("pageTitle", "New Ticket");
        return "support/tickets/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("ticket", ticketService.get(id));
        prepareForm(model);
        model.addAttribute("pageTitle", "Edit Ticket");
        return "support/tickets/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String save(@Valid @ModelAttribute("ticket") Ticket ticket, BindingResult result,
                       @RequestParam(value = "categoryId", required = false) UUID categoryId,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            prepareForm(model);
            model.addAttribute("pageTitle", ticket.isNew() ? "New Ticket" : "Edit Ticket");
            return "support/tickets/form";
        }
        ticket.setCategory(categoryId != null ? categoryService.get(categoryId) : null);
        // Apply default SLA hours for the chosen priority when none were provided.
        if (ticket.getSlaResolutionHours() <= 0) {
            ticket.setSlaResolutionHours(ticket.getPriority().getDefaultResolutionHours());
        }
        if (ticket.getSlaResponseHours() <= 0) {
            ticket.setSlaResponseHours(ticket.getPriority().getDefaultResponseHours());
        }
        ticketService.save(ticket);
        ra.addFlashAttribute("successMessage", "Ticket saved successfully.");
        return "redirect:/support/tickets";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Ticket ticket = ticketService.get(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("newMessage", new TicketMessage());
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("pageTitle", ticket.getTicketNumber() + " · " + ticket.getSubject());
        return "support/tickets/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/reply")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String reply(@PathVariable UUID id, @ModelAttribute("newMessage") TicketMessage message,
                        RedirectAttributes ra) {
        ticketService.addMessage(id, message);
        ra.addFlashAttribute("successMessage", "Reply added.");
        return "redirect:/support/tickets/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/status")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String updateStatus(@PathVariable UUID id, @RequestParam TicketStatus status,
                               RedirectAttributes ra) {
        ticketService.updateStatus(id, status);
        ra.addFlashAttribute("successMessage", "Ticket status updated to " + status.getLabel() + ".");
        return "redirect:/support/tickets/" + id;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('SUPPORT_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        ticketService.delete(id);
        ra.addFlashAttribute("successMessage", "Ticket deleted.");
        return "redirect:/support/tickets";
    }

    private void prepareForm(Model model) {
        model.addAttribute("categories", categoryService.all());
        model.addAttribute("priorities", TicketPriority.values());
        model.addAttribute("statuses", TicketStatus.values());
    }
}
