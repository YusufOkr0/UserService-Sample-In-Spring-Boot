package com.user_service.repository;

import com.user_service.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<Employee, Long> {

    boolean existsByUsername(String userName);
    boolean existsByEmail(String email);

    @Modifying
    @Query("""
           UPDATE Employee emp SET
           emp.name = COALESCE(:name, emp.name) ,
           emp.surname = COALESCE(:surname, emp.surname),
           emp.username = COALESCE(:username, emp.username),
           emp.password = COALESCE(:password, emp.password),
           emp.email = COALESCE(:email, emp.email),
           emp.updatedAt = COALESCE(:updatedAt, emp.updatedAt)
           WHERE emp.id = :id
           """)
    void updateEmployeeById(Long id, String name, String surname, String username, String password, String email, LocalDateTime updatedAt);


    Page<Employee> findAllByisDeleted(Boolean isDeleted, Pageable pageable);


}
