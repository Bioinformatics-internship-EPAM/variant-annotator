package ru.spbstu.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.dto.VariantSearchRequest;
import ru.spbstu.model.Variant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public interface VariantRepository extends CrudRepository<Variant, Long>, JpaSpecificationExecutor<Variant> {
  default Optional<Variant> findVariant(VariantSearchRequest searchRequest) {
    return findOne(getSearchSpecification(searchRequest));
  }

  private Specification<Variant> getSearchSpecification(VariantSearchRequest searchRequest) {
    return (variant, cq, cb) ->
        cb.and(
            cb.equal(variant.get("chromosome"), searchRequest.getChromosome()),
            cb.equal(variant.get("position"), searchRequest.getPosition()),
            getPredicateForNullableValue(searchRequest.getReferenceBase(), "referenceBase", variant, cb),
            getPredicateForNullableValue(searchRequest.getAlternateBase(), "alternateBase", variant, cb));
  }

  private Predicate getPredicateForNullableValue(String value, String fieldName, Root<Variant> variant, CriteriaBuilder cb) {
    return value == null ? cb.isNull(variant.get(fieldName)) : cb.equal(variant.get(fieldName), value);
  }
}
