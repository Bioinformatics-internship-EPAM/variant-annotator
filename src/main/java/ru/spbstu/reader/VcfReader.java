package ru.spbstu.reader;

import htsjdk.variant.vcf.VCFIteratorBuilder;
import ru.spbstu.reader.dto.VcfRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class VcfReader implements Reader<VcfRecord> {

    public Stream<VcfRecord> read(final InputStream inputStream) throws IOException {
        return new VCFIteratorBuilder().open(inputStream).stream()
                .map(VcfRecord::from);
    }
}