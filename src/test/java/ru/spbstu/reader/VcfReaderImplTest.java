package ru.spbstu.reader;

import org.junit.jupiter.api.Test;
import ru.spbstu.reader.dto.VcfRecord;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VcfReaderImplTest {

    @Test
    void testReadVcfFile() throws IOException {
        final String testFileName = "test_vcf_file.vcf";
        final List<VcfRecord> expectedList = List.of(
                new VcfRecord("chr1", 109, ".", "A", "T",
                        Map.of("AC", "1", "AN", "2", "AF", "0.50")),
                new VcfRecord("chr3", 147, ".", "C", "A",
                        Map.of("AC", "1", "AN", "2", "AF", "0.55")),
                new VcfRecord("chr2", 177, ".", "A", "C",
                        Map.of("AC", "1", "AN", "2", "AF", "0.70"))
        );
        final List<VcfRecord> result = new VcfReaderImpl().read(getClass().getClassLoader().getResourceAsStream(testFileName));
        assertEquals(expectedList.size(), result.size());
        assertTrue(result.containsAll(expectedList));
        assertTrue(expectedList.containsAll(result));
    }

    @Test
    void testReadVcfRecord() throws IOException {
        final String headers = "##fileformat=VCFv4.0\n" +
                "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tNA12878";
        final String chrom = "chr1";
        final int pos = 115;
        final String id = "ss";
        final String ref = "A";
        final String alt = "T";
        final Map<String, String> info = Map.of(
                "AC", "1",
                "AF", "0.50",
                "AN", "2",
                "DP", "1019",
                "MQ", "19.20"
        );
        final String text = String.format(
                "%s\n%s\t%s\t%s\t%s\t%s\t0\tFDRtranche2.00to10.00+\t%s\tGT:AD:DP:GL:GQ"
                        + "\t0/1:610,327:308:-316.30,-95.47,-803.03:99",
                headers, chrom, pos, id, ref, alt, mapToString(info));

        final VcfRecord expectedRecord = new VcfRecord(chrom, pos, id, ref, alt, info);

        final List<VcfRecord> result = new VcfReaderImpl().read(new ByteArrayInputStream(text.getBytes()));

        assertEquals(1, result.size());
        assertEquals(expectedRecord, result.get(0));
    }

    private String mapToString(final Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(";"));
    }
}
