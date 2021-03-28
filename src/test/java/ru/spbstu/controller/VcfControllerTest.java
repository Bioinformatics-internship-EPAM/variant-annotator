package ru.spbstu.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.spbstu.reader.Reader;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.service.VariantService;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VcfController.class)
class VcfControllerTest {

    private static final String PARSE_VCF_FILE_URL = "/vcfs";
    private static final MockMultipartFile VCF_STUB_FILE = new MockMultipartFile("vcf_file", "vcf.txt", "text/plain", "stub".getBytes());
    private static final String DB_NAME = "db";
    private static final VcfRecord VCF_RECORD_1 = new VcfRecord("chr1", 109, ".", "A", "T", Map.of("AC", "1", "AN", "2", "AF", "0.50"));
    private static final VcfRecord VCF_RECORD_2 = new VcfRecord("chr2", 147, ".", "C", "A", Map.of("AC", "1", "AN", "2", "AF", "0.55"));

    @MockBean
    private Reader<VcfRecord> vcfReader;

    @MockBean
    private VariantService variantService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenNewVcfRecords_whenParseVcfFile_thenReturnOk() throws Exception {
        final var vcfRecords = Stream.of(VCF_RECORD_1, VCF_RECORD_2);

        given(vcfReader.read(any())).willReturn(vcfRecords);
        doNothing().when(variantService).upsert(vcfRecords, DB_NAME);

        mockMvc.perform(multipart(PARSE_VCF_FILE_URL).file(VCF_STUB_FILE).param("db_name", DB_NAME))
                .andExpect(status().isOk());
    }

    @Test
    void givenParseError_whenParseVcfFile_thenReturnBadRequest() throws Exception {
        final var vcfRecords = Stream.of(VCF_RECORD_1, VCF_RECORD_2);

        given(vcfReader.read(any())).willThrow(IOException.class);
        doNothing().when(variantService).upsert(vcfRecords, DB_NAME);

        mockMvc.perform(multipart(PARSE_VCF_FILE_URL).file(VCF_STUB_FILE).param("db_name", DB_NAME))
                .andExpect(status().isBadRequest());
    }
}
