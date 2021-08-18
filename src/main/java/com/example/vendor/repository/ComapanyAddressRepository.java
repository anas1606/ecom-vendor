package com.example.vendor.repository;

import com.example.commanentity.Company_Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComapanyAddressRepository extends JpaRepository<Company_Address, String> {
}
