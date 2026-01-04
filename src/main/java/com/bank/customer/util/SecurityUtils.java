package com.bank.customer.util;

import com.bank.customer.exception.ForbiddenException;
import com.bank.customer.exception.InsufficientPermissionsException;
import com.bank.customer.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class SecurityUtils {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    /**
     * Extrae el ID del usuario del header
     */
    public String getUserId(ServerWebExchange exchange) {
        String userId = exchange.getRequest().getHeaders().getFirst(HEADER_USER_ID);

        if (userId == null || userId.isEmpty()) {
            log.error("No user ID found in headers");
            throw new UnauthorizedException("Usuario no autenticado. Por favor inicie sesión");
        }

        log.debug("Extracted user ID: {}", userId);
        return userId;
    }

    /**
     * Extrae los roles del usuario del header
     */
    public List<String> getUserRoles(ServerWebExchange exchange) {
        String rolesHeader = exchange.getRequest().getHeaders().getFirst(HEADER_USER_ROLES);

        if (rolesHeader == null || rolesHeader.isEmpty()) {
            log.warn("No roles header found for user");
            return List.of();
        }

        List<String> roles = Arrays.asList(rolesHeader.split(","));
        log.debug("Extracted roles: {}", roles);
        return roles;
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(ServerWebExchange exchange, String requiredRole) {
        List<String> roles = getUserRoles(exchange);
        boolean hasRole = roles.contains(requiredRole);

        log.debug("Checking if user has role '{}': {}", requiredRole, hasRole);
        return hasRole;
    }

    /**
     * Verifica si el usuario tiene al menos uno de los roles especificados
     */
    public boolean hasAnyRole(ServerWebExchange exchange, String... requiredRoles) {
        List<String> userRoles = getUserRoles(exchange);

        for (String requiredRole : requiredRoles) {
            if (userRoles.contains(requiredRole)) {
                log.debug("User has role: {}", requiredRole);
                return true;
            }
        }

        log.debug("User doesn't have any of the required roles: {}", Arrays.toString(requiredRoles));
        return false;
    }

    /**
     * Requiere que el usuario tenga un rol específico
     */
    public Mono<Void> requireRole(ServerWebExchange exchange, String requiredRole) {
        String userId = getUserId(exchange);
        List<String> userRoles = getUserRoles(exchange);

        if (!userRoles.contains(requiredRole)) {
            log.warn("Access denied: User '{}' with roles {} doesn't have required role '{}'",
                    userId, userRoles, requiredRole);

            throw new ForbiddenException(
                    String.format("Acceso denegado. Se requiere el rol '%s' para realizar esta operación",
                            requiredRole)
            );
        }

        log.debug("User '{}' has required role '{}'", userId, requiredRole);
        return Mono.empty();
    }

    /**
     * Requiere que el usuario tenga al menos uno de los roles especificados
     */
    public Mono<Void> requireAnyRole(ServerWebExchange exchange, String... requiredRoles) {
        String userId = getUserId(exchange);
        List<String> userRoles = getUserRoles(exchange);

        if (!hasAnyRole(exchange, requiredRoles)) {
            log.warn("Access denied: User '{}' with roles {} doesn't have any of required roles {}",
                    userId, userRoles, Arrays.asList(requiredRoles));

            throw new InsufficientPermissionsException(
                    String.format("Acceso denegado. Se requiere al menos uno de los siguientes roles: %s",
                            String.join(", ", requiredRoles))
            );
        }

        log.debug("User '{}' has at least one of the required roles", userId);
        return Mono.empty();
    }

    /**
     * Verifica si el usuario puede acceder al recurso (es admin o es el dueño)
     */
    public boolean canAccessResource(ServerWebExchange exchange, String resourceOwnerId) {
        String userId = getUserId(exchange);
        boolean isAdmin = hasRole(exchange, "ROLE_ADMIN");
        boolean isOwner = userId.equals(resourceOwnerId);

        log.debug("Access check - UserId: {}, ResourceOwnerId: {}, IsAdmin: {}, IsOwner: {}",
                userId, resourceOwnerId, isAdmin, isOwner);

        return isAdmin || isOwner;
    }

    /**
     * Requiere que el usuario pueda acceder al recurso
     */
    public Mono<Void> requireResourceAccess(ServerWebExchange exchange, String resourceOwnerId) {
        String userId = getUserId(exchange);

        if (!canAccessResource(exchange, resourceOwnerId)) {
            log.warn("Access denied: User '{}' cannot access resource owned by '{}'",
                    userId, resourceOwnerId);

      throw new ForbiddenException(
          "Acceso denegado. Solo puede acceder a sus propios recursos o ser administrador");
        }

        log.debug("User '{}' can access resource owned by '{}'", userId, resourceOwnerId);
        return Mono.empty();
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean isAdmin(ServerWebExchange exchange) {
        return hasRole(exchange, "ROLE_ADMIN");
    }

    /**
     * Requiere que el usuario sea administrador
     */
    public Mono<Void> requireAdmin(ServerWebExchange exchange) {
        return requireRole(exchange, "ROLE_ADMIN");
    }

    /**
     * Log de información de seguridad para debugging
     */
    public void logSecurityContext(ServerWebExchange exchange, String operation) {
        try {
            String userId = getUserId(exchange);
            List<String> roles = getUserRoles(exchange);
            String path = exchange.getRequest().getPath().value();
            String method = exchange.getRequest().getMethod().name();

            log.info("========== SECURITY CONTEXT ==========");
            log.info("Operation: {}", operation);
            log.info("Method: {} {}", method, path);
            log.info("User ID: {}", userId);
            log.info("Roles: {}", roles);
            log.info("======================================");
        } catch (Exception e) {
            log.error("Error logging security context", e);
        }
    }

}