package ru.spbstu.controller;

import com.sun.istack.NotNull;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.spbstu.repository.VariantRepository;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
//@WebMvcTest(ListAnnotationsController.class)
public class ListAnnotationsControllerTest {

    private static final String LIST_ANNOTATIONS_URL = "/getAnnotatedVariants";
    private static final String CHROM1 = "chr1";
    private static final String CHROM2 = "chr2";
    private static final long POS1 = 123L;
    private static final long POS2 = 234L;
    private static final String REF1 = "A";
    private static final String REF2 = "AC";
    private static final String ALT1 = "AG";
    private static final String ALT2 = "C";

    @Mock
    private VariantRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private ListAnnotationsController controller;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test() throws Exception {
//        Optional<Variant> variant1 = Optional.of(generateVariant(CHROM1, POS1, REF1, ALT1, null));
//        Mockito.when(repository.findVariant(CHROM1, POS1, REF1, ALT1)).thenReturn(variant1);
        Mockito.doNothing().when(repository.findVariant(CHROM1, POS1, REF1, ALT1));

//        Optional<Variant> variant2 = Optional.of(generateVariant(CHROM2, POS1, REF1, ALT1, null));
//        Mockito.when(repository.findVariant(CHROM2, POS2, REF2, ALT2)).thenReturn(variant2);
        Mockito.doNothing().when(repository.findVariant(CHROM2, POS2, REF2, ALT2));

        String path = "resources/java/variants_request_body.json";
        String content = Files.readString(Path.of(path), StandardCharsets.UTF_8);

        mockMvc.perform(post(LIST_ANNOTATIONS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());

        Mockito.verify(repository).findVariant(CHROM1, POS1, REF1, ALT1);
        Mockito.verify(repository).findVariant(CHROM2, POS2, REF2, ALT2);
    }

//    private Variant generateVariant(@NotNull String chromosome, @NotNull Long position, @NotNull String referenceBase,
//                                    @NotNull String alternateBase, Annotation[] annotations) {
//        Variant variant = new Variant();
//        variant.setChromosome(chromosome)
//                .setPosition(position)
//                .setReferenceBase(referenceBase)
//                .setAlternateBase(alternateBase);
//        for (Annotation annotation: annotations) {
//            variant.addAnnotation(annotation);
//        }
//        return variant;
//    }
}
