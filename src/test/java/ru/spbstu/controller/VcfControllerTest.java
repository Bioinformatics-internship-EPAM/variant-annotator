package ru.spbstu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.spbstu.dto.VariantListDto;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.service.UserService;
import ru.spbstu.service.VariantService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.spbstu.common.Constants.*;

@WebMvcTest(VcfController.class)
class VcfControllerTest {

    private static final String PARSE_VCF_FILE_URL = "/vcfs";
    private static final String LIST_ANNOTATIONS_URL = "/getAnnotatedVariants";
    private static final MockMultipartFile VCF_STUB_FILE = new MockMultipartFile("vcf_file", "vcf.txt", "text/plain", "stub".getBytes());

    @MockBean
    private VariantService variantService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username="admin", roles={"USER","ADMIN"})
    void givenNewVcfRecords_whenParseVcfFile_thenReturnOk() throws Exception {
        final var variants = List.of(VARIANT_1, VARIANT_2);

        given(variantService.save(any(), eq(DB_NAME))).willReturn(variants);

        mockMvc.perform(multipart(PARSE_VCF_FILE_URL).file(VCF_STUB_FILE).param("db_name", DB_NAME))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin", roles={"USER","ADMIN"})
    void givenParseError_whenParseVcfFile_thenReturnBadRequest() throws Exception {
        given(variantService.save(any(), eq(DB_NAME))).willThrow(IOException.class);

        mockMvc.perform(multipart(PARSE_VCF_FILE_URL).file(VCF_STUB_FILE).param("db_name", DB_NAME))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void givenAnnotatedVariants_whenGettingVariants_thenReturnOkAndServiceInvoked() throws Exception {
        String content = generateJsonVariantList(Arrays.asList(VARIANT_1, VARIANT_2, VARIANT_3));

        mockMvc.perform(post(LIST_ANNOTATIONS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andReturn();
        then(variantService).should(times(1)).getAnnotatedVariants(any());
    }

    private String generateJsonVariantList(Iterable<Variant> variants) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        VariantListDto variantListDto = new VariantListDto((List<Variant>) variants);
        return objectMapper.writeValueAsString(variantListDto);
    }
}
