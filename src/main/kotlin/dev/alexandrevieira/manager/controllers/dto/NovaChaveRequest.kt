package dev.alexandrevieira.manager.controllers.dto

import dev.alexandrevieira.manager.validation.ValidPixKey
import dev.alexandrevieira.manager.validation.ValidUUID
import dev.alexandrevieira.stubs.NovaChavePixRequest
import dev.alexandrevieira.stubs.TipoDaChave
import dev.alexandrevieira.stubs.TipoDaConta
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
@ValidPixKey
data class NovaChaveRequest(
    @field:NotNull val tipoChave: TipoChave?,
    val chave: String?,
    @field:NotNull val tipoConta: TipoConta?
) {
    fun toGrpcRequest(@NotBlank @ValidUUID clienteId: String): NovaChavePixRequest {
        return NovaChavePixRequest.newBuilder()
            .setClienteId(clienteId)
            .setTipoChave(TipoDaChave.valueOf(tipoChave!!.name))
            .setValorChave(chave ?: "")
            .setTipoConta(TipoDaConta.valueOf(tipoConta!!.name))
            .build()
    }
}