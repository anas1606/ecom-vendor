package com.example.vendor.repository;

import com.example.commanentity.Product;
import com.example.vendor.model.HomeFeedDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    int countByNameAndCategory_NameAndVendor_Id(String name, String category, String id);

    @Query("SELECT new com.example.vendor.model.HomeFeedDTO(p) FROM Product p WHERE p.name like :search AND p.vendor.id = :id AND p.status = 1")
    Page<HomeFeedDTO> findAllPagable(String search , String id, Pageable page);

    @Query("SELECT new com.example.vendor.model.HomeFeedDTO(p) FROM Product p WHERE p.name like :search AND p.vendor.id = :id AND p.status = 1 AND p.category.name = :category")
    Page<HomeFeedDTO> findAllByCategoryPagable(String search , String id, String category, Pageable page);
}
