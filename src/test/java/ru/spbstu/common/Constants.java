package ru.spbstu.common;

import ru.spbstu.model.Annotation;
import ru.spbstu.model.Variant;
import ru.spbstu.reader.dto.VcfRecord;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public final class Constants {

    public static final String DB_NAME = "db";
    public static final String CHR1 = "chr1";
    public static final String CHR2 = "chr2";
    public static final long POS1 = 109L;
    public static final long POS2 = 147L;
    public static final String REF1 = "A";
    public static final String REF2 = "C";
    public static final String ALT1 = "T";
    public static final String ALT2 = "A";
    public static final Map<String, String> INFO1 = Map.of("AC", "1", "AN", "2", "AF", "0.50");
    public static final Map<String, String> INFO2 = Map.of("AC", "1", "AN", "2", "AF", "0.55");
    public static final VcfRecord VCF_RECORD_1 = new VcfRecord(CHR1, POS1, ".", REF1, ALT1, INFO1);
    public static final VcfRecord VCF_RECORD_2 = new VcfRecord(CHR2, POS2, ".", REF2, ALT2, INFO2);
    public static final VcfRecord VCF_RECORD_3 = new VcfRecord(CHR1, POS2, ".", REF1, ALT2, null);
    public static final Annotation ANNOTATION = new Annotation().setDbName(DB_NAME).setInfo(INFO1);
    public static final Variant VARIANT_1 = Variant.from(VCF_RECORD_1).setId(1L);
    public static final Variant VARIANT_2 = Variant.from(VCF_RECORD_2).setId(2L);
    public static final Variant VARIANT_3 = Variant.from(VCF_RECORD_3).setId(3L);
    public static final Variant ANNOTATED_VARIANT_1 = Variant.from(VCF_RECORD_1).setId(1L)
            .setAnnotations(new HashSet<>(Collections.singletonList(ANNOTATION)));
}
