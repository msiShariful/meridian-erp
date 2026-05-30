package com.erp.core.service;

import com.erp.common.enums.PermissionType;
import com.erp.common.enums.RoleType;
import com.erp.core.entity.Permission;
import com.erp.core.entity.Role;
import com.erp.core.entity.User;
import com.erp.core.repository.PermissionRepository;
import com.erp.core.repository.RoleRepository;
import com.erp.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Seeds the security backbone (permissions, roles with their permission matrix and
 * one demo user per role) on first startup. Idempotent — skips entities that exist.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class SecurityDataInitializer {

    private static final String DEFAULT_PASSWORD = "Admin@1234";

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seed() {
        if (userRepository.existsByEmailIgnoreCase("admin@erp.com")) {
            log.info("Security data already present — skipping seed.");
            return;
        }
        log.info("Seeding security data (permissions, roles, users)...");

        Map<String, Permission> permissions = seedPermissions();
        Map<RoleType, Role> roles = seedRoles(permissions);
        seedUsers(roles);

        log.info("Security seed complete. Default password for all demo accounts: {}", DEFAULT_PASSWORD);
    }

    private Map<String, Permission> seedPermissions() {
        return Arrays.stream(PermissionType.values()).map(pt -> {
            Permission p = permissionRepository.findByName(pt.name()).orElseGet(() -> {
                Permission np = Permission.builder()
                        .name(pt.name())
                        .description(humanize(pt.name()))
                        .build();
                return permissionRepository.save(np);
            });
            return p;
        }).collect(Collectors.toMap(Permission::getName, p -> p));
    }

    private Map<RoleType, Role> seedRoles(Map<String, Permission> perms) {
        Map<RoleType, Set<PermissionType>> matrix = roleMatrix();
        return Arrays.stream(RoleType.values()).collect(Collectors.toMap(rt -> rt, rt -> {
            Role role = roleRepository.findByName(rt.name()).orElseGet(() -> Role.builder()
                    .name(rt.name())
                    .displayName(rt.getDisplayName())
                    .description(rt.getDisplayName() + " role")
                    .systemRole(true)
                    .build());
            Set<Permission> grant = matrix.get(rt).stream()
                    .map(pt -> perms.get(pt.name()))
                    .collect(Collectors.toSet());
            role.setPermissions(grant);
            return roleRepository.save(role);
        }));
    }

    private Map<RoleType, Set<PermissionType>> roleMatrix() {
        Set<PermissionType> all = EnumSet.allOf(PermissionType.class);
        return Map.ofEntries(
                Map.entry(RoleType.SUPER_ADMIN, all),
                Map.entry(RoleType.ADMIN, EnumSet.complementOf(EnumSet.of(PermissionType.ROLE_MANAGE))),
                Map.entry(RoleType.HR_MANAGER, EnumSet.of(PermissionType.DASHBOARD_VIEW,
                        PermissionType.HRM_VIEW, PermissionType.HRM_EDIT, PermissionType.REPORTS_VIEW)),
                Map.entry(RoleType.SALES_MANAGER, EnumSet.of(PermissionType.DASHBOARD_VIEW,
                        PermissionType.CRM_VIEW, PermissionType.CRM_EDIT,
                        PermissionType.ECOMMERCE_VIEW, PermissionType.ECOMMERCE_EDIT,
                        PermissionType.REPORTS_VIEW)),
                Map.entry(RoleType.ACCOUNTANT, EnumSet.of(PermissionType.DASHBOARD_VIEW,
                        PermissionType.ACCOUNTING_VIEW, PermissionType.ACCOUNTING_EDIT,
                        PermissionType.REPORTS_VIEW)),
                Map.entry(RoleType.INVENTORY_MANAGER, EnumSet.of(PermissionType.DASHBOARD_VIEW,
                        PermissionType.INVENTORY_VIEW, PermissionType.INVENTORY_EDIT,
                        PermissionType.PROCUREMENT_VIEW, PermissionType.PROCUREMENT_EDIT,
                        PermissionType.REPORTS_VIEW)),
                Map.entry(RoleType.SUPPORT_AGENT, EnumSet.of(PermissionType.DASHBOARD_VIEW,
                        PermissionType.SUPPORT_VIEW, PermissionType.SUPPORT_EDIT)),
                Map.entry(RoleType.EMPLOYEE, EnumSet.of(PermissionType.DASHBOARD_VIEW,
                        PermissionType.PROJECTS_VIEW, PermissionType.PROJECTS_EDIT)),
                Map.entry(RoleType.CUSTOMER, EnumSet.of(PermissionType.SUPPORT_VIEW)));
    }

    private void seedUsers(Map<RoleType, Role> roles) {
        createUser("System Administrator", "admin@erp.com", "Chief Administrator", roles.get(RoleType.SUPER_ADMIN));
        createUser("Tanvir Ahmed", "tanvir.admin@erp.com", "Operations Admin", roles.get(RoleType.ADMIN));
        createUser("Nusrat Jahan", "nusrat.hr@erp.com", "HR Manager", roles.get(RoleType.HR_MANAGER));
        createUser("Rakib Hasan", "rakib.sales@erp.com", "Sales Manager", roles.get(RoleType.SALES_MANAGER));
        createUser("Farzana Akter", "farzana.acc@erp.com", "Senior Accountant", roles.get(RoleType.ACCOUNTANT));
        createUser("Imran Khan", "imran.inv@erp.com", "Inventory Manager", roles.get(RoleType.INVENTORY_MANAGER));
        createUser("Sadia Islam", "sadia.support@erp.com", "Support Lead", roles.get(RoleType.SUPPORT_AGENT));
        createUser("Mehedi Hasan", "mehedi.emp@erp.com", "Software Engineer", roles.get(RoleType.EMPLOYEE));
        createUser("Ayesha Siddiqua", "ayesha.customer@example.com", "Customer", roles.get(RoleType.CUSTOMER));
    }

    private void createUser(String name, String email, String jobTitle, Role role) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        User user = User.builder()
                .fullName(name)
                .email(email)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .jobTitle(jobTitle)
                .enabled(true)
                .build();
        user.addRole(role);
        userRepository.save(user);
    }

    private String humanize(String code) {
        return Arrays.stream(code.split("_"))
                .map(w -> w.charAt(0) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
