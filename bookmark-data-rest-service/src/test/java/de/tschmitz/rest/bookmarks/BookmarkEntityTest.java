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
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests to assure that the {@literal CreatedBy} annotation is working
 * like expected.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BookmarkEntityTest {

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
    @WithMockUser("buddy")
    public void shouldSaveNewBookmarkAndSetUserId() throws Exception {
        Bookmark newBookmark = new Bookmark();
        newBookmark.setTitle("DZone");
        newBookmark.setHref("https://dzone.com/java-jdk-development-tutorials-tools-news");

        ObjectMapper mapper = new ObjectMapper();
        String bookmarkJson = mapper.writeValueAsString(newBookmark);

        // Post the bookmark and assert that userId of this bookmark is set
        ResultActions result = mockMvc.perform(
                post("/api/bookmarks")
                        .accept("application/json")
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .content(bookmarkJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(equalTo("buddy")));
    }

    @Test
    @WithMockUser("clint")
    public void userIdShouldNotBeModifiable() throws Exception {
        BookmarkDto newBookmark = new BookmarkDto();
        newBookmark.setTitle("Stadt Bremen");
        newBookmark.setHref("https://www.bremen.de/");

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
                .andReturn();

        String resultJson = mvcResult.getResponse().getContentAsString();
        // Get the href to currently saved bookmark from returned json
        String hrefToBookmark = ((JSONArray) JsonPath.compile("$..self.href")
                .read(resultJson)).get(0).toString();

        BookmarkDto savedBookmark = mapper.readValue(bookmarkJson, BookmarkDto.class);
        savedBookmark.setUserId("anotherUser");

        // Now assert that even if you try to change the userId it still is 'clint';
        String modifiedBookmark = mapper.writeValueAsString(savedBookmark);
        mockMvc.perform(
                put(hrefToBookmark)
                        .accept("application/json")
                        .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                        .param(csrfToken.getParameterName(), csrfToken.getToken())
                        .content(modifiedBookmark))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(equalTo("clint")));
    }
}
