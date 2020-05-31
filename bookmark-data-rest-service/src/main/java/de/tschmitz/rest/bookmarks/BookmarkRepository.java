package de.tschmitz.rest.bookmarks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Data repository to perform CRUD operations on bookmark table.
 * <p>
 * As long as we have spring-boot-starter-data-rest as dependency the Spring Data REST framework
 * will expose a discoverable REST API for bookmarks on top of this repository.
 */
public interface BookmarkRepository extends Repository<Bookmark, Long> {

    /**
     * Saves a given bookmark.
     *
     * @param bookmark must not be {@literal null}.
     * @return the saved bookmark; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal bookmark} is {@literal null}.
     */
    @PreAuthorize("(#bookmark.userId ?: principal?.username) == principal?.username")
    Bookmark save(Bookmark bookmark);

    /**
     * Retrieves a bookmark by its id.
     *
     * @param id must not be {@literal null}.
     * @return the bookmark with the given id or {@literal Optional#empty()} if none found.
     * @throws IllegalArgumentException if {@literal id} is {@literal null}.
     */
    @PostAuthorize("(returnObject.orElse(null)?.userId ?: principal?.username) == principal?.username")
    Optional<Bookmark> findById(Long id);

    /**
     * Returns a {@link Page} of bookmarks meeting the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable must not be {@literal null}.
     * @return a page of entities
     */
    @Query("select e from #{#entityName} e where e.userId = ?#{principal?.username}")
    Page<Bookmark> findAll(Pageable pageable);

    /**
     * Deletes the bookmark with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    @Modifying
    @Query("delete from #{#entityName} e where e.userId = ?#{principal?.username} and e.id = id")
    void deleteById(Long id);

    /**
     * Deletes a given bookmark.
     *
     * @param bookmark must not be {@literal null}.
     * @throws IllegalArgumentException in case the given bookmark is {@literal null}.
     */
    @PreAuthorize("(#bookmark.userId ?: principal?.username) == principal?.username")
    void delete(Bookmark bookmark);
}
