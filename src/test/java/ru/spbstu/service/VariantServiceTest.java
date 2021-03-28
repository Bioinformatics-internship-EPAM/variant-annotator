package ru.spbstu.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.repository.VariantRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class VariantServiceTest {

    private static final String DB_NAME = "db";
    private static final VcfRecord VCF_RECORD_1 = new VcfRecord("chr1", 109, ".", "A", "T", Map.of("AC", "1", "AN", "2", "AF", "0.50"));
    private static final VcfRecord VCF_RECORD_2 = new VcfRecord("chr2", 147, ".", "C", "A", Map.of("AC", "1", "AN", "2", "AF", "0.55"));

    @Mock
    VariantRepository variantRepository;

    @InjectMocks
    VariantService variantService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenNewVcfRecords_whenUpsertVcfRecords_thenVariantsAreSaved() {
        final var variant1 = Variant.from(VCF_RECORD_1, DB_NAME);
        final var variant2 = Variant.from(VCF_RECORD_2, DB_NAME);

        given(variantRepository.findVariant(variant1.getChromosome(), variant1.getPosition(),
                variant1.getReferenceBase(), variant1.getAlternateBase())).willReturn(Optional.empty());
        given(variantRepository.findVariant(variant2.getChromosome(), variant2.getPosition(),
                variant2.getReferenceBase(), variant2.getAlternateBase())).willReturn(Optional.empty());

        variantService.upsert(Stream.of(VCF_RECORD_1, VCF_RECORD_2), DB_NAME);

        then(variantRepository).should(times(1)).save(variant1);
        then(variantRepository).should(times(1)).save(variant2);
    }

    @Test
    void givenVariantExistInDb_whenUpsertVcfRecords_thenVariantIsUpdated() {
        final var vcfRecord = new VcfRecord(VCF_RECORD_1.getChrom(), VCF_RECORD_1.getPos(), VCF_RECORD_1.getId(),
                VCF_RECORD_1.getRef(), VCF_RECORD_1.getAlt(), Map.of("AC", "1", "AN", "2", "AF", "0.55"));
        final var variant = Variant.from(vcfRecord, DB_NAME);
        final var savedVariant = Variant.from(VCF_RECORD_1, DB_NAME);

        given(variantRepository.findVariant(variant.getChromosome(), variant.getPosition(),
                variant.getReferenceBase(), variant.getAlternateBase())).willReturn(Optional.of(savedVariant));

        variantService.upsert(Stream.of(vcfRecord), DB_NAME);

        variant.getAnnotations().forEach(savedVariant::addAnnotation);
        then(variantRepository).should(times(1)).save(savedVariant);
    }

    @Test
    void givenVariantExistInDb_whenUpsertVcfRecords_thenVariantIsNotUpdated() {
        final var variant = Variant.from(VCF_RECORD_1, DB_NAME);

        given(variantRepository.findVariant(variant.getChromosome(), variant.getPosition(),
                variant.getReferenceBase(), variant.getAlternateBase())).willReturn(Optional.of(variant));

        variantService.upsert(Stream.of(VCF_RECORD_1), DB_NAME);

        then(variantRepository).should(times(1)).save(variant);
        Assertions.assertThat(variant.getAnnotations().size()).isEqualTo(1);
    }

    @Test
    void givenNewVcfRecord_whenUpsertVariant_thenVariantIsSaved() {
        final var variant = Variant.from(VCF_RECORD_1, DB_NAME);

        given(variantRepository.findVariant(variant.getChromosome(), variant.getPosition(),
                variant.getReferenceBase(), variant.getAlternateBase())).willReturn(Optional.empty());

        variantService.upsert(variant);

        then(variantRepository).should(times(1)).save(variant);
    }

    @Test
    void givenVariantExistInDb_whenUpsertVariant_thenVariantIsUpdated() {
        final var vcfRecord = new VcfRecord(VCF_RECORD_1.getChrom(), VCF_RECORD_1.getPos(), VCF_RECORD_1.getId(),
                VCF_RECORD_1.getRef(), VCF_RECORD_1.getAlt(), Map.of("AC", "1", "AN", "2", "AF", "0.55"));
        final var variant = Variant.from(vcfRecord, DB_NAME);
        final var savedVariant = Variant.from(VCF_RECORD_1, DB_NAME);

        given(variantRepository.findVariant(variant.getChromosome(), variant.getPosition(),
                variant.getReferenceBase(), variant.getAlternateBase())).willReturn(Optional.of(savedVariant));

        variantService.upsert(variant);

        variant.getAnnotations().forEach(savedVariant::addAnnotation);
        then(variantRepository).should(times(1)).save(savedVariant);
    }

    @Test
    void givenVariantExistInDb_whenUpsertVariant_thenVariantIsNotUpdated() {
        final var variant = Variant.from(VCF_RECORD_1, DB_NAME);

        given(variantRepository.findVariant(variant.getChromosome(), variant.getPosition(),
                variant.getReferenceBase(), variant.getAlternateBase())).willReturn(Optional.of(variant));

        variantService.upsert(variant);

        then(variantRepository).should(times(1)).save(variant);
        Assertions.assertThat(variant.getAnnotations().size()).isEqualTo(1);
    }
}
