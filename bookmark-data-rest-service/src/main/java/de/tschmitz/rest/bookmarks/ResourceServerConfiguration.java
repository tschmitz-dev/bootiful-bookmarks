package de.tschmitz.rest.bookmarks;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * OAuth resource server configuration.
 * <p>
 * See also security.oauth2.resource.xyz and security.oauth2.client.xyz properties in application.yml.
 * <p>
 * To disable the resource server initialisation set the spring profile to <code>disable-oauth-resource-server</code>.
 */
@Configuration
@EnableResourceServer
@Profile("!disable-oauth-resource-server")
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("api");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //@formatter:off
        http
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeRequests(ar -> ar
                    // Allow access to HAL browser
                    .antMatchers("/api/browser/**").permitAll()
                    .anyRequest().authenticated());
        //@formatter:on
    }
}
