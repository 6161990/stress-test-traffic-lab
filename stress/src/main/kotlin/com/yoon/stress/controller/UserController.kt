package com.yoon.stress.controller

import com.yoon.stress.entity.User
import com.yoon.stress.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    
    @GetMapping
    fun getAllUsers(): List<User> = userService.getAllUsers()
    
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        return userService.getUser(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
    
    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return userService.createUser(user)
    }
}