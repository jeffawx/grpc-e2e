package com.airwallex.demo

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun defaultExecutor(): Executor {
        return Executors.newFixedThreadPool(200)
    }
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
