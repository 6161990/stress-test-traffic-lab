package com.yoon.stress.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(unique = true, nullable = false)
    val email: String = "",
    
    @Column(nullable = false)
    val name: String = "",
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)