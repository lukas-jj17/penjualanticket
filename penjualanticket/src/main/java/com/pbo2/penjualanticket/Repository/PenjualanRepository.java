package com.pbo2.penjualanticket.Repository;

import java.util.List;
<<<<<<< HEAD
import java.util.Optional;
=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822

import org.springframework.data.jpa.repository.JpaRepository;
import com.pbo2.penjualanticket.model.Penjualan;

public interface PenjualanRepository extends JpaRepository<Penjualan, Integer> {

	List<Penjualan> findByCustomerIdCustomer(Integer idCustomer);

<<<<<<< HEAD
	Optional<Penjualan> findByReferenceId(String referenceId);

=======
>>>>>>> 161c0cbacf056bd082d7edcb309739a73a669822
}

