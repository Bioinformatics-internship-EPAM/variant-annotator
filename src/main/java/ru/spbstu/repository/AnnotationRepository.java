package ru.spbstu.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.model.Annotation;

import java.util.List;

@Repository
@SuppressWarnings("squid:S100")
public interface AnnotationRepository extends CrudRepository<Annotation, Long> {
    List<Annotation> findAllByVariant_Id(Long id);

    List<Annotation> findAllByVariant_VariantCodeIn(List<String> variantCodes);
}
