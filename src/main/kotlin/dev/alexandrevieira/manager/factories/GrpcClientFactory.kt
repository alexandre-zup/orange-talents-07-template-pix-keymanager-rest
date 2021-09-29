package dev.alexandrevieira.manager.factories

import dev.alexandrevieira.stubs.KeyManagerConsultaServiceGrpc
import dev.alexandrevieira.stubs.KeyManagerListaServiceGrpc
import dev.alexandrevieira.stubs.KeyManagerRegistraServiceGrpc
import dev.alexandrevieira.stubs.KeyManagerRemoveServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GrpcClientFactory(
    @GrpcChannel("keyManager") val channel: ManagedChannel
) {
    @Singleton
    fun registraClient() = KeyManagerRegistraServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeClient() = KeyManagerRemoveServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultaClient() = KeyManagerConsultaServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaClient() = KeyManagerListaServiceGrpc.newBlockingStub(channel)
}
