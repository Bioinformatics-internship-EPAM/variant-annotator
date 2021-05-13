package ru.spbstu.dto;

import lombok.Value;
import ru.spbstu.model.Variant;

import java.util.List;

@Value
public class VariantListDto {
    List<Variant> variants;
}
