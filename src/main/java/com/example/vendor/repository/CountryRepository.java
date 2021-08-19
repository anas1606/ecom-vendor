package com.example.vendor.repository;


import com.example.commanentity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    Country findByName(String name);
}
