package com.sergiplca.payment_assistant_service.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "embedding",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_embeddings_record",
        columnNames = {"record_type", "record_id"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Embedding {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "embedding_seq")
    @SequenceGenerator(
        name = "embedding_seq",
        sequenceName = "paymentassistantservice.embedding_sequence",
        allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, length = 50)
    private String recordType;

    @Column(nullable = false, length = 100)
    private String recordId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "text")
    private String contentText;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(nullable = false, columnDefinition = "vector(768)")
    private float[] embedding;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
