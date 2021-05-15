package ru.spbstu.reader;

import htsjdk.variant.vcf.VCFIteratorBuilder;
import org.springframework.stereotype.Component;
import ru.spbstu.reader.dto.VcfRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

@Component
@SuppressWarnings("squid:S2095")
public class VcfReader implements Reader<VcfRecord> {

    public Stream<VcfRecord> read(final InputStream inputStream) throws IOException {
        return new VCFIteratorBuilder().open(inputStream).stream()
                .map(VcfRecord::from);
    }
}
