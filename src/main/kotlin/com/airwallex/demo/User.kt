package com.airwallex.demo

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.Size
import org.springframework.data.annotation.Id

data class User(

    @Id
    val id: UUID? = null,

    @get:Size(min = 2, max = 10, message = "name should contain 2-10 characters")
    val name: String,

    @get:Email
    val email: String? = null
)
