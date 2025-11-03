package com.sau.hdfs.repository;

import com.sau.hdfs.model.Emp;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpRepository extends JpaRepository<Emp, Integer> {
    
    @Cacheable(value = "employees", key = "'all'")
    @Override
    List<Emp> findAll();
    
    @Cacheable(value = "employees", key = "#empno")
    @Override
    Optional<Emp> findById(Integer empno);
    
    @CacheEvict(value = "employees", allEntries = true)
    @Override
    <S extends Emp> S save(S entity);
    


    @Query("SELECT e FROM Emp e JOIN FETCH e.department LEFT JOIN FETCH e.manager")
    List<Emp> findAllWithDetails();
}
