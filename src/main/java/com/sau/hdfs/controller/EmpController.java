package com.sau.hdfs.controller;

// Gerekli tüm importlar
import com.sau.hdfs.model.Dept;
import com.sau.hdfs.model.Emp;
import com.sau.hdfs.repository.DeptRepository;
import com.sau.hdfs.repository.EmpRepository;
import com.sau.hdfs.service.HdfsImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import java.util.List;

@Controller
public class EmpController {

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private DeptRepository deptRepository;

    @Autowired
    private HdfsImageService hdfsImageService; // HDFS servisini enjekte et

    @Autowired(required = false) // Hata vermemesi için required=false
    private CacheManager cacheManager; // Cache temizleme butonu için

    /**
     * GÖREV C: Ana sayfa.
     * Veritabanından verimli JOIN sorgusu (findAllWithDetails) ile verileri çeker.
     */
    @GetMapping("/employee/{id}")
    public String viewEmployee(@PathVariable("id") int id, Model model) {
        // FetchType.EAGER sayesinde tüm veriler (yönetici, departman) zaten gelecek
        Emp emp = empRepository.findById(id).orElse(null);

        if (emp != null) {
            // Resim URL'sini ayarla
            emp.setImgUrl("/api/images/" + emp.getEmpno());
            model.addAttribute("emp", emp);
            return "emp"; // emp.html'i göster
        } else {
            // Çalışan bulunamazsa ana sayfaya yönlendir
            return "redirect:/";
        }
    }
    @GetMapping("/")
    public String index(Model model) {
        // Repository'de yazdığımız verimli JOIN sorgusunu kullanıyoruz
        List<Emp> employees = empRepository.findAllWithDetails();

        employees.forEach(emp -> {
            // Sadece resim URL'sini belirliyoruz.
            // Bu URL, ImageController'daki @GetMapping("/api/images/{id}") adresini işaret eder
            emp.setImgUrl("/api/images/" + emp.getEmpno());
        });

        model.addAttribute("employees", employees);
        return "index"; // index.html'e gönder
    }

    /**
     * Çalışan ekleme formunu gösterir (CRUD'un 'C' adımı)
     * Forma departman ve yönetici listelerini gönderir
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("departments", deptRepository.findAll());
        model.addAttribute("managers", empRepository.findAll());
        return "add"; // add.html'i göster
    }

    /**
     * GÖREV A: CRUD - Yeni çalışan oluşturur (CREATE)
     * add.html formundan gelen veriyi işler
     */
    @PostMapping("/add")
    public String addEmployee(
            @RequestParam int empno,
            @RequestParam String ename,
            @RequestParam String job,
            @RequestParam(required = false) Integer mgr, // Yöneticinin ID'si (empno)
            @RequestParam(required = false) Date hiredate,
            @RequestParam(required = false) Integer sal,
            @RequestParam(required = false) Integer comm,
            @RequestParam int deptno) { // Departmanın ID'si (deptno)

        // Gelen ID'leri kullanarak tam nesneleri DB'den çekiyoruz
        Dept department = deptRepository.findById(deptno).orElse(null);
        Emp manager = (mgr != null) ? empRepository.findById(mgr).orElse(null) : null;

        Emp emp = new Emp();
        emp.setEmpno(empno);
        emp.setEname(ename);
        emp.setJob(job);
        emp.setHiredate(hiredate);
        emp.setSal(sal);
        emp.setComm(comm);
        emp.setDepartment(department); // Tam nesneyi set et
        emp.setManager(manager);       // Tam nesneyi set et

        // Kaydet. Bu metot aynı zamanda @CacheEvict sayesinde cache'i temizleyecek.
        empRepository.save(emp);

        return "redirect:/"; // Ana sayfaya yönlendir
    }

    /**
     * GÖREV A: CRUD - Çalışan detay/düzenleme sayfası (READ/UPDATE)
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Emp emp = empRepository.findById(id).orElse(null);

        if (emp != null) {
            // API endpoint'ini set et
            emp.setImgUrl("/api/images/" + emp.getEmpno());
        }

        model.addAttribute("emp", emp);
        model.addAttribute("departments", deptRepository.findAll());
        model.addAttribute("managers", empRepository.findAll());

        return "edit"; // edit.html'i göster
    }

    /**
     * GÖREV A: CRUD - Çalışan Güncelleme (UPDATE)
     * 'edit.html' formundan gelen veriyi işler.
     */
    @PostMapping("/update")
    public String updateEmployee(
            @RequestParam int empno, // Değiştirilemez, ID olarak kullanılır
            @RequestParam String ename,
            @RequestParam String job,
            @RequestParam(required = false) Integer mgr, // Yöneticinin ID'si
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date hiredate,
            @RequestParam(required = false) Integer sal,
            @RequestParam(required = false) Integer comm,
            @RequestParam int deptno) { // Departmanın ID'si

        Emp emp = empRepository.findById(empno).orElseThrow(() ->
                new IllegalArgumentException("Invalid employee EmpNo:" + empno));

        Dept department = deptRepository.findById(deptno).orElse(null);
        Emp manager = (mgr != null) ? empRepository.findById(mgr).orElse(null) : null;

        emp.setEname(ename);
        emp.setJob(job);
        emp.setHiredate(hiredate);
        emp.setSal(sal);
        emp.setComm(comm);
        emp.setDepartment(department);
        emp.setManager(manager);

        empRepository.save(emp);

        return "redirect:/"; // Ana sayfaya yönlendir
    }

    /**
     * GÖREV D: Bir çalışan için resim yüklemeyi yönetir.
     * Bu metot, resmi HDFS'e yükler VE veritabanındaki 'imgUrl' alanını günceller.
     */
    @PostMapping("/uploadImage")
    public String uploadEmployeeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("empno") int empno) {

        if (file.isEmpty()) {
            return "redirect:/edit/" + empno;
        }

        // 1. Dosyayı HDFS'e kaydetmeyi dene
        String hdfsPath = hdfsImageService.saveEmployeeImage(file, empno);

        // 2. SADECE HDFS'e kaydetme başarılı OLDUYSA veritabanını güncelle
        if (hdfsPath != null) {
            Emp emp = empRepository.findById(empno).orElse(null);
            if (emp != null) {
                // Veritabanına HDFS yolunu değil, sadece dosya adını kaydet
                emp.setImgUrl(empno + ".jpg");
                empRepository.save(emp);
            }
        } else {
            // HDFS'e kaydetme başarısız oldu (terminalde hata mesajı olmalı)
            // (Bir hata mesajı da gösterebilirsin ama şimdilik gerek yok)
        }

        return "redirect:/edit/" + empno;
    }

    /**
     * GÖREV A: CRUD - Çalışan Silme (DELETE)
     */
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") int id) {
        empRepository.deleteById(id);
        // Not: Verimli olması için deleteById'ı da @CacheEvict yapabilirsiniz
        return "redirect:/";
    }

    /**
     * GÖREV A: Redis önbelleğini manuel temizleme
     */
    @GetMapping("/clear-cache")
    public String clearCache() {
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(cacheName ->
                    cacheManager.getCache(cacheName).clear()
            );
        }
        return "redirect:/";
    }
}