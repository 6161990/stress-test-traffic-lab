package com.yoon.stress.service

import com.yoon.stress.entity.User
import com.yoon.stress.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    
    fun getAllUsers(): List<User> = userRepository.findAll()
    
    fun getUser(id: Long): User? = userRepository.findById(id).orElse(null)
    
    fun getUserByEmail(email: String): User? = userRepository.findByEmail(email)
    
    fun createUser(user: User): User = userRepository.save(user)
}