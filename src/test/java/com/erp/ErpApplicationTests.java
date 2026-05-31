package com.erp;

import com.erp.core.repository.RoleRepository;
import com.erp.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Smoke tests: the Spring context boots, security seeding runs, and the public
 * login page renders. Protected pages redirect unauthenticated callers to login.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ErpApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void contextLoadsAndSeedsSecurityData() {
        assertThat(roleRepository.count()).isGreaterThan(0);
        assertThat(userRepository.findByEmailIgnoreCase("admin@erp.com")).isPresent();
    }

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sign in")));
    }

    @Test
    void protectedPageRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/crm/leads"))
                .andExpect(status().is3xxRedirection());
    }
}
