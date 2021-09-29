package dev.alexandrevieira.manager.controllers.consulta

import com.google.protobuf.Timestamp
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
import io.micronaut.http.hateoas.JsonError
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Instant
import java.util.*

@MicronautTest
internal class DetalhaChaveControllerTest {
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @field:Inject
    lateinit var grpcClient: KeyManagerConsultaServiceGrpc.KeyManagerConsultaServiceBlockingStub

    @Test
    @DisplayName("Deve consultar uma chave existente")
    internal fun deveRemoverUmaChaveExistente() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val chave = "meu@email.com"
        val tipo = TipoDaChave.EMAIL

        val grpcRequest = grpcRequest(clienteId = clienteId, chavePixId = pixId)
        val grpcResponse = grpcResponse(clienteId = clienteId, chavePixId = pixId, chave = chave, tipo = tipo)

        Mockito.`when`(grpcClient.consulta(grpcRequest)).thenReturn(grpcResponse)

        val httpRequest: HttpRequest<Void> = HttpRequest.GET("/api/v1/clientes/$clienteId/pix/$pixId")
        val response = httpClient.toBlocking().exchange(httpRequest, DetalhaChaveResponse::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(chave,response.body()!!.chave)
        assertEquals(tipo.name,response.body()!!.tipo.name)
    }

    @Test
    @DisplayName("Deve falhar ao tentar buscar uma chave inexistente")
    internal fun deveFalharAoTentarBuscarUmaChaveInexistente() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val grpcRequest = grpcRequest(clienteId = clienteId, chavePixId = pixId)

        Mockito.`when`(grpcClient.consulta(grpcRequest)).thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val httpRequest: HttpRequest<Void> = HttpRequest.GET("/api/v1/clientes/$clienteId/pix/$pixId")
        val error = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, DetalhaChaveResponse::class.java)
        }

        assertNotNull(error)
        assertEquals(HttpStatus.NOT_FOUND, error.status)
    }

    @Test
    @DisplayName("Deve falhar ao buscar com clienteId invalido")
    internal fun deveFalharAoBuscarClienteInvalido() {
        val clienteId = "abc123"
        val pixId = UUID.randomUUID().toString()

        val httpRequest: HttpRequest<Void> = HttpRequest.GET("/api/v1/clientes/$clienteId/pix/$pixId")
        val error = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, DetalhaChaveResponse::class.java)
        }

        assertNotNull(error)
        assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }

    @Test
    @DisplayName("Deve falhar com chavePixId invalida")
    internal fun deveFalharAoBuscarChaveInvalida() {
        val clienteId = UUID.randomUUID().toString()
        val pixId = "123abc"

        val httpRequest: HttpRequest<Void> = HttpRequest.GET("/api/v1/clientes/$clienteId/pix/$pixId")
        val error = org.junit.jupiter.api.assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(httpRequest, DetalhaChaveResponse::class.java)
        }

        assertNotNull(error)
        assertEquals(HttpStatus.BAD_REQUEST, error.status)
    }

    private fun grpcRequest(
        clienteId: String = UUID.randomUUID().toString(),
        chavePixId: String = UUID.randomUUID().toString()
    ): ConsultaChaveRequest {
        val pixId = ConsultaChaveRequest.FiltroPorId.newBuilder()
            .setChavePixId(chavePixId)
            .setClienteId(clienteId)

        return ConsultaChaveRequest.newBuilder()
            .setPixId(pixId)
            .build()
    }

    private fun grpcResponse(
        clienteId: String = UUID.randomUUID().toString(),
        chavePixId: String = UUID.randomUUID().toString(),
        chave: String,
        tipo: TipoDaChave
    ): ConsultaChaveResponse {
        val i = ConsultaChaveResponse.ContaInfo.InstituicaoInfo.newBuilder()
            .setIspb("12345678")
            .setNome("Itau")

        val t = ConsultaChaveResponse.ContaInfo.TitularInfo.newBuilder()
            .setCpf("12345678900")
            .setNome("Alexandre")
            .setId(clienteId)

        val conta = ConsultaChaveResponse.ContaInfo.newBuilder()
            .setAgencia("0001")
            .setNumero("123455")
            .setInstituicao(i)
            .setTitular(t)
            .setTipo(TipoDaConta.CONTA_CORRENTE)

        val instant = Instant.now()
        val criadaEm = Timestamp.newBuilder().setSeconds(instant.epochSecond).setNanos(instant.nano).build()

        return ConsultaChaveResponse.newBuilder()
            .setConta(conta)
            .setChave(chave)
            .setChavePixId(chavePixId)
            .setCriadaEm(criadaEm)
            .setTipo(tipo)
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerConsultaServiceGrpc.KeyManagerConsultaServiceBlockingStub::class.java)
    }
}