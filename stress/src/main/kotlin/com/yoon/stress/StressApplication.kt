package com.yoon.stress

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StressApplication

fun main(args: Array<String>) {
  runApplication<StressApplication>(*args)
}
