package ru.spbstu.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.spbstu.model.Annotation;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.service.VariantService;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import javax.json.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VcfController.class)
class VcfControllerTest {

    private static final String PARSE_VCF_FILE_URL = "/vcfs";
    private static final String LIST_ANNOTATIONS_URL = "/getAnnotatedVariants";
    private static final MockMultipartFile VCF_STUB_FILE = new MockMultipartFile("vcf_file", "vcf.txt", "text/plain", "stub".getBytes());
    private static final String DB_NAME = "db";
    private static final String CHR1 = "chr1";
    private static final String CHR2 = "chr2";
    private static final long POS1 = 109L;
    private static final long POS2 = 147L;
    private static final String REF1 = "A";
    private static final String REF2 = "C";
    private static final String ALT1 = "T";
    private static final String ALT2 = "A";
    private static final Map<String, String> INFO1 = Map.of("AC", "1", "AN", "2", "AF", "0.50");
    private static final Map<String, String> INFO2 = Map.of("AC", "1", "AN", "2", "AF", "0.55");
    private static final VcfRecord VCF_RECORD_1 = new VcfRecord(CHR1, POS1, ".", REF1, ALT1, INFO1);
    private static final VcfRecord VCF_RECORD_2 = new VcfRecord(CHR2, POS2, ".", REF2, ALT2, INFO2);
    private static final VcfRecord VCF_RECORD_3 = new VcfRecord(CHR1, POS2, ".", REF1, ALT2, null);
    private static final Annotation ANNOTATION = new Annotation().setDbName(DB_NAME).setInfo(INFO1);
    private static final Variant VARIANT_1 = Variant.from(VCF_RECORD_1).setId(1L)
            .setAnnotations(new HashSet<Annotation>(Collections.singletonList(ANNOTATION)));
    private static final Variant VARIANT_2 = Variant.from(VCF_RECORD_2).setId(2L);
    private static final Variant VARIANT_3 = Variant.from(VCF_RECORD_3).setId(3L);

    @MockBean
    private VariantService variantService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenNewVcfRecords_whenParseVcfFile_thenReturnOk() throws Exception {
        final var variants = List.of(VARIANT_1, VARIANT_2);

        given(variantService.save(any(), eq(DB_NAME))).willReturn(variants);

        mockMvc.perform(multipart(PARSE_VCF_FILE_URL).file(VCF_STUB_FILE).param("db_name", DB_NAME))
                .andExpect(status().isOk());
    }

    @Test
    void givenParseError_whenParseVcfFile_thenReturnBadRequest() throws Exception {
        given(variantService.save(any(), eq(DB_NAME))).willThrow(IOException.class);

        mockMvc.perform(multipart(PARSE_VCF_FILE_URL).file(VCF_STUB_FILE).param("db_name", DB_NAME))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void givenAnnotatedVariants_whenGettingVariants_thenReturnOk() throws Exception {
        given(variantService.find(eq(CHR1), eq(POS1), eq(REF1), eq(ALT1))).willReturn(Optional.of(VARIANT_1));
        given(variantService.find(eq(CHR2), eq(POS2), eq(REF2), eq(ALT2))).willReturn(Optional.of(VARIANT_2));
        given(variantService.find(eq(CHR1), eq(POS2), eq(REF1), eq(ALT2))).willReturn(Optional.empty());

        String content = generateJsonVariantList(Arrays.asList(VARIANT_1, VARIANT_2, VARIANT_3));

        MvcResult result = mockMvc.perform(post(LIST_ANNOTATIONS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonReader reader = Json.createReader(new StringReader(responseContent));
        JsonObject json = reader.readObject();
        reader.close();

        JsonArray array = json.getJsonArray("variantAnnotations");
        Assertions.assertThat(array.size()).isEqualTo(2);

        JsonObject annotation = array.get(0).asJsonObject().getJsonArray("annotations").get(0).asJsonObject();
        Assertions.assertThat(annotation.getString("dbName")).isEqualTo(DB_NAME);
        Assertions.assertThat(annotation.get("info").asJsonObject()).isNotEmpty();

        Assertions.assertThat(array.get(1).asJsonObject().getJsonArray("annotations")).isNullOrEmpty();
    }

    private String generateJsonVariantList(Iterable<Variant> variants) {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        for(Variant variant: variants) {
            builder.add(Json.createObjectBuilder()
            .add("chrom", variant.getChromosome())
            .add("pos", variant.getPosition())
            .add("ref", variant.getReferenceBase())
            .add("alt", variant.getAlternateBase()));
        }

        return Json.createObjectBuilder()
                .add("variants", builder)
                .build()
                .toString();
    }
}
