package ru.spbstu.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.spbstu.model.Annotation;
import ru.spbstu.model.Variant;

import java.util.Map;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VariantRepositoryTest {
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private AnnotationRepository annotationRepository;

    @Test
    void simpleInsert() {
        Variant variant = variantRepository.save(new Variant()
                .setChromosome("a")
                .setPosition(2L)
                .setReferenceBase("aaa")
                .setAlternateBase("aaab"));

        Assertions.assertThat(variantRepository.findById(variant.getId()))
                .isPresent();
    }

    @Test
    void violateUniqueConstraint() {
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

        Variant variant = new Variant()
                .setChromosome("a")
                .setPosition(3L)
                .setReferenceBase("aaa")
                .setAlternateBase("aaab");
        Assertions.assertThatThrownBy(() -> variantRepository.save(variant))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByCompositeKey() {
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

        Assertions.assertThat(variantRepository.findVariant("a", 2L, "bbb", "aabb"))
                .isEmpty();
        Assertions.assertThat(variantRepository.findVariant("a", 2L, "aaa", "aaab"))
                .contains(variant1);
        Assertions.assertThat(variantRepository.findVariant("a", 2L, "aaa", null))
                .isEmpty();
        Assertions.assertThat(variantRepository.findVariant("a", 4L, null, null))
                .contains(variant4);
    }

    @Test
    void getAnnotationsRelatedToVariant() {
        variantRepository.save(new Variant().setChromosome("a").setPosition(2L)
                .addAnnotation(new Annotation().setInfo(Map.of("info", "1")).setDbName("1"))
                .addAnnotation(new Annotation().setInfo(Map.of("info", "1")).setDbName("1"))
                .addAnnotation(new Annotation().setInfo(Map.of("info", "2")).setDbName("2")));

        variantRepository.save(new Variant().setChromosome("a").setPosition(3L)
                .addAnnotation(new Annotation().setInfo(Map.of("info", "1")).setDbName("3")));

        Iterable<Annotation> annotations = annotationRepository.findAll();
        Assertions.assertThat(annotations).hasSize(3);
        annotations.forEach(a -> Assertions.assertThat(a.getVariant()).isNotNull());
    }

    @Test
    void checkVariantCode() {
        Variant variant = variantRepository.save(new Variant()
                .setChromosome("a").setPosition(2L).setReferenceBase("t"));
        Assertions.assertThat(variant.getVariantCode()).isEqualTo("a:2:t>");
    }

    @Test
    void violateVariantCodeUniqueConstraint() {
        variantRepository.save(new Variant()
                .setChromosome("a").setPosition(2L).setReferenceBase("t").setAlternateBase(""));

        Variant variant = new Variant()
                .setChromosome("a").setPosition(2L).setReferenceBase("t").setAlternateBase("");
        Assertions.assertThatThrownBy(() -> variantRepository.save(variant))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
