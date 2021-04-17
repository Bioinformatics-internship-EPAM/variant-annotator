package ru.spbstu.repository;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.spbstu.model.Annotation;
import ru.spbstu.model.Variant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AnnotationRepositoryTest {
  @Autowired
  private VariantRepository variantRepository;
  @Autowired
  private AnnotationRepository annotationRepository;

  @Test
  public void simpleInsert() {
    Variant variant = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("aaa"));

    Annotation info = annotationRepository.save(new Annotation()
        .setVariant(variant)
        .setInfo(Map.of("info", "1")));

    Optional<Annotation> variantInformation = annotationRepository.findById(info.getId());
    Assertions.assertThat(variantInformation)
        .isPresent();
    Assertions.assertThat(variantInformation.get().getVariant())
        .isEqualTo(variant);
  }

  @Test
  public void violateConstraints() {
    Assertions.assertThatThrownBy(() -> annotationRepository.save(new Annotation().setInfo(Map.of("info", "1"))))
        .isInstanceOf(DataIntegrityViolationException.class);
    Assertions.assertThatThrownBy(() -> annotationRepository.save(new Annotation().setVariant(new Variant().setId(1L)).setInfo(Map.of("info", "2"))))
        .isInstanceOf(DataIntegrityViolationException.class);
    Variant variant = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("aaa"));

    Assertions.assertThatThrownBy(() -> annotationRepository.save(new Annotation().setVariant(variant)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  public void findAllByVariantId() {
    Variant variant = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("aaa"));
    Variant variant2 = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(3L)
        .setReferenceBase("aaa"));
    annotationRepository.saveAll(List.of(
        new Annotation().setVariant(variant).setInfo(Map.of("info", "1")),
        new Annotation().setVariant(variant2).setInfo(Map.of("info", "2")),
        new Annotation().setVariant(variant).setInfo(Map.of("info", "3"))));

    List<Annotation> infos = annotationRepository.findAllByVariant_Id(variant.getId());
    Assertions.assertThat(infos)
        .hasSize(2);
    Assertions.assertThat(infos.stream().map(Annotation::getInfo).map(m -> m.get("info")))
        .containsOnly("1", "3");
  }

  @Test
  public void checkFindByVariantCodes() {
    Variant variant = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("aaa"));
    Variant variant2 = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(3L)
        .setReferenceBase("aaa"));
    Variant variant3 = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(4L)
        .setReferenceBase("bbb"));
    annotationRepository.saveAll(List.of(
        new Annotation().setVariant(variant).setInfo(Map.of("info", "1")),
        new Annotation().setVariant(variant2).setInfo(Map.of("info", "2")),
        new Annotation().setVariant(variant3).setInfo(Map.of("info", "3"))));

    List<Annotation> annotations = annotationRepository.findAllByVariant_VariantCodeIn(List.of("a:2:aaa>", "a:4:bbb>"));
    Assertions.assertThat(annotations).hasSize(2);
    Assertions.assertThat(annotations.stream().map(Annotation::getVariant).map(Variant::getId))
        .containsOnly(variant.getId(), variant3.getId());
  }
}