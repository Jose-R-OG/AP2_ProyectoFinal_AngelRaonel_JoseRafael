package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.clients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
    onAddClientClick: () -> Unit,
    onClientClick: (Long) -> Unit,
    viewModel: ClientListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ClientListContent(
        uiState = uiState,
        onAddClientClick = onAddClientClick,
        onClientClick = onClientClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListContent(
    uiState: ClientListUiState,
    onAddClientClick: () -> Unit,
    onClientClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Clientes") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClientClick) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Cliente")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.clients.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay clientes registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(uiState.clients) { cliente ->
                    ListItem(
                        headlineContent = { Text(cliente.fullName) },
                        supportingContent = { Text(cliente.dni) },
                        leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClientClick(cliente.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClientListScreenPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        ClientListContent(
            uiState = ClientListUiState(
                clients = listOf(
                    Cliente(
                        id = 1,
                        fullName = "Angel Raonel",
                        dni = "402-0000000-1",
                        phone = "809-000-0000",
                        address = "Calle A, #1",
                        zone = "ZONA 1"
                    ),
                    Cliente(
                        id = 2,
                        fullName = "Jose Rafael",
                        dni = "402-0000000-2",
                        phone = "809-000-0001",
                        address = "Calle B, #2",
                        zone = "ZONA 2"
                    )
                )
            ),
            onAddClientClick = {},
            onClientClick = {}
        )
    }
}
