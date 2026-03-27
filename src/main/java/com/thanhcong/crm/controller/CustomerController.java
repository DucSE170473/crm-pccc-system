package com.thanhcong.crm.controller;


import com.thanhcong.crm.entity.Customer;
import com.thanhcong.crm.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Customer create(@RequestBody Customer c) {
        return service.create(c);
    }

    @PostMapping("/{id}/pay")
    public Customer pay(@PathVariable Long id, @RequestParam BigDecimal amount, @RequestParam String note) {
        return service.processPayment(id, amount, note);
    }
}