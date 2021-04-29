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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AnnotationRepositoryTest {
    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private AnnotationRepository annotationRepository;

    @Test
    void simpleInsert() {
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
    void violateConstraintsJsonb() {
        Variant variant = variantRepository.save(new Variant()
                .setChromosome("a")
                .setPosition(2L)
                .setReferenceBase("aaa"));

        Annotation annotation = new Annotation().setVariant(variant);
        Assertions.assertThatThrownBy(() -> annotationRepository.save(annotation))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void violateConstraintsVariantIdNotExists() {
        Annotation annotation = new Annotation().setVariant(new Variant()
                .setId(1L)).setInfo(Map.of("info", "2"));

        Assertions.assertThatThrownBy(() -> annotationRepository.save(annotation))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void violateConstraintsVariantIdNotSet() {
        Annotation annotation = new Annotation().setInfo(Map.of("info", "1"));

        Assertions.assertThatThrownBy(() -> annotationRepository.save(annotation))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findAllByVariantId() {
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
    void checkFindByVariantCodes() {
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
