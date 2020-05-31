package de.tschmitz.rest.bookmarks;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

/**
 * Testing method security of {@link BookmarkRepository} and {@link TagRepository} requires
 * to {@link EnableGlobalMethodSecurity}.
 * <p>
 * This is not necessarily needed for securing up the Data REST service methods, therefore it is only
 * enabled for tests.
 */
@TestConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RepositorySecurityTestConfig {

    /**
     * Adds Spring Security's integration with Spring Data, e.g. for accessing principal object
     * within {@code Query} annotations, like done in {@link BookmarkRepository#findAll(Pageable)}.
     * <p>
     * Outside of tests this is automatically added by Spring Boot auto configuration
     * as long as spring-security-data is in classpath.
     */
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
