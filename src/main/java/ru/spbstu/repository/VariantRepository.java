package ru.spbstu.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.model.Variant;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public interface VariantRepository extends CrudRepository<Variant, Long>, JpaSpecificationExecutor<Variant> {
  default Optional<Variant> findVariant(String chromosome, Long position, String referenceBase, String alternateBase) {
    return findOne((variant, cq, cb) ->
        cb.and(
            cb.equal(variant.get("chromosome"), chromosome),
            cb.equal(variant.get("position"), position),
            getPredicateForNullableValue(referenceBase, "referenceBase", variant, cb),
            getPredicateForNullableValue(alternateBase, "alternateBase", variant, cb)));
  }

  private Predicate getPredicateForNullableValue(String value, String fieldName, Root<Variant> variant, CriteriaBuilder cb) {
    return value == null ? cb.isNull(variant.get(fieldName)) : cb.equal(variant.get(fieldName), value);
  }
}
