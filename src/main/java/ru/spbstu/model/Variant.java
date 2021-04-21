package ru.spbstu.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Accessors(chain = true)
@Table(name = "variants", uniqueConstraints = @UniqueConstraint(columnNames = {"chrom", "pos", "ref", "alt"}))
@Entity
public class Variant {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "chrom", nullable = false)
  @JsonProperty("chrom")
  private String chromosome;
  @Column(name = "pos", nullable = false)
  @JsonProperty("pos")
  private Long position;
  @Column(name = "ref")
  @JsonProperty("ref")
  private String referenceBase;
  @Column(name = "alt")
  @JsonProperty("alt")
  private String alternateBase;
  @Column(name = "variant_code", nullable = false)
  private String variantCode;
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonProperty("annotations")
  private List<Annotation> annotations = new ArrayList<>();

  public Variant addAnnotation(Annotation annotation) {
    this.annotations.add(annotation);
    annotation.setVariant(this);
    return this;
  }

  // TODO: remove this after migrating to PostgreSQL
  @PreUpdate
  @PrePersist
  private void updateVariantCode() {
    variantCode = Stream.of(this.chromosome, ":", position.toString(), ":", referenceBase, ">", alternateBase)
        .filter(Objects::nonNull)
        .collect(Collectors.joining());
  }
}
