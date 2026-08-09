package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.ticketcontrato

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TicketContratoGenerator {

    fun generarTicketTermico(
        prestamo: PrestamoEntity,
        nombreCliente: String,
        cedulaCliente: String,
        nombreAdmin: String
    ): String {
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        return """
        --------------------------------
                   TACOBRAO             
             PAGARÉ DE PRÉSTAMO         
        --------------------------------
        Fecha: $fecha
        Préstamo No: #${prestamo.id}
        Cliente: $nombreCliente
        Cédula: $cedulaCliente
        --------------------------------
        Monto Aprobado : RD$ ${prestamo.montoSolicitado}
        Tasa Interés   : ${prestamo.porcentajeInteres}%
        Cuotas         : ${prestamo.cantidadCuotas} (${prestamo.frecuenciaPago})
        Monto x Cuota  : RD$ ${prestamo.montoCuota}
        Total a Pagar  : RD$ ${prestamo.totalAPagar}
        --------------------------------
        Me comprometo incondicionalmente
        a pagar la suma arriba indicada 
        según la frecuencia acordada.
        
        
        
        ________________________________
              Firma del Cliente
              
        Atendido por: $nombreAdmin
        --------------------------------
            ¡Gracias por su confianza!  
        --------------------------------
        
        
        """.trimIndent()
    }
}
