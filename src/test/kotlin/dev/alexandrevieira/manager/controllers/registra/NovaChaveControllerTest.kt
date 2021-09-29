package dev.alexandrevieira.manager.controllers.registra

import dev.alexandrevieira.manager.controllers.registra.dto.NovaChaveRequest
import dev.alexandrevieira.manager.controllers.registra.dto.TipoChave
import dev.alexandrevieira.manager.controllers.registra.dto.TipoConta
import dev.alexandrevieira.manager.factories.GrpcClientFactory
import dev.alexandrevieira.stubs.*
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
internal class NovaChaveControllerTest {

    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: KeyManagerRegistraServiceGrpc.KeyManagerRegistraServiceBlockingStub

    @Test
    @DisplayName("Deve criar uma chave EMAIL / CORRENTE")
    internal fun deveCriarUmaChaveEmailCorrente() {
        val clienteId = UUID.randomUUID()
        val pixId = UUID.randomUUID()
        val email = "meu@email.com"
        val tipoChave = TipoChave.EMAIL
        val tipoConta = TipoConta.CONTA_CORRENTE

        val grpcRequest = grpcRequest(clienteId = clienteId, tipoChave = tipoChave, valorChave = email)
        val grpcResponse = grpcResponse(pixId = pixId, clienteId = clienteId)

        Mockito.`when`(grpcClient.registra(grpcRequest)).thenReturn(grpcResponse)

        val novaChaveRequest = httpDTO(tipoChave = tipoChave, valorChave = email, tipoConta = tipoConta)
        val httpRequest: HttpRequest<Any> = HttpRequest.POST("/api/v1/clientes/$clienteId/pix", novaChaveRequest)
        val response = httpClient.toBlocking().exchange(httpRequest, Void::class.java)

        Assertions.assertEquals(HttpStatus.CREATED, response.status)
        Assertions.assertTrue(response.headers.contains("Location"))
        Assertions.assertTrue(response.header("Location")!!.contains(pixId.toString()))
        Assertions.assertTrue(response.header("Location")!!.contains(clienteId.toString()))
    }

    @Test
    @DisplayName("Deve criar uma chave ALEATORIA / POUPANCA")
    internal fun deveCriarUmaChaveAleatoriaPoupanca() {
        val clienteId = UUID.randomUUID()
        val pixId = UUID.randomUUID()

        val grpcRequest = grpcRequest(clienteId = clienteId, tipoConta = TipoConta.CONTA_POUPANCA)
        val grpcResponse = grpcResponse(pixId = pixId, clienteId = clienteId)

        Mockito.`when`(grpcClient.registra(grpcRequest)).thenReturn(grpcResponse)

        val novaChaveRequest = httpDTO(tipoConta = TipoConta.CONTA_POUPANCA)
        val httpRequest: HttpRequest<Any> = HttpRequest.POST("/api/v1/clientes/$clienteId/pix", novaChaveRequest)
        val response = httpClient.toBlocking().exchange(httpRequest, Void::class.java)

        Assertions.assertEquals(HttpStatus.CREATED, response.status)
        Assertions.assertTrue(response.headers.contains("Location"))
        Assertions.assertTrue(response.header("Location")!!.contains(pixId.toString()))
        Assertions.assertTrue(response.header("Location")!!.contains(clienteId.toString()))
    }

    @Test
    @DisplayName("Deve falhar ao tentar uma chave invalida")
    internal fun deveFalharAoTentarUmaChaveInvalida() {
        val clienteId = UUID.randomUUID()
        val email = "meu@email.com"
        val grpcRequest = grpcRequest(clienteId = clienteId, tipoChave = TipoChave.EMAIL, valorChave = email)

        Mockito.`when`(grpcClient.registra(grpcRequest))
            .thenThrow(StatusRuntimeException(Status.ALREADY_EXISTS))

        val novaChaveRequest = httpDTO(tipoChave = TipoChave.EMAIL, valorChave = email)
        val httpRequest: HttpRequest<Any> = HttpRequest.POST("/api/v1/clientes/$clienteId/pix", novaChaveRequest)
        val erro = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, Void::class.java)
        }
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, erro.status)
    }

    private fun grpcRequest(
        clienteId: UUID = UUID.randomUUID(),
        tipoChave: TipoChave = TipoChave.ALEATORIA,
        tipoConta: TipoConta = TipoConta.CONTA_CORRENTE,
        valorChave: String = ""
    ): NovaChavePixRequest {
        return NovaChavePixRequest.newBuilder()
            .setClienteId(clienteId.toString())
            .setTipoChave(TipoDaChave.valueOf(tipoChave.name))
            .setTipoConta(TipoDaConta.valueOf(tipoConta.name))
            .setValorChave(valorChave)
            .build()
    }

    private fun grpcResponse(
        pixId: UUID = UUID.randomUUID(),
        clienteId: UUID = UUID.randomUUID()
    ): NovaChavePixResponse {
        return NovaChavePixResponse.newBuilder()
            .setChavePixId(pixId.toString())
            .setClienteId(clienteId.toString())
            .build()
    }

    private fun httpDTO(
        tipoChave: TipoChave = TipoChave.ALEATORIA,
        valorChave: String = "",
        tipoConta: TipoConta = TipoConta.CONTA_CORRENTE
    ): NovaChaveRequest {
        return NovaChaveRequest(tipoChave = tipoChave, chave = valorChave, tipoConta = tipoConta)
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerRegistraServiceGrpc.KeyManagerRegistraServiceBlockingStub::class.java)
    }
}