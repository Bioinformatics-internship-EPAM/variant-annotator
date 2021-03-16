package ru.spbstu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VariantSearchRequest {
  private final String chromosome;
  private final Long position;
  private final String referenceBase;
  private final String alternateBase;
}
