package de.tschmitz.rest.bookmarks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.TypeReferences.CollectionModelType;
import org.springframework.hateoas.server.core.TypeReferences.EntityModelType;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;

/**
 * This tests demonstrate how to consume the bookmark rest service with a RestTemplate.
 * <p>
 * To autowire the TestRestTemplate we have to use a WebEnvironment with a
 * web application context (e.g. WebEnvironment.RANDOM_PORT).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class BookmarkRestServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private URI baseUrl;

    @BeforeEach
    public void beforeEach() {
        baseUrl = URI.create(String.format("http://localhost:%s/api", port));
    }

    @Test
    public void shouldRetrieveHalJsonWithRootLinks() {
        // Perform a GET on base url
        RequestEntity<Void> requestEntity = RequestEntity.get(baseUrl).accept(HAL_JSON).build();
        EntityModel<Object> rootLinks = restTemplate.exchange(requestEntity, new EntityModelType<Object>() {
        }).getBody();

        // Assert that we got three links in response
        assertThat(rootLinks).isNotNull();
        assertThat(rootLinks.getLinks()).hasSize(3);

        String bookmarksHref = rootLinks.getLinks().getRequiredLink("bookmarks").getHref();
        assertThat(bookmarksHref).isEqualTo(baseUrl.toString() + "/bookmarks{?page,size,sort}");

        String tagsHref = rootLinks.getLinks().getRequiredLink("tags").getHref();
        assertThat(tagsHref).isEqualTo(baseUrl.toString() + "/tags{?page,size,sort}");

        String profilesHref = rootLinks.getLinks().getRequiredLink("profile").getHref();
        assertThat(profilesHref).isEqualTo(baseUrl.toString() + "/profile");
    }

    @Test
    public void shouldReturnAllBookmarks_andReturn200() {
        URI bookmarksUrl = URI.create(baseUrl + "/bookmarks");

        // Perform GET on url to list bookmarks
        RequestEntity<Void> requestEntity = RequestEntity.get(bookmarksUrl).accept(HAL_JSON).build();
        ResponseEntity<CollectionModel<Bookmark>> responseEntity = restTemplate.exchange(requestEntity, new CollectionModelType<Bookmark>() {
        });

        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);

        CollectionModel<Bookmark> bookmarks = responseEntity.getBody();
        assertThat(bookmarks).isNotNull();
        // Should contain at least four bookmarks, according to data-h2.sql
        assertThat(bookmarks.getContent().size()).isGreaterThanOrEqualTo(4);
        bookmarks.getContent().forEach(bookmark -> assertThat(bookmark.getTitle()).isNotNull());
    }

    @Test
    public void shouldAddNewBookmark_andReturn201() {
        URI bookmarksUrl = URI.create(baseUrl + "/bookmarks");

        Bookmark bookmark = new Bookmark();
        bookmark.setTitle("DuckDuckGo");
        bookmark.setHref("https://duckduckgo.com");

        RequestEntity<Bookmark> requestEntity = RequestEntity.post(bookmarksUrl).accept(HAL_JSON).body(bookmark);
        ResponseEntity<Bookmark> responseEntity = restTemplate.exchange(requestEntity, Bookmark.class);

        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);

        Bookmark savedBookmark = responseEntity.getBody();
        assertThat(savedBookmark.getTitle()).isEqualTo("DuckDuckGo");
        assertThat(savedBookmark.getAdded()).isNotNull();
    }

    /**
     * This test demonstrates how to bind one resource to another.
     * <p>
     * In this case we bind a {@link Tag} to a {@link Bookmark}.
     */
    @Test
    public void shouldAddExistingTagToBookmark_andReturn204() {
        // We want associate tag with id 2 to bookmark with id 1
        URI bookmarksUrl = URI.create(baseUrl + "/bookmarks/1/tags");
        URI tagUrl = URI.create(baseUrl + "/tags/2");

        // Perform a PUT on bookmarks url. Content-Type must be 'text/uri-list'.
        RequestEntity<String> requestEntity = RequestEntity.put(bookmarksUrl)
                .contentType(new MediaType("text", "uri-list"))
                .body(tagUrl.toString());
        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(204);
    }

    @Test
    public void shouldNotAddTagToNotExistingBookmark_andReturn404() {
        URI bookmarksUrl = URI.create(baseUrl + "/bookmarks/100/tags");
        URI tagUrl = URI.create(baseUrl + "/tags/2");

        RequestEntity<String> requestEntity = RequestEntity.put(bookmarksUrl)
                .contentType(new MediaType("text", "uri-list"))
                .body(tagUrl.toString());
        ResponseEntity<Void> responseEntity = restTemplate.exchange(requestEntity, Void.class);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    }
}