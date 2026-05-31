package com.erp.projects.controller;

import com.erp.projects.entity.Milestone;
import com.erp.projects.entity.Project;
import com.erp.projects.entity.ProjectMember;
import com.erp.projects.entity.Task;
import com.erp.projects.entity.TimeLog;
import com.erp.projects.enums.ProjectStatus;
import com.erp.projects.enums.TaskPriority;
import com.erp.projects.enums.TaskStatus;
import com.erp.projects.service.ProjectService;
import com.erp.projects.service.TaskService;
import com.erp.projects.service.TimeLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PROJECTS_VIEW')")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final TimeLogService timeLogService;

    /* ============================== PROJECTS ============================== */

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) ProjectStatus status,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("projects", projectService.search(q, status,
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("pageTitle", "Projects");
        return "projects/projects/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String createForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("pageTitle", "New Project");
        return "projects/projects/form";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("project", projectService.get(id));
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("pageTitle", "Edit Project");
        return "projects/projects/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String save(@Valid @ModelAttribute("project") Project project, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", ProjectStatus.values());
            model.addAttribute("pageTitle", project.isNew() ? "New Project" : "Edit Project");
            return "projects/projects/form";
        }
        if (project.getProgress() < 0) project.setProgress(0);
        if (project.getProgress() > 100) project.setProgress(100);
        projectService.save(project);
        ra.addFlashAttribute("successMessage", "Project saved successfully.");
        return "redirect:/projects";
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public String detail(@PathVariable UUID id, Model model) {
        Project project = projectService.get(id);
        List<Milestone> milestones = projectService.milestones(id);
        List<Task> tasks = taskService.byProject(id);
        List<ProjectMember> members = projectService.members(id);
        List<TimeLog> timeLogs = timeLogService.byProject(id);

        model.addAttribute("project", project);
        model.addAttribute("milestones", milestones);
        model.addAttribute("tasks", tasks);
        model.addAttribute("members", members);
        model.addAttribute("timeLogs", timeLogs);
        model.addAttribute("totalHours", timeLogService.totalHours(timeLogs));
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("pageTitle", project.getName());
        return "projects/projects/detail";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        projectService.delete(id);
        ra.addFlashAttribute("successMessage", "Project deleted.");
        return "redirect:/projects";
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/milestones")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String addMilestone(@PathVariable UUID id,
                               @RequestParam String name,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
                               RedirectAttributes ra) {
        Project project = projectService.get(id);
        projectService.addMilestone(Milestone.builder()
                .project(project).name(name).dueDate(dueDate).completed(false).build());
        ra.addFlashAttribute("successMessage", "Milestone added.");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/milestones/{id:[0-9a-fA-F-]{36}}/toggle")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String toggleMilestone(@PathVariable UUID id,
                                  @RequestParam UUID projectId, RedirectAttributes ra) {
        projectService.toggleMilestone(id);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/{id:[0-9a-fA-F-]{36}}/members")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String addMember(@PathVariable UUID id,
                            @RequestParam String name,
                            @RequestParam(required = false) String role,
                            RedirectAttributes ra) {
        Project project = projectService.get(id);
        projectService.addMember(ProjectMember.builder()
                .project(project).name(name).role(role).build());
        ra.addFlashAttribute("successMessage", "Team member added.");
        return "redirect:/projects/" + id;
    }

    /* =============================== TASKS =============================== */

    @GetMapping("/tasks")
    public String tasksBoard(Model model) {
        Map<TaskStatus, List<Task>> board = new LinkedHashMap<>();
        for (TaskStatus s : TaskStatus.board()) {
            board.put(s, taskService.byStatus(s));
        }
        model.addAttribute("board", board);
        model.addAttribute("pageTitle", "Task Board");
        return "projects/tasks/kanban";
    }

    @GetMapping("/tasks/list")
    public String tasksList(@RequestParam(required = false) TaskStatus status,
                            @RequestParam(required = false) TaskPriority priority, Model model) {
        model.addAttribute("tasks", taskService.filter(status, priority));
        model.addAttribute("statusFilter", status);
        model.addAttribute("priorityFilter", priority);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("pageTitle", "Tasks");
        return "projects/tasks/list";
    }

    @GetMapping("/tasks/new")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String taskCreateForm(@RequestParam(required = false) UUID projectId, Model model) {
        Task task = new Task();
        if (projectId != null) {
            task.setProject(projectService.get(projectId));
        }
        model.addAttribute("task", task);
        prepareTaskForm(model);
        model.addAttribute("pageTitle", "New Task");
        return "projects/tasks/form";
    }

    @GetMapping("/tasks/{id:[0-9a-fA-F-]{36}}/edit")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String taskEditForm(@PathVariable UUID id, Model model) {
        model.addAttribute("task", taskService.get(id));
        prepareTaskForm(model);
        model.addAttribute("pageTitle", "Edit Task");
        return "projects/tasks/form";
    }

    @PostMapping("/tasks/save")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String saveTask(@Valid @ModelAttribute("task") Task task, BindingResult result,
                           @RequestParam(value = "projectId", required = false) UUID projectId,
                           Model model, RedirectAttributes ra) {
        if (projectId == null) {
            result.rejectValue("project", "required", "Project is required.");
        }
        if (result.hasErrors()) {
            prepareTaskForm(model);
            model.addAttribute("pageTitle", task.isNew() ? "New Task" : "Edit Task");
            return "projects/tasks/form";
        }
        task.setProject(projectService.get(projectId));
        taskService.save(task);
        ra.addFlashAttribute("successMessage", "Task saved successfully.");
        return "redirect:/projects/tasks/list";
    }

    @GetMapping("/tasks/{id:[0-9a-fA-F-]{36}}")
    public String taskDetail(@PathVariable UUID id, Model model) {
        Task task = taskService.get(id);
        model.addAttribute("task", task);
        model.addAttribute("timeLogs", timeLogService.byTask(id));
        model.addAttribute("totalHours", timeLogService.totalHours(timeLogService.byTask(id)));
        model.addAttribute("pageTitle", task.getTitle());
        return "projects/tasks/detail";
    }

    @PostMapping("/tasks/{id:[0-9a-fA-F-]{36}}/status")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    @ResponseBody
    public String updateTaskStatus(@PathVariable UUID id, @RequestParam TaskStatus status) {
        taskService.updateStatus(id, status);
        return "ok";
    }

    @PostMapping("/tasks/{id:[0-9a-fA-F-]{36}}/comment")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String addComment(@PathVariable UUID id,
                             @RequestParam(required = false) String author,
                             @RequestParam String body, RedirectAttributes ra) {
        String who = (author == null || author.isBlank()) ? "Me" : author.trim();
        taskService.addComment(id, who, body);
        ra.addFlashAttribute("successMessage", "Comment added.");
        return "redirect:/projects/tasks/" + id;
    }

    @PostMapping("/tasks/{id:[0-9a-fA-F-]{36}}/delete")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String deleteTask(@PathVariable UUID id, RedirectAttributes ra) {
        taskService.delete(id);
        ra.addFlashAttribute("successMessage", "Task deleted.");
        return "redirect:/projects/tasks/list";
    }

    /* ============================ TIMESHEETS ============================ */

    @GetMapping("/timesheets")
    public String timesheets(Model model) {
        List<TimeLog> logs = timeLogService.all();
        model.addAttribute("timeLogs", logs);
        model.addAttribute("totalHours", timeLogService.totalHours(logs));
        model.addAttribute("projects", projectService.all());
        model.addAttribute("pageTitle", "Timesheets");
        return "projects/timesheets/list";
    }

    @PostMapping("/timesheets/save")
    @PreAuthorize("hasAuthority('PROJECTS_EDIT')")
    public String saveTimeLog(@RequestParam UUID projectId,
                              @RequestParam(required = false) UUID taskId,
                              @RequestParam(required = false) String member,
                              @RequestParam BigDecimal hours,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                              @RequestParam(required = false) String note,
                              RedirectAttributes ra) {
        TimeLog log = TimeLog.builder()
                .project(projectService.get(projectId))
                .task(taskId != null ? taskService.get(taskId) : null)
                .member(member)
                .hours(hours != null ? hours : BigDecimal.ZERO)
                .date(date != null ? date : LocalDate.now())
                .note(note)
                .build();
        timeLogService.save(log);
        ra.addFlashAttribute("successMessage", "Time logged.");
        return "redirect:/projects/timesheets";
    }

    private void prepareTaskForm(Model model) {
        model.addAttribute("projects", projectService.all());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
    }
}
