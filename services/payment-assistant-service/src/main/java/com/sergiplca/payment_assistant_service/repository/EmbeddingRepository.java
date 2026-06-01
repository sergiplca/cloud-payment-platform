package com.sergiplca.payment_assistant_service.repository;

import com.sergiplca.payment_assistant_service.model.entity.Embedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmbeddingRepository extends JpaRepository<Embedding, Long> {
}
