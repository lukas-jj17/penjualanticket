package com.pbo2.penjualanticket.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pbo2.penjualanticket.model.Penjualan;

public interface PenjualanRepository extends JpaRepository<Penjualan, Integer> {

	List<Penjualan> findByCustomerIdCustomer(Integer idCustomer);

	Optional<Penjualan> findByReferenceId(String referenceId);
}

