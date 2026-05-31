package com.erp.projects.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.projects.entity.Task;
import com.erp.projects.entity.TaskComment;
import com.erp.projects.enums.TaskPriority;
import com.erp.projects.enums.TaskStatus;
import com.erp.projects.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public Task get(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Task", id));
    }

    @Transactional(readOnly = true)
    public List<Task> byStatus(TaskStatus status) {
        return taskRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public List<Task> byProject(UUID projectId) {
        return taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    @Transactional(readOnly = true)
    public List<Task> filter(TaskStatus status, TaskPriority priority) {
        return taskRepository.filter(status, priority);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void updateStatus(UUID id, TaskStatus status) {
        Task task = get(id);
        task.setStatus(status);
        taskRepository.save(task);
    }

    public void delete(UUID id) {
        taskRepository.deleteById(id);
    }

    public TaskComment addComment(UUID taskId, String author, String body) {
        Task task = get(taskId);
        TaskComment comment = TaskComment.builder()
                .task(task)
                .author(author)
                .body(body)
                .postedAt(LocalDateTime.now())
                .build();
        task.getComments().add(comment);
        taskRepository.save(task);
        return comment;
    }
}
