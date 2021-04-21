package ru.spbstu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.dto.AnnotatedVariantList;
import ru.spbstu.dto.VariantList;
import ru.spbstu.model.Variant;
import ru.spbstu.repository.VariantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ListAnnotationsController {

    private final static Logger logger = LoggerFactory.getLogger(ListAnnotationsController.class);

    @Autowired
    VariantRepository repository;

    @PostMapping("/getAnnotatedVariants")
    @ResponseBody
    public AnnotatedVariantList getAnnotatedVariants(@RequestBody VariantList requestedVariantList) {
        List<Variant> requestedVariants = requestedVariantList.getVariants();
        List<Variant> annotatedVariants = new ArrayList<>();

        logger.info("Fetching annotations of requested variants");
        for (Variant var: requestedVariants) {
            Optional<Variant> variant = repository.findVariant(var.getChromosome(), var.getPosition(),
                    var.getReferenceBase(), var.getAlternateBase());
            if (variant.isPresent() && variant.get().getAnnotations() != null) {
                annotatedVariants.add(variant.get());
            }
        }
        AnnotatedVariantList annotatedVariantList = new AnnotatedVariantList();
        annotatedVariantList.setVariants(annotatedVariants);
        return annotatedVariantList;
    }
}
