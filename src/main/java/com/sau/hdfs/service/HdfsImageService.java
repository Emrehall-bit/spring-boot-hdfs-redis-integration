package com.sau.hdfs.service;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class HdfsImageService {

    @Autowired
    private FileSystem fileSystem; // HdfsConfig'den gelir

    // Bu yolu DÜZELTTİK (senin WSL kullanıcı adınla)
    private static final String HDFS_IMAGE_PATH = "/user/emrehalli/emp_images/";

    /**
     * GÖREV D: Resmi HDFS'e yükler (Kullanıcının web'den yüklemesi için)
     */
    public String saveEmployeeImage(MultipartFile file, int empno) {
        if (file.isEmpty()) {
            return null;
        }
        try {
            // Bu metodu, InputStream kullanan ortak metoda yönlendiriyoruz
            return saveImageStream(file.getInputStream(), empno);
        } catch (IOException e) {
            System.err.println("❌ Error reading MultipartFile stream for empno " + empno + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * YENİ METOT: InputStream (dosya akışı) alarak HDFS'e yazar.
     * Hem /uploadImage (manuel) hem de HdfsDataInitializer (otomatik) bunu kullanacak.
     */
    public String saveImageStream(InputStream inputStream, int empno) {
        try {
            String filename = empno + ".jpg";
            Path hdfsPath = new Path(HDFS_IMAGE_PATH + filename);

            OutputStream outputStream = fileSystem.create(hdfsPath, true); // Üzerine yaz

            IOUtils.copyBytes(inputStream, outputStream, 4096, true);

            System.out.println("✅ Image " + filename + " uploaded to HDFS: " + hdfsPath);
            return hdfsPath.toString();

        } catch (IOException e) {
            System.err.println("❌ Error uploading image stream for empno " + empno + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * GÖREV B: Resmi HDFS'ten okur
     */
    public byte[] getEmployeeImageBytes(int empno) {
        try {
            Path imagePath = new Path(HDFS_IMAGE_PATH + empno + ".jpg");

            if (!fileSystem.exists(imagePath)) {
                System.out.println("⚠️ Image not found in HDFS: " + imagePath);
                return null; // Resim yoksa null döndür
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
