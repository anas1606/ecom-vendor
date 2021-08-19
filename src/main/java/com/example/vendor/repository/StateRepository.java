package com.example.vendor.repository;


import com.example.commanentity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StateRepository extends JpaRepository<State, String> {
    State findByName(String name);
}
