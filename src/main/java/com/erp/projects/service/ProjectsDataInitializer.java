package com.erp.projects.service;

import com.erp.projects.entity.Milestone;
import com.erp.projects.entity.Project;
import com.erp.projects.entity.ProjectMember;
import com.erp.projects.entity.Task;
import com.erp.projects.entity.TaskComment;
import com.erp.projects.entity.TimeLog;
import com.erp.projects.enums.ProjectStatus;
import com.erp.projects.enums.TaskPriority;
import com.erp.projects.enums.TaskStatus;
import com.erp.projects.repository.MilestoneRepository;
import com.erp.projects.repository.ProjectMemberRepository;
import com.erp.projects.repository.ProjectRepository;
import com.erp.projects.repository.TaskRepository;
import com.erp.projects.repository.TimeLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds representative Project Management data (projects, milestones, tasks,
 * comments, team members, time logs) on first startup.
 */
@Slf4j
@Component
@Order(7)
@RequiredArgsConstructor
public class ProjectsDataInitializer {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final TimeLogRepository timeLogRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Order(7)
    @Transactional
    public void seed() {
        if (projectRepository.count() > 0) {
            return;
        }
        log.info("Seeding Projects demo data...");

        // name, client, status, progress, monthsAgoStart, durationMonths, budget(lakh)
        Object[][] projectData = {
                {"Beximco ERP Implementation", "Beximco Group", ProjectStatus.ACTIVE, 65, 3, 8, 4500000L},
                {"Square Pharma Portal", "Square Pharmaceuticals", ProjectStatus.ACTIVE, 40, 2, 6, 2800000L},
                {"Daraz Logistics Revamp", "Daraz Bangladesh", ProjectStatus.PLANNING, 10, 0, 7, 3600000L},
                {"Walton POS Rollout", "Walton Hi-Tech", ProjectStatus.ON_HOLD, 30, 5, 5, 1900000L},
                {"BRAC Bank Mobile App", "BRAC Bank", ProjectStatus.COMPLETED, 100, 10, 6, 5200000L},
                {"Pran-RFL Inventory Suite", "Pran-RFL Group", ProjectStatus.ACTIVE, 55, 4, 7, 3100000L}
        };

        List<Project> projects = new ArrayList<>();
        for (Object[] p : projectData) {
            int startAgo = (int) p[4];
            int duration = (int) p[5];
            LocalDate start = LocalDate.now().minusMonths(startAgo);
            projects.add(projectRepository.save(Project.builder()
                    .name((String) p[0])
                    .client((String) p[1])
                    .status((ProjectStatus) p[2])
                    .progress((int) p[3])
                    .startDate(start)
                    .endDate(start.plusMonths(duration))
                    .budget(BigDecimal.valueOf((long) p[6]))
                    .description("End-to-end delivery engagement for " + p[1] + " covering discovery, build and rollout phases.")
                    .build()));
        }

        // Milestones per project
        String[] milestoneNames = {"Requirements Sign-off", "Design Complete", "MVP Delivery",
                "UAT & Testing", "Go-Live"};
        for (Project project : projects) {
            LocalDate base = project.getStartDate() != null ? project.getStartDate() : LocalDate.now();
            for (int m = 0; m < milestoneNames.length; m++) {
                LocalDate due = base.plusMonths(m + 1);
                boolean completed = due.isBefore(LocalDate.now()) && project.getProgress() > (m * 20);
                milestoneRepository.save(Milestone.builder()
                        .project(project)
                        .name(milestoneNames[m])
                        .dueDate(due)
                        .completed(completed)
                        .build());
            }
        }

        // Team members per project
        String[][] memberPool = {
                {"Rakib Hasan", "Project Manager"}, {"Sadia Islam", "Lead Developer"},
                {"Tanvir Ahmed", "Backend Developer"}, {"Nusrat Jahan", "UI/UX Designer"},
                {"Imran Kabir", "QA Engineer"}, {"Farzana Akter", "Business Analyst"},
                {"Shakil Mahmud", "DevOps Engineer"}, {"Mitu Rahman", "Frontend Developer"}
        };
        List<String> allMembers = new ArrayList<>();
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            for (int j = 0; j < 4; j++) {
                String[] mem = memberPool[(i + j) % memberPool.length];
                projectMemberRepository.save(ProjectMember.builder()
                        .project(project).name(mem[0]).role(mem[1]).build());
                if (!allMembers.contains(mem[0])) allMembers.add(mem[0]);
            }
        }

        // ~25 tasks spread across projects, statuses, priorities
        String[] taskTitles = {
                "Set up project repository", "Define data model", "Build authentication module",
                "Design dashboard wireframes", "Implement REST API", "Configure CI/CD pipeline",
                "Write unit tests", "Integrate payment gateway", "Optimize database queries",
                "Create reporting module", "Conduct security audit", "Build notification service",
                "Migrate legacy data", "Design landing page", "Implement role-based access",
                "Set up monitoring", "Write API documentation", "Refactor service layer",
                "Build mobile-responsive UI", "Implement caching layer", "Create user onboarding flow",
                "Set up backup strategy", "Performance load testing", "Fix critical login bug",
                "Prepare deployment runbook"
        };
        String[] labelPool = {"backend", "frontend", "design", "devops", "qa", "urgent", "tech-debt"};
        TaskStatus[] statuses = TaskStatus.values();
        TaskPriority[] priorities = TaskPriority.values();

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < taskTitles.length; i++) {
            Project project = projects.get(i % projects.size());
            TaskStatus status = statuses[i % statuses.length];
            TaskPriority priority = priorities[i % priorities.length];
            String assignee = memberPool[i % memberPool.length][0];
            Task task = Task.builder()
                    .project(project)
                    .title(taskTitles[i])
                    .description("Deliverable for the " + project.getName() + " engagement. Ensure acceptance criteria are met before review.")
                    .status(status)
                    .priority(priority)
                    .assignee(assignee)
                    .dueDate(LocalDate.now().plusDays((i % 10) - 3))
                    .labels(labelPool[i % labelPool.length] + (i % 3 == 0 ? "," + labelPool[(i + 2) % labelPool.length] : ""))
                    .build();

            if (i % 4 == 0) {
                task.getComments().add(TaskComment.builder()
                        .task(task).author("Rakib Hasan")
                        .body("Please prioritise this for the current sprint.")
                        .postedAt(LocalDateTime.now().minusDays(2)).build());
                task.getComments().add(TaskComment.builder()
                        .task(task).author(assignee)
                        .body("On it — will share an update by end of day.")
                        .postedAt(LocalDateTime.now().minusDays(1)).build());
            }
            tasks.add(taskRepository.save(task));
        }

        // ~20 time logs across projects/tasks/members
        String[] notes = {"Development work", "Code review", "Client meeting", "Bug fixing",
                "Design iteration", "Testing", "Documentation", "Deployment support"};
        for (int i = 0; i < 20; i++) {
            Task task = tasks.get(i % tasks.size());
            Project project = task.getProject();
            String member = task.getAssignee();
            timeLogRepository.save(TimeLog.builder()
                    .project(project)
                    .task(i % 5 == 0 ? null : task)
                    .member(member)
                    .hours(BigDecimal.valueOf(2 + (i % 6)).add(BigDecimal.valueOf(0.5 * (i % 2))))
                    .date(LocalDate.now().minusDays(i % 14))
                    .note(notes[i % notes.length])
                    .build());
        }

        log.info("Projects demo data seeded: {} projects, {} tasks.", projects.size(), tasks.size());
    }
}
