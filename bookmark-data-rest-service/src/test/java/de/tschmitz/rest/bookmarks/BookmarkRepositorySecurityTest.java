package de.tschmitz.rest.bookmarks;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for method security annotations in {@link BookmarkRepository}.
 * <p>
 * Tests for repository methods that are annotated with {@literal PreAuthorize} or {@literal PostAuthorize}
 * need only a mocked user (@WithMockUser) but testing repository methods that use a principal object in
 * a custom query (@Query) requires an instance of {@literal SecurityEvaluationContextExtension}.
 *
 * @see RepositorySecurityTestConfig
 */
@ContextConfiguration(classes = {RepositorySecurityTestConfig.class, BookmarkRestService.class})
@DataJpaTest
class BookmarkRepositorySecurityTest {

    @Autowired
    private BookmarkRepository repository;

    @Test
    @WithMockUser(username = "demo")
    void saveBookmark_withValidUser_returnsSavedBookmark() {
        Bookmark b = new Bookmark();
        b.setTitle("Test-Bookmark");
        b.setHref("https://example.com");

        Bookmark saved = repository.save(b);
        assertThat(saved).isNotNull();
    }

    @Test
    @WithMockUser(username = "demo2")
    void saveBookmark_withOtherUserThenBookmarkOwner_throwsAccessDenied() {
        Bookmark b = new Bookmark();
        b.setTitle("Test-Bookmark");
        b.setHref("https://example.com");
        // Owner of this bookmark is another then given mock user
        b.setUserId("demo");

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> repository.save(b));
    }

    @Test
    @WithMockUser(username = "bud")
    void findById_isAllowedByBookmarkOwner() {
        assertThat(repository.findById(1L)).isNotEmpty();
    }

    @Test
    @WithMockUser(username = "invaliduser")
    void findById_isNotAllowedWithOtherUserThenBookmarkOwner() {
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> repository.findById(1L));
    }

    /**
     * As the BookmarkRepository#findAll method uses a custom query that depends
     * on security principal object this test requires a bean of SecurityEvaluationContextExtension
     * in context. Also this bean must have an instance of an Authentication. This is all configured
     * in {@link RepositorySecurityTestConfig}.
     */
    @Test
    @WithMockUser(username = "bud")
    void findAll_shouldOnlyReturnBookmarksOfGivenUser() {
        Page<Bookmark> all = repository.findAll(PageRequest.of(0, 10));
        assertThat(all.getTotalElements()).isEqualTo(4);
    }

    @Test
    @WithMockUser(username = "terence")
    void deleteById_withValidUser() {
        // terence is owner of bookmark with id 5
        Optional<Bookmark> byId = repository.findById(5L);
        assertThat(byId.isPresent()).isTrue();

        repository.delete(byId.get());

        byId = repository.findById(5L);
        assertThat(byId.isPresent()).isFalse();
    }

    @Test
    @WithMockUser(username = "bud")
    void deleteById_isNotAllowedWithOtherUserThenBookmarkOwner() {
        // terence is owner of bookmark with id 6 (see data-test.sql).
        // Try to delete. This will not throw any exception.
        repository.deleteById(6L);
        // But when we now try to find by id again it will throw
        // an AccessDeniedException. This implies that the deletion
        // did not work, like expected.
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> repository.findById(6L));
    }

    @Test
    @WithMockUser(username = "terence")
    void delete_withValidUser() {
        // terence is owner of bookmark with id 5
        Optional<Bookmark> byId = repository.findById(5L);
        assertThat(byId.isPresent()).isTrue();

        repository.delete(byId.get());

        byId = repository.findById(5L);
        assertThat(byId.isPresent()).isFalse();
    }

    @Test
    @WithMockUser(username = "bud")
    void delete_isNotAllowedWithOtherUserThenBookmarkOwner() {
        // terence is owner of bookmark with id 6
        Bookmark b = new Bookmark();
        b.setUserId("terence");
        b.setId(6L);

        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> repository.delete(b));
    }
}
