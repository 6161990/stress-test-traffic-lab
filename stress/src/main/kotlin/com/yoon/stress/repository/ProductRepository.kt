package com.yoon.stress.repository

import com.yoon.stress.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {
    
    fun findByNameContaining(name: String): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    fun findInStockProducts(): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    fun findByPriceRange(@Param("minPrice") minPrice: java.math.BigDecimal, 
                         @Param("maxPrice") maxPrice: java.math.BigDecimal): List<Product>
}