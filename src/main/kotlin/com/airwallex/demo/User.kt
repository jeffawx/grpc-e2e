package com.airwallex.demo

import java.util.UUID
import javax.validation.constraints.Email
import javax.validation.constraints.Size
import org.springframework.data.annotation.Id

class User(

    @Id
    val id: UUID? = null,

    @Size(min = 2, max = 10, message = "invalid name")
    val name: String,

    @Email
    val email: String? = null
)
