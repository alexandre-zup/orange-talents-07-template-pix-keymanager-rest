package dev.alexandrevieira.manager.factories

import dev.alexandrevieira.stubs.KeyManagerConsultaServiceGrpc
import dev.alexandrevieira.stubs.KeyManagerListaServiceGrpc
import dev.alexandrevieira.stubs.KeyManagerRegistraServiceGrpc
import dev.alexandrevieira.stubs.KeyManagerRemoveServiceGrpc
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

@MicronautTest
internal class GrpcClientFactoryTest {
    @Inject
    private lateinit var factory: GrpcClientFactory

    @Test
    fun registraClient() {
        val client  = factory.registraClient()
        assertNotNull(client)
        assertTrue(client is KeyManagerRegistraServiceGrpc.KeyManagerRegistraServiceBlockingStub)
    }

    @Test
    fun removeClient() {
        val client  = factory.removeClient()
        assertNotNull(client)
        assertTrue(client is KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub)
    }

    @Test
    fun consultaClient() {
        val client  = factory.consultaClient()
        assertNotNull(client)
        assertTrue(client is KeyManagerConsultaServiceGrpc.KeyManagerConsultaServiceBlockingStub)
    }

    @Test
    fun listaClient() {
        val client  = factory.listaClient()
        assertNotNull(client)
        assertTrue(client is KeyManagerListaServiceGrpc.KeyManagerListaServiceBlockingStub)
    }
}