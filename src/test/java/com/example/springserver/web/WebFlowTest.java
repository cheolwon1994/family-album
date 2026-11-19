package com.example.springserver.web;

import com.example.springserver.SpringServerApplication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = SpringServerApplication.class, properties = {
        "app.bootstrap.admin-email=owner@example.com",
        "app.bootstrap.admin-password=initial-secret",
        "app.storage.location=${java.io.tmpdir}/spring-server-test-uploads",
        "spring.datasource.url=jdbc:h2:mem:spring-server-test;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
class WebFlowTest {
    @Autowired private MockMvc mvc;

    @Test
    void loginPageIsAvailableToVisitors() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"))
                .andExpect(content().string(containsString("로그인")));
    }

    @Test
    void dashboardRequiresAuthentication() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void bootstrappedAdminCanLogInAndSeeSharedAlbumScreen() throws Exception {
        MockHttpSession session = loginAsAdmin();
        mvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("media/gallery"))
                .andExpect(content().string(containsString("날짜별 앨범")))
                .andExpect(content().string(containsString("오늘의 순간을 남겨요")));
    }

    @Test
    void adminInviteCanBeUsedOnlyOnce() throws Exception {
        MockHttpSession session = loginAsAdmin();
        MvcResult inviteResult = mvc.perform(post("/admin/invites").session(session).with(csrf()))
                .andExpect(status().is3xxRedirection()).andReturn();
        String code = (String) inviteResult.getFlashMap().get("newInviteCode");

        mvc.perform(post("/signup").with(csrf()).param("email", "member@example.com")
                        .param("password", "member-password").param("inviteCode", code))
                .andExpect(redirectedUrl("/login"));
        mvc.perform(post("/signup").with(csrf()).param("email", "another@example.com")
                        .param("password", "another-password").param("inviteCode", code))
                .andExpect(redirectedUrl("/signup"));
    }

    @Test
    void authenticatedUserCanUploadMediaToSharedAlbum() throws Exception {
        MockHttpSession session = loginAsAdmin();
        MockMultipartFile photo = new MockMultipartFile("file", "family.jpg", "image/jpeg", new byte[]{1, 2, 3});
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/media")
                        .file(photo).session(session).with(csrf()))
                .andExpect(status().is3xxRedirection());
        mvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("family.jpg")));
    }

    private MockHttpSession loginAsAdmin() throws Exception {
        MvcResult result = mvc.perform(post("/login").with(csrf())
                        .param("email", "owner@example.com").param("password", "initial-secret"))
                .andExpect(status().is3xxRedirection()).andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
