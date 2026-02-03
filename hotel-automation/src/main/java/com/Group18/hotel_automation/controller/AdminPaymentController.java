package com.Group18.hotel_automation.controller;

import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.BillItem;
import com.Group18.hotel_automation.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {

    private final BillService billService;

    public AdminPaymentController(BillService billService) {
        this.billService = billService;
    }

    // Get all bills
    @GetMapping("/bills")
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    // Get bill items
    @GetMapping("/bills/{id}/items")
    public ResponseEntity<List<BillItem>> getBillItems(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillItems(id));
    }

    // Mark bill as PAID
    @PutMapping("/bills/{id}/pay")
    public ResponseEntity<String> markBillPaid(@PathVariable Long id) {
        billService.markBillAsPaid(id);
        return ResponseEntity.ok("Bill marked as PAID");
    }
}
