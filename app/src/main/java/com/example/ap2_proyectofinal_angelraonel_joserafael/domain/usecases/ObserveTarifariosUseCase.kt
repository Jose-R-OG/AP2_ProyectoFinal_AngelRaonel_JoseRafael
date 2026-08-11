package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTarifariosUseCase @Inject constructor(
    private val repository: TarifarioRepository
) {
    operator fun invoke(): Flow<List<Tarifario>> {
        return repository.getActiveTarifarios()
    }
}