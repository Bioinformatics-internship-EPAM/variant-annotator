package ru.spbstu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.spbstu.model.Variant;

import java.util.List;

public class AnnotatedVariantList {
    @JsonProperty("variantAnnotations")
    public List<Variant> variants;

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }
}
