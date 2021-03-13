package ru.spbstu.reader.dto;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class VcfRecord {
    private final String chrom;
    private final int pos;
    private final String id;
    private final Allele ref;
    private final Allele alt;
    private final Map<String, Object> info;

    public static VcfRecord from(final VariantContext variantContext) {
        return new VcfRecord(
                variantContext.getContig(),
                variantContext.getEnd(),
                variantContext.getID(),
                variantContext.getReference(),
                variantContext.getAlternateAllele(0),
                variantContext.getAttributes());
    }

    @Override
    public String toString() {
        return String.format("VcfRecord{chrom=%s, pos=%s, id=%s, ref=%s, alt='%s, info=%s}",
                chrom, pos, id, ref.getBaseString(), alt, info);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VcfRecord vcfRecord = (VcfRecord) o;
        return pos == vcfRecord.pos && chrom.equals(vcfRecord.chrom) && id.equals(vcfRecord.id) && ref.equals(vcfRecord.ref) && alt.equals(vcfRecord.alt) && info.equals(vcfRecord.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chrom, pos, id, ref, alt, info);
    }
}
