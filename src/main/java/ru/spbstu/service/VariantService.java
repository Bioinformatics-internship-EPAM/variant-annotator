package ru.spbstu.service;

import ru.spbstu.model.Variant;

import java.io.IOException;
import java.io.InputStream;

public interface VariantService {

    Iterable<Variant> save(InputStream inputStream, String dbName) throws IOException;
    void saveWithBatch(InputStream inputStream, String dbName) throws IOException;
}
