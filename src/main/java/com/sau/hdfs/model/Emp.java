package com.sau.hdfs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "emp")
public class Emp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "empno")
    private Integer empno;

    @Column(name = "ename")
    private String ename;

    @Column(name = "job")
    private String job;

    // YÖNETİCİ İLİŞKİSİ (emp tablosuna self-join)
    // 'mgr' sütununu kullanarak Emp nesnesine bağlan
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mgr")
    private Emp manager; // Artık Integer değil, bir Emp nesnesi

    @Column(name = "hiredate")
    @Temporal(TemporalType.DATE)
    private Date hiredate;

    @Column(name = "sal")
    private Integer sal;

    @Column(name = "comm")
    private Integer comm;

    // DEPARTMAN İLİŞKİSİ (dept tablosuna join)
    // 'deptno' sütununu kullanarak Dept nesnesine bağlan
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deptno")
    private Dept department; // Artık Integer değil, bir Dept nesnesi

    @Column(name = "imgUrl")
    private String imgUrl;

    // @Transient alanlara artık GEREK YOK, çünkü:
    // Yönetici adını almak için: emp.getManager().getEname()
    // Departman adını almak için: emp.getDepartment().getDname()
    // kullanacağız.
}
