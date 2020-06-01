package de.tschmitz.rest.bookmarks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Boot application class to start the REST service.
 */
@SpringBootApplication
@EnableJpaAuditing
public class BookmarkRestService {

    public static void main(String[] args) {
        SpringApplication.run(BookmarkRestService.class, args);
    }
}
