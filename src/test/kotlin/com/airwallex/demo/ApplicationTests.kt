package com.airwallex.demo

import com.airwallex.grpc.annotations.GrpcClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.get
import demo.UserServiceRpc
import io.grpc.Status.Code.INVALID_ARGUMENT
import io.grpc.Status.Code.NOT_FOUND
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    "grpc.server.name=test",
    "grpc.server.port=-1", // -1 for in-process server
    "grpc.client.channels.userClient.in-process=true",
    "grpc.client.channels.userClient.server-name=test"
)
class ApplicationTests {

    @Autowired
    @GrpcClient
    private lateinit var userClient: UserServiceRpc

    @Test
    fun `create user and load`() {
        val user = User(name = "Jeff")
        val userId = userClient.create(user).get()

        assertNotNull(userId)
        assertEquals(user.name, userClient.get(userId).get()!!.name)
    }

    @Test
    fun `create user validation failed`() {
        val user = User(name = "J")

        val result = userClient.create(user)
        assertTrue(result is Err)

        val error = result.error
        assertEquals(INVALID_ARGUMENT, error.statusCode)
        assertEquals(
            mapOf("x-validate-name" to "name should contain 2-10 characters"),
            error.details
        ) // bean validation errors
    }

    @Test
    fun `create user custom error`() {
        val user = User(name = "admin")

        val result = userClient.create(user)
        assertTrue(result is Err)

        val error = result.error
        assertEquals(INVALID_ARGUMENT, error.statusCode)
        assertEquals("ADMIN_NOT_ALLOWED", error.code) // custom error code propagated to client
        assertEquals("cannot create admin", error.description)
        assertEquals("admin", error.details["invalid_name"]) // custom error details propagated to client
    }

    @Test
    fun `user not found`() {
        val result = userClient.get(UUID.randomUUID())
        assertTrue(result is Err)
        assertEquals(NOT_FOUND, result.error.statusCode)
    }
}
