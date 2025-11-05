package com.sau.hdfs.config;

import com.sau.hdfs.model.Emp;
import com.sau.hdfs.repository.EmpRepository;
import com.sau.hdfs.service.HdfsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class HdfsDataInitializer implements CommandLineRunner {

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private HdfsImageService hdfsImageService;

    @Autowired
    private ResourceLoader resourceLoader; // Proje içindeki dosyaları (resources) okumak için

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- HDFS Data Initializer Başlıyor ---");

        List<Emp> employees = empRepository.findAll();

        for (Emp emp : employees) {
            // 1. Sadece veritabanında resim yolu (imgUrl) kayıtlı olmayanları kontrol et
            if (emp.getImgUrl() == null || emp.getImgUrl().isEmpty()) {
                String filename = emp.getEname().toLowerCase() + ".jpg";

                try {
                    // 2. Resmi 'src/main/resources/example-images.images/' klasöründen ara
                    // "classpath:" demek, 'src/main/resources' klasörünün içi demektir
                    Resource resource = resourceLoader.getResource("classpath:example_images/" + filename);

                    if (resource.exists()) {
                        // 3. Resim bulunduysa, HDFS'e yükle
                        System.out.println("-> Bulundu: " + filename + ", HDFS'e yükleniyor...");
                        InputStream inputStream = resource.getInputStream();
                        String hdfsPath = hdfsImageService.saveImageStream(inputStream, emp.getEmpno());

                        if (hdfsPath != null) {
                            // 4. HDFS'e yükleme başarılıysa, veritabanını güncelle
                            emp.setImgUrl(filename);
                            empRepository.save(emp);
                        }
                    } else {
                        System.out.println("-> 'resources/example_images' içinde Bulunamadı: " + filename + " (atlandı)");
                    }
                } catch (Exception e) {
                    System.err.println("Hata (Initializer): " + filename + " - " + e.getMessage());
                }
            }
        }
        System.out.println("--- HDFS Data Initializer Tamamlandı ---");
    }
}

