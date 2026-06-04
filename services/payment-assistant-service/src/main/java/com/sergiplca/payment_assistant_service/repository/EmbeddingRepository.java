package com.sergiplca.payment_assistant_service.repository;

import com.sergiplca.payment_assistant_service.model.entity.Embedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmbeddingRepository extends JpaRepository<Embedding, Long> {

    @Query(value = """
        SELECT id, record_type, record_id, content_text, embedding, created_at
        FROM embedding
        ORDER BY embedding <=> CAST(:embedding AS vector)
        LIMIT :limit
    """, nativeQuery = true)
    List<Embedding> findSimilar(@Param("embedding") String embedding, @Param("limit") int limit);
}
