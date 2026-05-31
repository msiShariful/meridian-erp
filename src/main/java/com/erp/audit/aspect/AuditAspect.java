package com.erp.audit.aspect;

import com.erp.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Automatically records an {@link com.erp.audit.entity.AuditLog} for every create,
 * update and delete performed through a module service — but only when triggered by
 * an authenticated user (background seeding, which runs as "system", is ignored).
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Pointcut("execution(* com.erp..service.*Service.save*(..)) "
            + "|| execution(* com.erp..service.*Service.create*(..)) "
            + "|| execution(* com.erp..service.*Service.update*(..)) "
            + "|| execution(* com.erp..service.*Service.delete*(..))")
    public void serviceMutations() {
    }

    @AfterReturning("serviceMutations()")
    public void afterMutation(JoinPoint jp) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return; // skip unauthenticated / system (seed) operations
        }
        String className = jp.getSignature().getDeclaringType().getSimpleName();
        String entityType = className.endsWith("Service") ? className.substring(0, className.length() - 7) : className;
        // Don't audit the audit/notification plumbing itself
        if (entityType.equals("Audit") || entityType.equals("Notification")) {
            return;
        }
        String method = jp.getSignature().getName();
        String action = method.startsWith("delete") ? "DELETE"
                : (method.startsWith("update") ? "UPDATE" : "CREATE");
        try {
            auditService.record(auth.getName(), action, entityType,
                    method + "(" + summarizeArgs(jp.getArgs()) + ")", clientIp());
        } catch (Exception ignored) {
            // auditing must never break the business operation
        }
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) return "";
        Object first = args[0];
        String s = String.valueOf(first);
        return s.length() > 80 ? s.substring(0, 80) : s;
    }

    private String clientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest().getRemoteAddr() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
