package ru.spbstu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.spbstu.reader.dto.VcfRecord;

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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Accessors(chain = true)
@Table(name = "variants", uniqueConstraints = @UniqueConstraint(columnNames = {"chrom", "pos", "ref", "alt"}))
@Entity
public class Variant {
    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
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
    @JsonIgnore
    private String variantCode;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("annotations")
    private Set<Annotation> annotations = new HashSet<>();

    public Variant addAnnotation(final Annotation annotation) {
        annotation.setVariant(this);
        annotations.add(annotation);
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

    public static Variant newInstance(final VcfRecord vcfRecord,
                                      final String dbName) {
        final var variant = from(vcfRecord);
        final var annotation = new Annotation()
                .setDbName(dbName)
                .setInfo(vcfRecord.getInfo());
        return variant.addAnnotation(annotation);
    }

    public static Variant from(final VcfRecord vcfRecord) {
        return new Variant().setChromosome(vcfRecord.getChrom())
                .setPosition((long) vcfRecord.getPos())
                .setReferenceBase(vcfRecord.getRef())
                .setAlternateBase(vcfRecord.getAlt());
    }
}
