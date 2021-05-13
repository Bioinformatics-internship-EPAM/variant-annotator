package ru.spbstu.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.spbstu.dto.VariantListDto;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.Reader;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.repository.VariantRepository;
import ru.spbstu.service.impl.VariantServiceImpl;
import utils.TestUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static ru.spbstu.common.Constants.*;

class VariantServiceTest {

    private static final int BATCH_SIZE = 5;

    @Mock
    VariantRepository variantRepository;

    @Mock
    Reader<VcfRecord> vcfReader;

    @InjectMocks
    VariantServiceImpl variantService;

    @BeforeEach
    void init() throws IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        FieldUtils.writeField(variantService, "saveBatchSize", BATCH_SIZE, true);
    }

    @Test
    void givenInputStream_whenSaveVcfRecords_thenVariantsAreSaved() throws Exception {
        final var variants = List.of(VARIANT_1, VARIANT_2);

        given(vcfReader.read(any())).willReturn(Stream.of(VCF_RECORD_1, VCF_RECORD_2));
        given(variantRepository.saveAll(variants)).willReturn(variants);

        variantService.save(InputStream.nullInputStream(), DB_NAME);

        then(vcfReader).should(times(1)).read(any());
        then(variantRepository).should(times(1)).saveAll(variants);
    }

    @Test
    void givenInputStream_whenSaveVcfRecordsWithBatch_thenVariantsAreSavedWithBatch() throws Exception {
        final var vcfRecords = Stream.generate(TestUtils::createRandomVcfRecord)
                .limit(ThreadLocalRandom.current().nextInt(100))
                .collect(Collectors.toList());
        final var variants = vcfRecords.stream()
                .map(Variant::from)
                .collect(Collectors.toList());
        final var variantsBatches = Lists.partition(variants, BATCH_SIZE);

        given(vcfReader.read(any())).willReturn(vcfRecords.stream());
        variantsBatches.forEach(batch -> given(variantRepository.saveAll(batch)).willReturn(batch));

        variantService.saveWithBatch(InputStream.nullInputStream(), DB_NAME);

        then(vcfReader).should(times(1)).read(any());
        then(variantRepository).should(times(variantsBatches.size())).saveAll(any());
        variantsBatches.forEach(batch -> then(variantRepository).should(times(1)).saveAll(batch));
    }

    @Test
    void givenAnnotatedVariants_whenGettingVariants_thenReturnAnnotatedVariants() {
        given(variantRepository.findVariant(eq(CHR1), eq(POS1), eq(REF1), eq(ALT1))).willReturn(Optional.of(ANNOTATED_VARIANT_1));
        given(variantRepository.findVariant(eq(CHR2), eq(POS2), eq(REF2), eq(ALT2))).willReturn(Optional.of(VARIANT_2));
        given(variantRepository.findVariant(eq(CHR1), eq(POS2), eq(REF1), eq(ALT2))).willReturn(Optional.empty());

        VariantListDto requestedVariantListDto = new VariantListDto(Arrays.asList(VARIANT_1, VARIANT_2, VARIANT_3));

        VariantListDto annotatedVariantListDto = variantService.getAnnotatedVariants(requestedVariantListDto);

        List<Variant> variants = annotatedVariantListDto.getVariants();
        Assertions.assertEquals(variants.size(), 2);
        Assertions.assertEquals(variants.get(0).getAnnotations().size(), 1);
        Assertions.assertEquals(variants.get(1).getAnnotations().size(), 0);
    }
}
