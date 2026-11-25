package br.com.grafica.calculadora

import android.app.Application
import br.com.grafica.calculadora.di.moduloApresentacao
import br.com.grafica.calculadora.di.moduloLogica
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AplicacaoCalculadora : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AplicacaoCalculadora)
            modules(moduloLogica, moduloApresentacao)
        }
    }
}
