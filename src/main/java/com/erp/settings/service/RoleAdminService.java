package com.erp.settings.service;

import com.erp.common.exception.ResourceNotFoundException;
import com.erp.core.entity.Permission;
import com.erp.core.entity.Role;
import com.erp.core.repository.PermissionRepository;
import com.erp.core.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleAdminService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<Role> allRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Permission> allPermissions() {
        return permissionRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Role getRole(UUID id) {
        return roleRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Role", id));
    }

    @Transactional
    public Role createRole(String name, String displayName, String description) {
        Role role = Role.builder()
                .name(name.toUpperCase().replace(' ', '_'))
                .displayName(displayName)
                .description(description)
                .build();
        return roleRepository.save(role);
    }

    @Transactional
    public void updatePermissions(UUID roleId, List<String> permissionNames) {
        Role role = getRole(roleId);
        Set<Permission> perms = new HashSet<>();
        if (permissionNames != null) {
            permissionNames.forEach(pn ->
                    permissionRepository.findByName(pn).ifPresent(perms::add));
        }
        role.setPermissions(perms);
        roleRepository.save(role);
    }
}
