package br.com.grafica.calculadora.nucleo.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaComDraggableSheet() {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        // Define a altura mínima visível (equivalente ao minChildSize / initialChildSize)
        sheetPeekHeight = 140.dp,
        // Conteúdo da folha arrastável
        sheetContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    // Define a altura máxima (equivalente ao maxChildSize)
                    .fillMaxHeight(0.85f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(30) { index ->
                    ListItem(
                        headlineContent = { Text("Item da lista #$index") },
                        supportingContent = { Text("Arraste para cima ou role a lista") }
                    )
                    HorizontalDivider()
                }
            }
        }
    ) { innerPadding ->
        // Conteúdo principal da tela (ex: um Mapa ou tela de fundo)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Conteúdo de fundo (ex: Mapa)",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}