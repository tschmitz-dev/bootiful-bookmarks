package de.tschmitz.rest.bookmarks;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * AuditorAware implementation to set the userId of bookmarks automatically
 * as long as the userId attribute is annotated with {@literal CreatedBy}.
 * <p>
 * In addition to this the Bookmark entity, or others that use the CreatedBy annotation,
 * must be annotated with {@literal @EntityListeners(AuditingEntityListener.class)}. Beside this
 * Jpa auditing must be enabled with {@literal @EnableJpaAuditing}. This is done in {@link BookmarkRestService}.
 */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {

    @SuppressWarnings("NullableProblems")
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }
}