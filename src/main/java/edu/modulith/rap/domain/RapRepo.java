package edu.modulith.rap.domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RapRepo extends JpaRepository<Rap, Long> {
    @Query(value = "SELECT * FROM rap ORDER BY created_at DESC", nativeQuery = true)
    List<Rap> findAllOrderByCreatedAtDesc();
}
