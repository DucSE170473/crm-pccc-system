package com.thanhcong.crm.repository;

import com.thanhcong.crm.entity.DebtTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DebtTransactionRepository extends JpaRepository<DebtTransaction, Long> {
    // Lấy lịch sử giao dịch của một khách hàng (mới nhất lên đầu)
    List<DebtTransaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}