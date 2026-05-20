package com.phimhay.juanng.modules.user.entity;

import com.phimhay.juanng.common.utils.UlidHelper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Column(nullable = false)
    private double amount;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // e.g. "MOMO", "VNPAY", "BANK_TRANSFER"

    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus; // e.g. "PENDING", "SUCCESS", "FAILED"

    @Column(name = "external_transaction_id")
    private String externalTransactionId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UlidHelper.nextUlid();
        }
    }
}
