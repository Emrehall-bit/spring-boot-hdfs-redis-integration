package com.sau.hdfs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dept")
public class Dept implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "deptno")
    private Integer deptno;
    
    @Column(name = "dname")
    private String dname;
    
    @Column(name = "loc")
    private String loc;
}
