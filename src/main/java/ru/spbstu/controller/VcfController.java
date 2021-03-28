package ru.spbstu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.spbstu.reader.Reader;
import ru.spbstu.reader.dto.VcfRecord;
import ru.spbstu.service.VariantService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class VcfController {

    private final Reader<VcfRecord> vcfReader;
    private final VariantService variantService;

    @PostMapping("/vcfs")
    public ResponseEntity<Object> parseVcfFile(@RequestParam("vcf_file") final MultipartFile file,
                                               @RequestParam("db_name") final String dbName) {
        try {
            variantService.upsert(vcfReader.read(file.getInputStream()), dbName);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
