package ru.spbstu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@Table(name = "variants", uniqueConstraints = @UniqueConstraint(columnNames = {"chrom", "pos", "ref", "alt"}))
@Entity
public class Variant {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "chrom", nullable = false)
  private String chromosome;
  @Column(name = "pos", nullable = false)
  private Long position;
  @Column(name = "ref")
  private String referenceBase;
  @Column(name = "alt")
  private String alternateBase;
  @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Annotation> annotations = new ArrayList<>();

  public Variant addAnnotation(Annotation annotation) {
    this.annotations.add(annotation);
    annotation.setVariant(this);
    return this;
  }
}
