package de.tschmitz.rest.bookmarks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provides some security related tests for the bookmark REST service.
 * <p>
 * The tests are done with MockMvc as this is the easiest way to test security
 * on the web layer.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BookmarkRestServiceSecurityTest {

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BookmarkDto {
        private String userId;
        private String title;
        private String href;
    }

    private static String TOKEN_ATTR_NAME = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";

    @Autowired
    private MockMvc mockMvc;
    private CsrfToken csrfToken;

    @BeforeEach
    public void beforeEach() {
        HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());
    }

    @Test
    @WithMockUser("bud")
    public void shouldRetrieveBookmarksByUserBud() throws Exception {
        mockMvc.perform(get("/api/bookmarks").accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..bookmarks.*", hasSize(4)))
                .andExpect(jsonPath("$..page.totalElements").value(contains(4)))
                .andExpect(jsonPath("$..bookmarks[0].userId").value(contains("bud")))
                .andExpect(jsonPath("$..bookmarks[1].userId").value(contains("bud")))
                .andExpect(jsonPath("$..bookmarks[2].userId").value(contains("bud")))
                .andExpect(jsonPath("$..bookmarks[3].userId").value(contains("bud")));
    }

    @Test
    @WithMockUser("terence")
    public void shouldRetrieveBookmarksByUserTerence() throws Exception {
        mockMvc.perform(get("/api/bookmarks").accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..bookmarks.*", hasSize(4)))
                .andExpect(jsonPath("$..page.totalElements").value(contains(4)))
                .andExpect(jsonPath("$..bookmarks[0].userId").value(contains("terence")))
                .andExpect(jsonPath("$..bookmarks[1].userId").value(contains("terence")))
                .andExpect(jsonPath("$..bookmarks[2].userId").value(contains("terence")))
                .andExpect(jsonPath("$..bookmarks[3].userId").value(contains("terence")));
    }

    @Test
    @WithMockUser("unknown")
    public void shouldRetrieveNoBookmarks() throws Exception {
        mockMvc.perform(get("/api/bookmarks").accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..bookmarks.*", hasSize(0)))
                .andExpect(jsonPath("$..page.totalElements").value(is(newArrayList(0))));
    }

    @Test
    @WithMockUser("buddy")
    public void shouldSaveNewBookmark() throws Exception {
        Bookmark newBookmark = new Bookmark();
        newBookmark.setTitle("DZone");
        newBookmark.setHref("https://dzone.com/java-jdk-development-tutorials-tools-news");

        ObjectMapper mapper = new ObjectMapper();
        String bookmarkJson = mapper.writeValueAsString(newBookmark);

        // Post the bookmark
        mockMvc.perform(
                post("/api/bookmarks")
                        .accept("application/json")
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .content(bookmarkJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$..self.href").isNotEmpty());
    }

    @Test
    @WithMockUser("clint")
    public void shouldModifyExistingBookmark() throws Exception {
        BookmarkDto newBookmark = new BookmarkDto();
        newBookmark.setTitle("DZone");
        newBookmark.setHref("https://dzone.com/java-jdk-development-tutorials-tools-news");

        ObjectMapper mapper = new ObjectMapper();
        String bookmarkJson = mapper.writeValueAsString(newBookmark);

        // Add new bookmark
        MvcResult mvcResult = mockMvc.perform(
                post("/api/bookmarks")
                        .accept("application/json")
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .content(bookmarkJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$..self.href").isNotEmpty())
                .andReturn();

        String resultJson = mvcResult.getResponse().getContentAsString();

        String hrefToBookmark = ((JSONArray) JsonPath.compile("$..self.href")
                .read(resultJson)).get(0).toString();

        BookmarkDto savedBookmark = mapper.readValue(bookmarkJson, BookmarkDto.class);
        String newHref = "http://anotherurl.com";
        savedBookmark.setHref(newHref);

        mockMvc.perform(
                put(hrefToBookmark)
                        .accept("application/json")
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .content(mapper.writeValueAsString(savedBookmark)))
                .andExpect(status().isOk());

        mockMvc.perform(
                get(hrefToBookmark)
                        .accept("application/json"))
                .andExpect(jsonPath("$..href", hasItem(newHref)));
    }
}
