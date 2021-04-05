package utils;

import org.apache.commons.lang3.RandomStringUtils;
import ru.spbstu.reader.dto.VcfRecord;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class TestUtils {

    private TestUtils() {
        // not instantiable
    }

    public static VcfRecord createRandomVcfRecord() {
        final var chrom = RandomStringUtils.randomAlphabetic(4);
        final var pos = ThreadLocalRandom.current().nextInt(1000);
        final var ref = RandomStringUtils.randomAlphabetic(1).toUpperCase();
        final var alt = RandomStringUtils.randomAlphabetic(1).toUpperCase();
        final var info = Map.of(
                "AC", RandomStringUtils.randomNumeric(1),
                "AN", RandomStringUtils.randomNumeric(1),
                "AF", RandomStringUtils.randomNumeric(1));
        return new VcfRecord(chrom, pos, ".", ref, alt, info);
    }
}
