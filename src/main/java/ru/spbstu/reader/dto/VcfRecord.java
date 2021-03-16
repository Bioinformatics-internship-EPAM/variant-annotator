package ru.spbstu.reader.dto;

import htsjdk.variant.variantcontext.VariantContext;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Data
@AllArgsConstructor
public class VcfRecord {
    private final String chrom;
    private final int pos;
    private final String id;
    private final String ref;
    private final String alt;
    private final Map<String, String> info;

    public static VcfRecord from(final VariantContext variantContext) {
        return new VcfRecord(
                variantContext.getContig(),
                variantContext.getEnd(),
                variantContext.getID(),
                variantContext.getReference().getBaseString(),
                variantContext.getAlternateAlleles().isEmpty() ? null : variantContext.getAlternateAllele(0).getBaseString(),
                variantContext.getAttributes().entrySet().stream()
                        .collect(toMap(Map.Entry::getKey, entry -> entry.getValue().toString())));
    }

    @Override
    public String toString() {
        return String.format("VcfRecord{chrom=%s, pos=%s, id=%s, ref=%s, alt='%s, info=%s}",
                chrom, pos, id, ref, alt, info);
    }
}
