package com.sau.hdfs.controller;

import com.sau.hdfs.service.HdfsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private HdfsImageService hdfsImageService;

    @GetMapping("/{empno}")
    public ResponseEntity<?> getEmployeeImage(@PathVariable int empno) {
        byte[] imageBytes = hdfsImageService.getEmployeeImageBytes(empno);
        
        if (imageBytes == null || imageBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + empno + ".jpg\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }
}
