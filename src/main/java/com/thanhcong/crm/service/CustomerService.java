package com.thanhcong.crm.service;

import com.thanhcong.crm.entity.Customer;
import com.thanhcong.crm.entity.DebtTransaction;
import com.thanhcong.crm.repository.CustomerRepository;
import com.thanhcong.crm.repository.DebtTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository repo;
    private final DebtTransactionRepository debtRepo;

    // Inject cả 2 repository để xử lý nghiệp vụ liên quan đến công nợ
    public CustomerService(CustomerRepository repo, DebtTransactionRepository debtRepo) {
        this.repo = repo;
        this.debtRepo = debtRepo;
    }

    // 1. Lấy danh sách toàn bộ khách hàng
    public List<Customer> getAll() {
        return repo.findAll();
    }

    // 2. Tìm khách hàng theo ID
    public Optional<Customer> getById(Long id) {
        return repo.findById(id);
    }

    // 3. Tạo mới khách hàng
    @Transactional
    public Customer create(Customer c) {
        if (c.getCurrentDebt() == null) {
            c.setCurrentDebt(BigDecimal.ZERO);
        }
        return repo.save(c);
    }

    // 4. Cập nhật thông tin khách hàng
    @Transactional
    public Customer update(Long id, Customer details) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + id));

        customer.setName(details.getName());
        customer.setPhone(details.getPhone());
        customer.setEmail(details.getEmail());
        customer.setAddress(details.getAddress());

        return repo.save(customer);
    }

    /**
     * 5. Logic quan trọng: Xử lý thanh toán và giảm nợ
     * @Transactional đảm bảo nếu lưu log lỗi thì tiền nợ của khách sẽ không bị trừ (ACID)
     */
    @Transactional
    public Customer processPayment(Long customerId, BigDecimal paymentAmount, String note) {
        // Kiểm tra khách hàng có tồn tại không
        Customer customer = repo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng để thanh toán"));

        // Tính toán dư nợ mới: Nợ cũ - Số tiền trả
        BigDecimal newDebt = customer.getCurrentDebt().subtract(paymentAmount);
        customer.setCurrentDebt(newDebt);

        // Lưu cập nhật khách hàng
        Customer updatedCustomer = repo.save(customer);

        // Ghi lại nhật ký giao dịch công nợ (CREDIT - Giảm nợ)
        DebtTransaction log = new DebtTransaction();
        log.setCustomer(updatedCustomer);
        log.setType("CREDIT");
        log.setAmount(paymentAmount);
        log.setNote(note != null ? note : "Khách hàng thanh toán tiền thiết bị/dịch vụ PCCC");
        log.setCreatedAt(LocalDateTime.now());

        debtRepo.save(log);

        return updatedCustomer;
    }

    // 6. Xóa khách hàng (Chỉ nên xóa khi nợ = 0)
    @Transactional
    public void delete(Long id) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng để xóa"));

        if (customer.getCurrentDebt().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Không thể xóa khách hàng vẫn còn nợ!");
        }

        repo.deleteById(id);
    }
}