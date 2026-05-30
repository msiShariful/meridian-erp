package com.erp.core.service;

import com.erp.common.exception.BusinessException;
import com.erp.core.entity.Role;
import com.erp.core.entity.User;
import com.erp.core.repository.RoleRepository;
import com.erp.core.repository.UserRepository;
import com.erp.common.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException("User not found: " + email));
    }

    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    @Transactional(readOnly = true)
    public Page<User> search(String query, Pageable pageable) {
        return userRepository.search(blankToNull(query), pageable);
    }

    @Transactional
    public void updateProfile(String email, String fullName, String phone, String jobTitle) {
        User user = getByEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setJobTitle(jobTitle);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updateAvatar(String email, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return;
        }
        User user = getByEmail(email);
        String path = fileStorageService.store(file, "avatars");
        user.setAvatar(path);
        userRepository.save(user);
    }

    @Transactional
    public User createUser(String fullName, String email, String rawPassword,
                           String jobTitle, Set<String> roleNames) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("A user with that email already exists.");
        }
        Set<Role> roles = roleNames.stream()
                .map(rn -> roleRepository.findByName(rn)
                        .orElseThrow(() -> new BusinessException("Unknown role: " + rn)))
                .collect(Collectors.toSet());
        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .jobTitle(jobTitle)
                .enabled(true)
                .roles(roles)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public void updateUser(UUID id, String fullName, String jobTitle, Set<String> roleNames, boolean enabled) {
        User user = getById(id);
        user.setFullName(fullName);
        user.setJobTitle(jobTitle);
        user.setEnabled(enabled);
        Set<Role> roles = roleNames.stream()
                .map(rn -> roleRepository.findByName(rn)
                        .orElseThrow(() -> new BusinessException("Unknown role: " + rn)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Transactional
    public void setEnabled(UUID id, boolean enabled) {
        User user = getById(id);
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Role> allRoles() {
        return roleRepository.findAll();
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
