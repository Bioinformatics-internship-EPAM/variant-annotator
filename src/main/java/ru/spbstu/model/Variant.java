package ru.spbstu.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
}
