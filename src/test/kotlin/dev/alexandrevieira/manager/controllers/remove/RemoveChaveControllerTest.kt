package dev.alexandrevieira.manager.controllers.remove

import dev.alexandrevieira.manager.factories.GrpcClientFactory
import dev.alexandrevieira.stubs.KeyManagerRemoveServiceGrpc
import dev.alexandrevieira.stubs.RemoveChaveRequest
import dev.alexandrevieira.stubs.RemoveChaveResponse
import io.grpc.Status
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*

@MicronautTest
internal class RemoveChaveControllerTest {
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub

    @Test
    @DisplayName("Deve remover uma chave existente")
    internal fun deveRemoverUmaChaveExistente() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val grpcRequest = grpcRequest(clienteId = clienteId, chavePixId = pixId)
        val grpcResponse = grpcResponse(clienteId = clienteId, chavePixId = pixId)

        Mockito.`when`(grpcClient.remove(grpcRequest)).thenReturn(grpcResponse)

        val httpRequest: HttpRequest<Void> = HttpRequest.DELETE("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = httpClient.toBlocking().exchange(httpRequest, Void::class.java)

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.status)
    }

    @Test
    @DisplayName("Deve falhar ao tentar remover uma chave inexistente")
    internal fun deveFalharAoTentarRemoverUmaChaveInexistente() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val grpcRequest = grpcRequest(clienteId = clienteId, chavePixId = pixId)

        Mockito.`when`(grpcClient.remove(grpcRequest)).thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val httpRequest: HttpRequest<Void> = HttpRequest.DELETE("/api/v1/clientes/$clienteId/pix/$pixId")
        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Void::class.java)
        }

        Assertions.assertEquals(HttpStatus.NOT_FOUND, error.status)
    }

    @Test
    @DisplayName("Deve falhar com clienteId inválido")
    internal fun deveFalharComClienteInvalido() {
        val clienteId = "abc123"
        val pixId = UUID.randomUUID().toString()

        val httpRequest: HttpRequest<Void> = HttpRequest.DELETE("/api/v1/clientes/$clienteId/pix/$pixId")
        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Void::class.java)
        }

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }

    @Test
    @DisplayName("Deve falhar com chavePixId inválida")
    internal fun deveFalharComChaveInvalida() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = "123abc"

        val httpRequest: HttpRequest<Void> = HttpRequest.DELETE("/api/v1/clientes/$clienteId/pix/$pixId")
        val error = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Void::class.java)
        }

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }

    private fun grpcRequest(
        clienteId: String = UUID.randomUUID().toString(),
        chavePixId: String = UUID.randomUUID().toString()
    ): RemoveChaveRequest {
        return RemoveChaveRequest.newBuilder()
            .setClienteId(clienteId)
            .setChavePixId(chavePixId)
            .build()
    }

    private fun grpcResponse(
        clienteId: String = UUID.randomUUID().toString(),
        chavePixId: String = UUID.randomUUID().toString()
    ): RemoveChaveResponse {
        return RemoveChaveResponse.newBuilder()
            .setChavePixId(chavePixId)
            .setClienteId(clienteId)
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerRemoveServiceGrpc.KeyManagerRemoveServiceBlockingStub::class.java)
    }
}