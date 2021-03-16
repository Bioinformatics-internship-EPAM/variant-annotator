package ru.spbstu.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.spbstu.dto.VariantSearchRequest;
import ru.spbstu.model.Variant;
import ru.spbstu.repository.AnnotationRepository;
import ru.spbstu.repository.VariantRepository;

@DataJpaTest
public class VariantRepositoryTest {
  @Autowired
  private VariantRepository variantRepository;
  @Autowired
  private AnnotationRepository annotationRepository;

  @Test
  public void simpleInsert() {
    Variant variant = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("aaa")
        .setAlternateBase("aaab"));

    Assertions.assertThat(variantRepository.findById(variant.getId()))
        .isPresent();
  }

  @Test
  public void violateUniqueConstraint() {
    variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("aaa")
        .setAlternateBase("aaab"));
    variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(3L)
        .setReferenceBase("aaa")
        .setAlternateBase("aaab"));
    Assertions.assertThatThrownBy(() -> variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(3L)
        .setReferenceBase("aaa")
        .setAlternateBase("aaab")))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  public void findByCompositeKey() {
    Variant variant1 = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("aaa")
        .setAlternateBase("aaab"));
    variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("bbb")
        .setAlternateBase("aaab"));
    variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(2L)
        .setReferenceBase("bbb"));
    Variant variant4 = variantRepository.save(new Variant()
        .setChromosome("a")
        .setPosition(4L));

    Assertions.assertThat(variantRepository.findVariant(new VariantSearchRequest("a", 2L, "bbb", "aabb")))
        .isEmpty();
    Assertions.assertThat(variantRepository.findVariant(new VariantSearchRequest("a", 2L, "aaa", "aaab")))
        .contains(variant1);
    Assertions.assertThat(variantRepository.findVariant(new VariantSearchRequest("a", 2L, "aaa", null)))
        .isEmpty();
    Assertions.assertThat(variantRepository.findVariant(new VariantSearchRequest("a", 4L, null, null)))
        .contains(variant4);
  }
}
