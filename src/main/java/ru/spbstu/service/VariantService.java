package ru.spbstu.service;

import ru.spbstu.model.Variant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface VariantService {

    Iterable<Variant> save(InputStream inputStream, String dbName) throws IOException;
    void saveWithBatch(InputStream inputStream, String dbName) throws IOException;
    Optional<Variant> find(String chromosome, Long position, String referenceBase, String alternateBase);
}
