package de.tschmitz.rest.bookmarks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Data repository to perform CRUD operations on tag table.
 * <p>
 * As long as we have spring-boot-starter-data-rest as dependency the Spring Data REST framework
 * will expose a discoverable REST API for tags on top of this repository.
 */
public interface TagRepository extends Repository<Tag, Long> {

    /**
     * Saves a given tag.
     *
     * @param tag must not be {@literal null}.
     * @return the saved tag; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal tag} is {@literal null}.
     */
    Tag save(Tag tag);

    /**
     * Retrieves a tag by its id.
     *
     * @param id must not be {@literal null}.
     * @return the tag with the given id or {@literal Optional#empty()} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    Optional<Tag> findById(Long id);

    /**
     * Returns a {@link Page} of tags meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable must not be {@literal null}.
     * @return a page of entities
     */
    Page<Tag> findAll(Pageable pageable);

    /**
     * Deletes the tag with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    void deleteById(Long id);

    /**
     * Deletes a given tag.
     *
     * @param tag must not be {@literal null}.
     * @throws IllegalArgumentException in case the given tag is {@literal null}.
     */
    void delete(Tag tag);

    /**
     * Deletes the given tags.
     *
     * @param tags must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal tags} or one of its tags is {@literal null}.
     */
    void deleteAll(Iterable<Tag> tags);

    /**
     * Deletes all tags.
     */
    void deleteAll();
}
