package com.Group18.hotel_automation.service;

import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.BillItem;
import com.Group18.hotel_automation.enums.BillStatus;
import com.Group18.hotel_automation.repository.BillItemRepository;
import com.Group18.hotel_automation.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    public BillService(BillRepository billRepository,
                       BillItemRepository billItemRepository) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public List<BillItem> getBillItems(Long billId) {
        return billItemRepository.findByBillId(billId);
    }

    public void markBillAsPaid(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setStatus(BillStatus.PAID);
        billRepository.save(bill);
    }
}
