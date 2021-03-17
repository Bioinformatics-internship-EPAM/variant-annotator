package ru.spbstu.reader;

import htsjdk.variant.vcf.VCFIteratorBuilder;
import ru.spbstu.reader.dto.VcfRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class VcfReaderImpl implements VcfReader<VcfRecord> {

    public List<VcfRecord> read(final InputStream inputStream) throws IOException {
        return new VCFIteratorBuilder().open(inputStream).stream()
                .map(VcfRecord::from)
                .collect(Collectors.toList());
    }
}
