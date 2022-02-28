package com.airwallex.demo

import com.airwallex.grpc.annotations.GrpcClient
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.get
import demo.UserServiceRpc
import io.grpc.Status
import java.util.UUID
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
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
    fun `create user and load`() = runBlocking {
        val user = User(name = "Jeff")
        val userId = userClient.create(user).get()

        assertNotNull(userId)
        assertEquals(user.name, userClient.get(userId).get()!!.name)
    }

    @Test
    fun `create user validation failed`() = runBlocking {
        val user = User(name = "J")

        val result = userClient.create(user)
        assertTrue(result is Err)

        val error = result.error
        assertEquals(Status.Code.INVALID_ARGUMENT, error.statusCode)
        assertContains(error.details, "x-validate-name")
    }

    @Test
    fun `create user custom error`() = runBlocking {
        val user = User(name = "admin")

        val result = userClient.create(user)
        assertTrue(result is Err)

        val error = result.error
        assertEquals(Status.Code.INVALID_ARGUMENT, error.statusCode)
        assertEquals("INVALID_ARGUMENT: cannot create admin", error.description)
        assertEquals(mapOf("invalid_name" to "admin"), error.details)
    }

    @Test
    fun `user not found`() = runBlocking {
        val result = userClient.get(UUID.randomUUID())
        assertTrue(result is Err)
        assertEquals(Status.Code.NOT_FOUND, result.error.statusCode)
    }
}
