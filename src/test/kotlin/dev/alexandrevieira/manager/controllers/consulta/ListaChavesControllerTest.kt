package dev.alexandrevieira.manager.controllers.consulta

import com.google.protobuf.Timestamp
import dev.alexandrevieira.manager.factories.GrpcClientFactory
import dev.alexandrevieira.stubs.*
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Instant
import java.util.*

@MicronautTest
internal class ListaChavesControllerTest {
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: KeyManagerListaServiceGrpc.KeyManagerListaServiceBlockingStub

    @Test
    @DisplayName("Deve listar chaves de um cliente")
    internal fun deveListarChavesDeUmCliente() {
        val clienteId = UUID.randomUUID().toString()
        val grpcRequest = grpcRequest(clienteId = clienteId)
        val grpcResponse = grpcResponse(clienteId = clienteId, empty = false)

        Mockito.`when`(grpcClient.lista(grpcRequest)).thenReturn(grpcResponse)

        val httpRequest: HttpRequest<Void> = HttpRequest.GET("/api/v1/clientes/$clienteId/pix")
        val response = httpClient.toBlocking().exchange(httpRequest, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(2, response.body()!!.size)
    }

    @Test
    @DisplayName("Deve retornar lista vazia")
    internal fun deveRetornarListaVazia() {
        val clienteId = UUID.randomUUID().toString()
        val grpcRequest = grpcRequest(clienteId = clienteId)
        val grpcResponse = grpcResponse(clienteId = clienteId, empty = true)

        Mockito.`when`(grpcClient.lista(grpcRequest)).thenReturn(grpcResponse)

        val httpRequest: HttpRequest<Void> = HttpRequest.GET("/api/v1/clientes/$clienteId/pix")
        val response = httpClient.toBlocking().exchange(httpRequest, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(0, response.body()!!.size)
    }


    @Test
    @DisplayName("Deve falhar ao buscar com clienteId invalido")
    internal fun deveFalharAoBuscarClienteInvalido() {
        val clienteId = "abc123"

        val httpRequest: HttpRequest<Void> = HttpRequest.GET("/api/v1/clientes/$clienteId/pix")
        val error = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, List::class.java)
        }

        assertNotNull(error)
        assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }


    private fun grpcRequest(
        clienteId: String = UUID.randomUUID().toString()
    ): ListaChaveRequest {
        return ListaChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .build()
    }

    private fun grpcResponse(
        clienteId: String = UUID.randomUUID().toString(),
        empty: Boolean
    ): ListaChaveResponse {
        val instant = Instant.now()
        val criadaEm = Timestamp.newBuilder().setSeconds(instant.epochSecond).setNanos(instant.nano).build()

        val chave1 = ListaChaveResponse.ChaveInfo.newBuilder()
            .setChavePixId(UUID.randomUUID().toString())
            .setClienteId(clienteId)
            .setTipo(TipoDaChave.EMAIL)
            .setTipoConta(TipoDaConta.CONTA_CORRENTE)
            .setCriadaEm(criadaEm)
            .setChave("meu@email.com")
            .build()

        val chave2 = ListaChaveResponse.ChaveInfo.newBuilder()
            .setChavePixId(UUID.randomUUID().toString())
            .setClienteId(clienteId)
            .setTipo(TipoDaChave.ALEATORIA)
            .setTipoConta(TipoDaConta.CONTA_POUPANCA)
            .setCriadaEm(criadaEm)
            .setChave(UUID.randomUUID().toString())
            .build()

        return ListaChaveResponse.newBuilder()
            .addAllChaves(if (empty) mutableListOf() else mutableListOf(chave1, chave2))
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerListaServiceGrpc.KeyManagerListaServiceBlockingStub::class.java)
    }
}