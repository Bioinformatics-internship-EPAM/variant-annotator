package ru.spbstu.reader;

import org.junit.jupiter.api.Test;
import ru.spbstu.reader.dto.VcfRecord;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VcfReaderTest {

    @Test
    public void testReadVcfFile() throws IOException {
        List<VcfRecord> result = VcfReader.read(new FileInputStream("src/test/resources/test_vcf_file.vcf"));
        assertEquals(8, result.size());
    }

    @Test
    public void testReadVcfRecord() throws IOException {
        final String headers = "##fileformat=VCFv4.0\n" +
                "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tNA12878";
        final String chrom = "chr1";
        final int pos = 115;
        final String id = "ss";
        final String ref = "A";
        final String alt = "T";
        final Map<String, Object> info = Map.of(
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

        List<VcfRecord> result = VcfReader.read(new ByteArrayInputStream(text.getBytes()));

        assertEquals(1, result.size());

        VcfRecord record = result.get(0);
        assertEquals(chrom, record.getChrom());
        assertEquals(pos, record.getPos());
        assertEquals(id, record.getId());
        assertEquals(ref, record.getRef().getBaseString());
        assertEquals(alt, record.getAlt().getBaseString());
        assertEquals(info, record.getInfo());
    }

    private String mapToString(final Map<String, Object> map) {
        return map.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(";"));
    }

}
