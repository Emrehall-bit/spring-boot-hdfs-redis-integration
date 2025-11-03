package com.sau.hdfs.service;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.io.IOUtils;
@Service
public class HdfsImageService {

    @Autowired
    private FileSystem fileSystem;

    private static final String HDFS_IMAGE_PATH = "/user/emrehalli/emp_images/";
    public String saveEmployeeImage(MultipartFile file, int empno) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // Dosya adını empno'ya göre belirliyoruz (örn: 7369.jpg)
            // Orjinal dosya adının uzantısını alabiliriz, ama basitlik için .jpg varsayalım
            String filename = empno + ".jpg";
            Path hdfsPath = new Path(HDFS_IMAGE_PATH + filename);

            // HDFS'te bu yola bir dosya yazma akışı (OutputStream) aç
            // 'true' parametresi, dosya varsa üzerine yazılmasını sağlar
            OutputStream outputStream = fileSystem.create(hdfsPath, true);

            // Yüklenen dosyanın içeriğini (InputStream) HDFS'teki dosyaya (OutputStream) kopyala
            IOUtils.copyBytes(file.getInputStream(), outputStream, 4096, true);

            System.out.println("✅ Image uploaded to HDFS: " + hdfsPath);
            return hdfsPath.toString(); // Başarı durumunda HDFS yolunu döndür

        } catch (IOException e) {
            System.err.println("❌ Error uploading image for empno " + empno + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getEmployeeImageBytes(int empno) {
        try {
            Path imagePath = new Path(HDFS_IMAGE_PATH + empno + ".jpg");
            
            if (!fileSystem.exists(imagePath)) {
                System.out.println("⚠️ Image not found in HDFS: " + imagePath);
                return null;
            }

            InputStream inputStream = fileSystem.open(imagePath);
            byte[] imageBytes = inputStream.readAllBytes();
            inputStream.close();

            System.out.println("✅ Image loaded from HDFS: " + empno);
            return imageBytes;
        } catch (IOException e) {
            System.err.println("❌ Error reading image for empno " + empno + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
