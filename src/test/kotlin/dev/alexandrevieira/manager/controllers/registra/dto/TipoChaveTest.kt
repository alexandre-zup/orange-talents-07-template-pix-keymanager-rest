package dev.alexandrevieira.manager.controllers.registra.dto

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest {

    @Nested
    inner class ALEATORIA {
        @Test
        @DisplayName("Deve ser valida se nao possuir valor")
        internal fun deveSerValidaSeNaoPossuirValor() {
            with(TipoChave.ALEATORIA) {
                Assertions.assertTrue(valida(null))
                Assertions.assertTrue(valida(""))
                Assertions.assertTrue(valida(" "))
            }
        }

        @Test
        @DisplayName("Deve ser invalida se chave possuir valor")
        internal fun deveSerInvalidaSeChavePossuirValor() {
            with(TipoChave.ALEATORIA) {
                Assertions.assertFalse(valida("algum valor"))
            }
        }
    }

    @Nested
    inner class CPF {
        @Test
        @DisplayName("Deve ser valida se CPF valido")
        internal fun deveSerValidaSeCpfValido() {
            with(TipoChave.CPF) {
                Assertions.assertTrue(valida("05982564079"))
                Assertions.assertTrue(valida("21115799045"))
                Assertions.assertTrue(valida("63198340055"))
            }
        }

        @Test
        @DisplayName("Deve ser invalida se CPF invalido")
        internal fun deveSerInvalidaSeCpfInvalido() {
            with(TipoChave.CPF) {
                Assertions.assertFalse(valida("12345678900"))
                Assertions.assertFalse(valida("12312312312"))
            }
        }

        @Test
        @DisplayName("Deve ser invalida se CPF em formato invalido")
        internal fun deveSerInvalidaSeCpfEmFormatoInvalido() {
            with(TipoChave.CPF) {
                Assertions.assertFalse(valida("059.825.640-79"))
                Assertions.assertFalse(valida("+5534991999177"))
                Assertions.assertFalse(valida("email@email.com"))
                Assertions.assertFalse(valida(" "))
                Assertions.assertFalse(valida(null))
            }
        }
    }

    @Nested
    inner class EMAIL {
        @Test
        @DisplayName("Deve ser valida se email em formato valido")
        internal fun deveSerValidaSeEmailEmFormatoValido() {
            with(TipoChave.EMAIL) {
                Assertions.assertTrue(valida("email@email.com"))
                Assertions.assertTrue(valida("email@email.com.it"))
                Assertions.assertTrue(valida("email@us.gov"))
            }
        }

        @Test
        @DisplayName("Deve ser invalida se email em formato invalido")
        internal fun deveSerInvalidaSeEmailEmFormatoInvalido() {
            with(TipoChave.EMAIL) {
                Assertions.assertFalse(valida("email@email"))
                Assertions.assertFalse(valida("email@email.c"))
                Assertions.assertFalse(valida("email@co"))
                Assertions.assertFalse(valida("12345678900"))
                Assertions.assertFalse(valida("+5534991979197"))
                Assertions.assertFalse(valida(" "))
                Assertions.assertFalse(valida(null))
            }
        }
    }

    @Nested
    inner class CELULAR {
        @Test
        @DisplayName("Deve ser valida se celular em formato valido")
        internal fun deveSerValidaSeCelularEmFormatoValido() {
            with(TipoChave.CELULAR) {
                Assertions.assertTrue(valida("+5534991779177"))
                Assertions.assertTrue(valida("+5511984305543"))
                Assertions.assertTrue(valida("+12125557385"))
            }
        }

        @Test
        @DisplayName("Deve ser invalida se email em formato invalido")
        internal fun deveSerInvalidaSeEmailEmFormatoInvalido() {
            with(TipoChave.CELULAR) {
                Assertions.assertFalse(valida("+55"))
                Assertions.assertFalse(valida("034991779177"))
                Assertions.assertFalse(valida("5534991977155"))
                Assertions.assertFalse(valida("12345678900"))
                Assertions.assertFalse(valida("email@email.com"))
                Assertions.assertFalse(valida(null))
            }
        }
    }
}