package ru.spbstu.service.impl;

import com.google.common.collect.Iterators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.Reader;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.repository.VariantRepository;
import ru.spbstu.service.VariantService;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {

    private final Reader<VcfRecord> vcfReader;
    private final VariantRepository variantRepository;

    @Override
    public Iterable<Variant> save(final InputStream inputStream,
                                  final String dbName) throws IOException {
        final var variants = vcfReader.read(inputStream)
                .map(vcfRecord -> Variant.newInstance(vcfRecord, dbName))
                .collect(Collectors.toList());
        return variantRepository.saveAll(variants);
    }

    @Override
    public void saveWithBatch(final InputStream inputStream,
                              final String dbName,
                              int batchSize) throws IOException {
        final var variants = vcfReader.read(inputStream)
                .map(vcfRecord -> Variant.newInstance(vcfRecord, dbName));
        Iterators.partition(variants.iterator(), batchSize).forEachRemaining(variantRepository::saveAll);
    }
}
