package de.tschmitz.rest.bookmarks;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import java.util.Collections;

@TestConfiguration
public class BookmarkRestServiceTestConfig {

    /**
     * For tests without SecurityAutoConfiguration we need to to provide
     * a bean of {@link SecurityEvaluationContextExtension}. This is because we use
     * a security principal object in our data Query annotations.
     *
     * An example for such a query that is used in {@link BookmarkRepository}:
     * <code>@Query("select e from #{#entityName} e where e.userId = ?#{principal?.username}")</code>
     */
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        // To let a query like "select e from #{#entityName} e where e.userId = ?#{principal?.username}"
        // work in tests without throwing an exception we need a fake authentication.
        User user = new User("bud", "spencer",
                Collections.singletonList(new SimpleGrantedAuthority("USER")));
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        return new SecurityEvaluationContextExtension(auth);
    }
}
