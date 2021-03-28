package ru.spbstu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.repository.VariantRepository;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VariantService {

    private final VariantRepository variantRepository;

    public void upsert(final Stream<VcfRecord> vcfRecords,
                       final String dbName) {
        vcfRecords.map(vcfRecord -> Variant.from(vcfRecord, dbName))
                .forEach(this::upsert);
    }

    public void upsert(final Variant variant) {
        final var variantToSave = variantRepository.findVariant(
                variant.getChromosome(), variant.getPosition(),
                variant.getReferenceBase(), variant.getAlternateBase()).orElse(variant);
        variant.getAnnotations().forEach(variantToSave::addAnnotation);
        variantRepository.save(variantToSave);
    }
}
