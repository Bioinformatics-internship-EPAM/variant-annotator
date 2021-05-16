package ru.spbstu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.spbstu.dto.VariantListDto;
import ru.spbstu.exception.FileProcessingException;
import ru.spbstu.service.VariantService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class VcfController {

    private final VariantService variantService;

    @PostMapping(value = "/vcfs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> parseVcfFile(@RequestParam("db_name") final String dbName,
                                               @RequestPart("vcf_file") final MultipartFile file) {
        try {
            variantService.save(file.getInputStream(), dbName);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new FileProcessingException("Incorrect vcf file", e);
        }
    }

    @PostMapping("/getAnnotatedVariants")
    public VariantListDto getAnnotatedVariants(@RequestBody VariantListDto requestedVariantListDto) {
        return variantService.getAnnotatedVariants(requestedVariantListDto);
    }
}
