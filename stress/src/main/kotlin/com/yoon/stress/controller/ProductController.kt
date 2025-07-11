package com.yoon.stress.controller

import com.yoon.stress.entity.Product
import com.yoon.stress.service.ProductService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    private val logger = LoggerFactory.getLogger(ProductController::class.java)
    
    @GetMapping
    fun getAllProducts(): List<Product> {
        logger.info("GET /api/products - 전체 상품 조회 요청")
        val products = productService.getAllProducts()
        logger.info("GET /api/products - 응답: ${products.size}개 상품")
        return products
    }
    
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<Product> {
        logger.info("GET /api/products/$id - 상품 조회 요청")
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
        logger.info("POST /api/products - 상품 생성 요청: ${product.name}")
        val createdProduct = productService.createProduct(product)
        logger.info("POST /api/products - 상품 생성 완료: ID=${createdProduct.id}")
        return createdProduct
    }
}
