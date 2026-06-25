package com.pbo2.penjualanticket.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pbo2.penjualanticket.model.DetailPenjualan;

public interface DetailPenjualanRepository
extends JpaRepository<DetailPenjualan,Integer>{

    List<DetailPenjualan> findByPenjualanIdPenjualan(Integer idPenjualan);

}
