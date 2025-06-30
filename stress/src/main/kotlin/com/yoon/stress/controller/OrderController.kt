package com.yoon.stress.controller

import com.yoon.stress.entity.Order
import com.yoon.stress.service.OrderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    
    data class OrderRequest(
        val userId: Long,
        val items: List<OrderService.OrderItemRequest>
    )
    
    @GetMapping("/user/{userId}")
    fun getUserOrders(@PathVariable userId: Long): List<Order> {
        return orderService.getUserOrders(userId)
    }
    
    @PostMapping
    fun createOrder(@RequestBody request: OrderRequest): Order {
        return orderService.createOrder(request.userId, request.items)
    }
}