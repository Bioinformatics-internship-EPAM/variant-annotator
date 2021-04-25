package ru.spbstu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.spbstu.dto.AnnotatedVariantList;
import ru.spbstu.dto.VariantList;
import ru.spbstu.model.Variant;
import ru.spbstu.service.VariantService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class VcfController {

    private final VariantService variantService;

    @PostMapping("/vcfs")
    public ResponseEntity<Object> parseVcfFile(@RequestParam("vcf_file") final MultipartFile file,
                                               @RequestParam("db_name") final String dbName) {
        try {
            variantService.save(file.getInputStream(), dbName);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Incorrect vcf file");
        }
    }

    @PostMapping("/getAnnotatedVariants")
    @ResponseBody
    public AnnotatedVariantList getAnnotatedVariants(@RequestBody VariantList requestedVariantList) {
        List<Variant> requestedVariants = requestedVariantList.getVariants();
        List<Variant> annotatedVariants = new ArrayList<>();

        for (Variant var: requestedVariants) {
            Optional<Variant> variant = variantService.find(var.getChromosome(), var.getPosition(),
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
