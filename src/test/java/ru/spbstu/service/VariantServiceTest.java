package ru.spbstu.service;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.Reader;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.repository.VariantRepository;
import ru.spbstu.service.impl.VariantServiceImpl;
import utils.TestUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class VariantServiceTest {

    private static final String DB_NAME = "db";
    private static final VcfRecord VCF_RECORD_1 = new VcfRecord("chr1", 109, ".", "A", "T", Map.of("AC", "1", "AN", "2", "AF", "0.50"));
    private static final VcfRecord VCF_RECORD_2 = new VcfRecord("chr2", 147, ".", "C", "A", Map.of("AC", "1", "AN", "2", "AF", "0.55"));
    private static final Variant VARIANT_1 = Variant.newInstance(VCF_RECORD_1, DB_NAME);
    private static final Variant VARIANT_2 = Variant.newInstance(VCF_RECORD_2, DB_NAME);
    private static final int BATCH_SIZE = 5;

    @Mock
    VariantRepository variantRepository;

    @Mock
    Reader<VcfRecord> vcfReader;

    @InjectMocks
    VariantServiceImpl variantService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
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

        variantService.saveWithBatch(InputStream.nullInputStream(), DB_NAME, BATCH_SIZE);

        then(vcfReader).should(times(1)).read(any());
        then(variantRepository).should(times(variantsBatches.size())).saveAll(any());
        variantsBatches.forEach(batch -> then(variantRepository).should(times(1)).saveAll(batch));
    }
}
