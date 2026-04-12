package com.example.repository;

import com.example.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE " +
            "LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :value, '%')) OR " +
            "LOWER(i.systemInvoiceNumber) LIKE LOWER(CONCAT('%', :value, '%')) OR " +
            "LOWER(i.companyName) LIKE LOWER(CONCAT('%', :value, '%')) OR " +
            "LOWER(i.seller) LIKE LOWER(CONCAT('%', :value, '%')) OR " +
            "LOWER(i.invoiceDate) LIKE LOWER(CONCAT('%', :value, '%'))")
    List<Invoice> searchInvoices(@Param("value") String value);
}