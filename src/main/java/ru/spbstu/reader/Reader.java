package ru.spbstu.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

@FunctionalInterface
public interface Reader<T> {
    Stream<T> read(final InputStream inputStream) throws IOException;
}
