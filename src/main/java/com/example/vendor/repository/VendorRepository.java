package com.example.vendor.repository;

import com.example.commanentity.Vendor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends CrudRepository<Vendor, String> {
    @Query("SELECT v FROM Vendor v WHERE v.emailid = :email AND v.status = 1")
    Vendor findByEmailid(String email);

    int countByEmailid(String email);

    @Query("SELECT count(a.id) FROM Vendor a WHERE a.session_token =:token")
    int countBySessionTokenAndStatus(String token);
}
