package de.tschmitz.rest.bookmarks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data repository to perform CRUD operations on bookmark table.
 * <p>
 * As long as we have spring-boot-starter-data-rest as dependency the Spring Data REST framework
 * will expose a discoverable REST API for bookmarks on top of this repository.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
