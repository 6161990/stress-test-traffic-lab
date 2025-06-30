package com.yoon.stress.service

import com.yoon.stress.entity.Product
import com.yoon.stress.repository.ProductRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun getAllProducts(): List<Product> = productRepository.findAll()
    
    fun getProduct(id: Long): Product? = productRepository.findById(id).orElse(null)
    
    fun searchProducts(name: String): List<Product> = productRepository.findByNameContaining(name)
    
    fun getInStockProducts(): List<Product> = productRepository.findInStockProducts()
    
    fun getProductsByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product> {
        return productRepository.findByPriceRange(minPrice, maxPrice)
    }
    
    fun createProduct(product: Product): Product = productRepository.save(product)
    
    fun updateStock(productId: Long, quantity: Int): Boolean {
        val product = productRepository.findById(productId).orElse(null) ?: return false
        
        if (product.stock < quantity) {
            return false
        }
        
        val updatedProduct = product.copy(stock = product.stock - quantity)
        productRepository.save(updatedProduct)
        return true
    }
}