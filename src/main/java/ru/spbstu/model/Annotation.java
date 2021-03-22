package ru.spbstu.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Map;

@Data
@Accessors(chain = true)
@Table(name = "annotations")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
public class Annotation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "variant_id", nullable = false)
  private Variant variant;
  @Type(type = "jsonb")
  @Column(nullable = false, columnDefinition = "jsonb")
  private Map<String, String> info;
  @Column(name = "db_name")
  private String dbName;
}
