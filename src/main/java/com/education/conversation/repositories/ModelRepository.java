package com.education.conversation.repositories;

import com.education.conversation.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    @Query(value = "SELECT * FROM models m WHERE m.model = ?1", nativeQuery = true)
    Optional<Model> findByModel(String model);

}
