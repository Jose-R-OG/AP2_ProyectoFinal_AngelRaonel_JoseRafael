package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase.ObserveClientesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ClientListUiState(
    val clients: List<Cliente> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ClientListViewModel @Inject constructor(
    observeClientesUseCase: ObserveClientesUseCase
) : ViewModel() {

    val uiState: StateFlow<ClientListUiState> = observeClientesUseCase()
        .map { ClientListUiState(clients = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ClientListUiState(isLoading = true)
        )
}
