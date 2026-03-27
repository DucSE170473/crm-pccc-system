package com.thanhcong.crm.entity; // Chắc chắn dòng này phải như thế này

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", unique = true, nullable = false)
    private String customerCode;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;

    @Column(columnDefinition = "TEXT") // Để lưu địa chỉ dài
    private String address;

    @Column(name = "current_debt", precision = 18, scale = 2)
    private BigDecimal currentDebt = BigDecimal.ZERO;
}