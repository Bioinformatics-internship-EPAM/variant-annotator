package ru.spbstu.reader;

import htsjdk.variant.vcf.VCFIterator;
import htsjdk.variant.vcf.VCFIteratorBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import ru.spbstu.reader.dto.VcfRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class VcfReader {

    public static List<VcfRecord> read(final InputStream inputStream) throws IOException {
        return read(new VCFIteratorBuilder().open(inputStream));
    }

    public static List<VcfRecord> read(final String path) throws IOException {
        return read(new VCFIteratorBuilder().open(path));
    }

    private static List<VcfRecord> read(final VCFIterator iterator) {
        return iterator.stream()
                .map(VcfRecord::from)
                .collect(Collectors.toList());
    }
}
