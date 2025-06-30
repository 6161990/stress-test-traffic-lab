package com.yoon.stress.service

import com.yoon.stress.entity.*
import com.yoon.stress.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) {
    
    data class OrderItemRequest(
        val productId: Long,
        val quantity: Int
    )
    
    fun getUserOrders(userId: Long): List<Order> = orderRepository.findByUserId(userId)
    
    @Transactional
    fun createOrder(userId: Long, items: List<OrderItemRequest>): Order {
        val user = userRepository.findById(userId).orElseThrow { 
            IllegalArgumentException("사용자를 찾을 수 없습니다: $userId") 
        }
        
        // 재고 검증
        for (item in items) {
            val product = productRepository.findById(item.productId).orElseThrow {
                IllegalArgumentException("상품을 찾을 수 없습니다: ${item.productId}")
            }
            if (product.stock < item.quantity) {
                throw IllegalStateException("재고 부족: ${product.name}")
            }
        }
        
        var totalAmount = BigDecimal.ZERO
        val order = Order(user = user, totalAmount = totalAmount)
        val savedOrder = orderRepository.save(order)
        
        // 재고 차감 및 주문 아이템 생성
        for (item in items) {
            val product = productRepository.findById(item.productId).get()
            
            // 재고 차감 (나중에 Redis + 분산락으로 개선)
            val updatedProduct = product.copy(stock = product.stock - item.quantity)
            productRepository.save(updatedProduct)
            
            totalAmount = totalAmount.add(product.price.multiply(BigDecimal(item.quantity)))
        }
        
        return savedOrder.copy(totalAmount = totalAmount).let { orderRepository.save(it) }
    }
}