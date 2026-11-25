package br.com.grafica.calculadora.dados.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.mp.KoinPlatform

actual fun obterConstrutorBancoDados(): RoomDatabase.Builder<BancoDadosAtelie> {
    val contexto: Context = KoinPlatform.getKoin().get()
    val arquivoBanco = contexto.getDatabasePath("buriti_grafica.db")
    return Room.databaseBuilder<BancoDadosAtelie>(
        context = contexto,
        name = arquivoBanco.absolutePath
    )
}
