package br.com.grafica.calculadora.dados.local

import androidx.room.RoomDatabase

expect fun obterConstrutorBancoDados(): RoomDatabase.Builder<BancoDadosAtelie>
