package com.erp.projects.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.projects.entity.Milestone;
import com.erp.projects.entity.Project;
import com.erp.projects.entity.ProjectMember;
import com.erp.projects.enums.ProjectStatus;
import com.erp.projects.repository.MilestoneRepository;
import com.erp.projects.repository.ProjectMemberRepository;
import com.erp.projects.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional(readOnly = true)
    public Page<Project> search(String q, ProjectStatus status, Pageable pageable) {
        return projectRepository.search(blank(q), status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Project> all() {
        return projectRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Project get(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", id));
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public void delete(UUID id) {
        projectRepository.deleteById(id);
    }

    /* ---- Milestones ---- */

    @Transactional(readOnly = true)
    public List<Milestone> milestones(UUID projectId) {
        return milestoneRepository.findByProjectIdOrderByDueDateAsc(projectId);
    }

    public Milestone addMilestone(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }

    public void toggleMilestone(UUID milestoneId) {
        Milestone m = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> ResourceNotFoundException.of("Milestone", milestoneId));
        m.setCompleted(!m.isCompleted());
        milestoneRepository.save(m);
    }

    /* ---- Members ---- */

    @Transactional(readOnly = true)
    public List<ProjectMember> members(UUID projectId) {
        return projectMemberRepository.findByProjectIdOrderByNameAsc(projectId);
    }

    public ProjectMember addMember(ProjectMember member) {
        return projectMemberRepository.save(member);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
