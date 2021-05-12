package ru.spbstu.exception;

import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ru.spbstu.controller.VcfController;
import ru.spbstu.service.UserService;
import ru.spbstu.service.VariantService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VcfController.class)
class GlobalExceptionHandlerTest {
  private static final String PARSE_VCF_FILE_URL = "/vcfs";
  private static final MockMultipartFile VCF_STUB_FILE = new MockMultipartFile("vcf_file", "vcf.txt", "text/plain", "stub".getBytes());
  private static final String DB_NAME = "db";

  @MockBean
  private VariantService variantService;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  @WithMockUser(username="admin", roles={"USER","ADMIN"})
  void checkDataIntegrityExceptionThrown() throws Exception {
    given(variantService.save(any(), any())).willThrow(DataIntegrityViolationException.class);

    String content = mockMvc.perform(getMultipartRequest())
        .andExpect(status().isUnprocessableEntity())
        .andReturn().getResponse().getContentAsString();

    assertThat(content)
        .contains("Data integrity violation:");
  }

  @Test
  @WithMockUser(username="admin", roles={"USER","ADMIN"})
  void checkRuntimeExceptionThrown() throws Exception {
    given(variantService.save(any(), eq(DB_NAME))).willThrow(NotFoundException.class);

    String content = mockMvc.perform(getMultipartRequest())
        .andExpect(status().isUnprocessableEntity())
        .andReturn().getResponse().getContentAsString();
    assertThat(content)
        .contains("Something went wrong:");
  }

  private RequestBuilder getMultipartRequest() {
    return multipart(PARSE_VCF_FILE_URL).file(VCF_STUB_FILE).param("db_name", DB_NAME);
  }
}
