package com.thanhcong.crm.service;

import com.thanhcong.crm.entity.*;
import com.thanhcong.crm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class ContractService {
    private final ContractRepository contractRepo;
    private final CustomerRepository customerRepo;
    private final DebtTransactionRepository debtRepo;

    public ContractService(ContractRepository contractRepo, CustomerRepository customerRepo, DebtTransactionRepository debtRepo) {
        this.contractRepo = contractRepo;
        this.customerRepo = customerRepo;
        this.debtRepo = debtRepo;
    }

    @Transactional
    public Contract createContract(Contract contract) {
        // 1. Lưu hợp đồng
        Contract savedContract = contractRepo.save(contract);

        // 2. Cập nhật tổng nợ khách hàng
        Customer customer = contract.getCustomer();
        customer.setCurrentDebt(customer.getCurrentDebt().add(contract.getTotalAmount()));
        customerRepo.save(customer);

        // 3. Ghi log giao dịch nợ
        DebtTransaction log = DebtTransaction.builder()
                .customer(customer)
                .type("DEBIT")
                .amount(contract.getTotalAmount())
                .note("Phát sinh nợ từ hợp đồng: " + contract.getContractNumber())
                .build();
        debtRepo.save(log);

        return savedContract;
    }
}