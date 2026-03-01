package com.Group18.hotel_automation.repository;

import com.Group18.hotel_automation.entity.Bill;
import com.Group18.hotel_automation.entity.BillItem;
import com.Group18.hotel_automation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    List<BillItem> findByBillOrderByCreatedAtAsc(Bill bill);

    List<BillItem> findByBillId(Long billId);

    @Query("""
       SELECT COALESCE(SUM(bi.amount),0)
       FROM BillItem bi
       WHERE bi.sourceType = 'SERVICE_REQUEST'
       AND bi.sourceId IN (
            SELECT sr.id FROM ServiceRequest sr
            WHERE sr.assignedStaff = :staff
            AND sr.status = 'COMPLETED'
       )
       """)
    double getServiceRevenueByStaff(User staff);

}
