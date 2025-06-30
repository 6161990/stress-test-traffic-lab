package com.yoon.stress.controller

import com.yoon.stress.entity.Product
import com.yoon.stress.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    
    @GetMapping
    fun getAllProducts(): List<Product> = productService.getAllProducts()
    
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<Product> {
        return productService.getProduct(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
    
    @GetMapping("/search")
    fun searchProducts(@RequestParam name: String): List<Product> {
        return productService.searchProducts(name)
    }
    
    @GetMapping("/in-stock")
    fun getInStockProducts(): List<Product> = productService.getInStockProducts()
    
    @GetMapping("/price-range")
    fun getProductsByPriceRange(
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal
    ): List<Product> {
        return productService.getProductsByPriceRange(minPrice, maxPrice)
    }
    
    @PostMapping
    fun createProduct(@RequestBody product: Product): Product {
        return productService.createProduct(product)
    }
}