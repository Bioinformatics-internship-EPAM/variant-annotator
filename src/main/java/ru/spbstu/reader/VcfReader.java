package ru.spbstu.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@FunctionalInterface
public interface VcfReader<T> {
    List<T> read(final InputStream inputStream) throws IOException;
}
