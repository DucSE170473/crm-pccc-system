package com.thanhcong.crm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "debt_transactions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DebtTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String type; // "DEBIT" (Tăng nợ), "CREDIT" (Giảm nợ)
    private BigDecimal amount;
    private String note;
    private LocalDateTime createdAt = LocalDateTime.now();
}