package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    List<BillItem> findByBillOrderByCreatedAtAsc(Bill bill);

    List<BillItem> findByBillId(Long billId);

}
