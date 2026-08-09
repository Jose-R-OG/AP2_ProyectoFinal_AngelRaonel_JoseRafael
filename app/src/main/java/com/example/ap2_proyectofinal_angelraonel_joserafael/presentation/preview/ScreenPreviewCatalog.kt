package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.preview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val page = Color(0xFFF8F9FF)
private val green = Color(0xFF006C49)
private val muted = Color(0xFF30323A)
private val outline = Color(0xFFC6C6CD)

/** Catálogo de diseño sin dependencias de Hilt ni de la base de datos. */
@Composable
private fun ScreenPreviewFrame(
    title: String,
    subtitle: String,
    metrics: List<Pair<String, String>> = emptyList(),
    sections: List<String> = emptyList(),
    bottom: String = "Inicio   Clientes   Préstamos   Rutas   Perfil"
) {
    MaterialTheme {
        Column(Modifier.fillMaxSize().background(page)) {
            Row(
                Modifier.fillMaxWidth().background(page).padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, null)
                    Text("  TacoBrao", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Notifications, null)
            }
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = muted)
                metrics.forEach { (label, value) ->
                    Card(
                        Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, outline)
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text(label, color = muted)
                            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = if (label.contains("Cobrado")) green else Color.Black)
                        }
                    }
                }
                sections.forEach { label ->
                    Surface(
                        Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White,
                        border = BorderStroke(1.dp, outline)
                    ) { Text(label, Modifier.padding(18.dp), fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(12.dp))
            }
            Text(bottom, Modifier.fillMaxWidth().background(Color.White).padding(18.dp), color = muted, fontSize = 11.sp)
        }
    }
}

@Preview(name = "01 · Acceso", showBackground = true, showSystemUi = true)
@Composable private fun LoginPreview() = ScreenPreviewFrame("Acceso al sistema", "Credenciales o cuenta de Google", sections = listOf("Correo / usuario", "Contraseña / PIN", "Iniciar sesión", "Continuar con Google"), bottom = "Un administrador por dispositivo")

@Preview(name = "02 · Registrar administrador", showBackground = true, showSystemUi = true)
@Composable private fun RegisterAdminPreview() = ScreenPreviewFrame("Crear administrador", "Solo disponible si el teléfono aún no tiene administrador", sections = listOf("Datos personales", "Negocio y comprobante", "Crear cuenta"), bottom = "Volver al acceso")

@Preview(name = "03 · Activación", showBackground = true, showSystemUi = true)
@Composable private fun ActivationPreview() = ScreenPreviewFrame("Código de activación", "Confirma el registro inicial", sections = listOf("Código de 6 dígitos", "Verificar"), bottom = "TacoBrao")

@Preview(name = "04 · Inicio administrador", showBackground = true, showSystemUi = true)
@Composable private fun AdminHomePreview() = ScreenPreviewFrame("Resumen de operaciones", "Métricas reales sin botón flotante", metrics = listOf("Cobrado hoy" to "RD$ 0.00", "Capital en la calle" to "RD$ 0.00", "Cartera por cobrar" to "RD$ 0.00"), sections = listOf("Acciones rápidas", "Movimientos recientes"))

@Preview(name = "05 · Inicio empleado", showBackground = true, showSystemUi = true)
@Composable private fun EmployeeHomePreview() = ScreenPreviewFrame("Resumen de hoy", "Solo datos y permisos del empleado", metrics = listOf("Total a cobrar" to "RD$ 0.00", "Cobrado" to "RD$ 0.00", "Pendiente" to "RD$ 0.00"), sections = listOf("Acciones autorizadas", "Cobros próximos"))

@Preview(name = "06 · Clientes", showBackground = true, showSystemUi = true)
@Composable private fun ClientsPreview() = ScreenPreviewFrame("Clientes", "Buscar, editar, asignar y solicitar otro préstamo", sections = listOf("Buscar por nombre, cédula o teléfono", "Cliente · Estado · Zona", "Modificar · Asignar · Nuevo préstamo"))

@Preview(name = "07 · Nuevo cliente / préstamo", showBackground = true, showSystemUi = true)
@Composable private fun ClientFormPreview() = ScreenPreviewFrame("Nuevo préstamo", "Incluye documentos, día de pago y tasa autorizada", sections = listOf("Información personal y fotos", "Monto y cuotas", "Frecuencia y día preferido", "Enviar a revisión"))

@Preview(name = "08 · Empleados", showBackground = true, showSystemUi = true)
@Composable private fun EmployeesPreview() = ScreenPreviewFrame("Directorio de empleados", "Editar, reactivar, distribuir clientes y permisos", sections = listOf("Buscar empleado", "Activo / Desactivado", "Ruta y actividad", "Permisos operativos"))

@Preview(name = "09 · Aprobaciones", showBackground = true, showSystemUi = true)
@Composable private fun ApprovalsPreview() = ScreenPreviewFrame("Gestión de préstamos", "Activos · Rechazados · En espera", metrics = listOf("Pendientes" to "0", "Volumen solicitado" to "RD$ 0.00"), sections = listOf("Expediente del cliente", "Detalles · Rechazar · Aprobar"))

@Preview(name = "10 · Ruta administrador", showBackground = true, showSystemUi = true)
@Composable private fun AdminRoutePreview() = ScreenPreviewFrame("Ruta de cobro", "Todas las zonas, fecha más próxima primero", sections = listOf("Todas · Norte · Sur · Este", "ZONA NORTE", "Cliente · próxima fecha · saldo"))

@Preview(name = "11 · Mi ruta empleado", showBackground = true, showSystemUi = true)
@Composable private fun EmployeeRoutePreview() = ScreenPreviewFrame("Mi ruta", "Solo clientes asignados al empleado", sections = listOf("Filtro por zona", "Cliente · dirección · próxima cuota", "Cobrar"))

@Preview(name = "12 · Detalle de cobro", showBackground = true, showSystemUi = true)
@Composable private fun PaymentPreview() = ScreenPreviewFrame("Detalle del préstamo", "Selecciona cuotas y registra el método", metrics = listOf("Saldo pendiente" to "RD$ 0.00"), sections = listOf("Efectivo · Transferencia", "Plan de pagos", "Generar comprobante"))

@Preview(name = "13 · Recibo", showBackground = true, showSystemUi = true)
@Composable private fun ReceiptPreview() = ScreenPreviewFrame("Comprobante de pago", "Cuota N.º, pagos restantes, saldo y firma digital", metrics = listOf("Monto pagado" to "RD$ 0.00"), sections = listOf("Código de validación", "Firma digital", "Imprimir · WhatsApp"), bottom = "Constancia automática de deuda saldada")

@Preview(name = "14 · Cobros recientes", showBackground = true, showSystemUi = true)
@Composable private fun HistoryPreview() = ScreenPreviewFrame("Cobros recientes", "Detalle, reimpresión y WhatsApp", sections = listOf("Cliente · préstamo · método", "Detalle del cobro", "Reimprimir comprobante"))

@Preview(name = "15 · Cierre de caja", showBackground = true, showSystemUi = true)
@Composable private fun CashClosePreview() = ScreenPreviewFrame("Cierre de caja", "Resumen real del turno", metrics = listOf("Total recaudado" to "RD$ 0.00"), sections = listOf("Efectivo · Transferencias", "Cuadre de efectivo", "Imprimir resumen · Finalizar turno"), bottom = "Inicio   Clientes   Cobros   Cierre")

@Preview(name = "16 · Tarifas", showBackground = true, showSystemUi = true)
@Composable private fun TariffsPreview() = ScreenPreviewFrame("Ajustar tarifas", "Por frecuencia y duración", sections = listOf("Diario", "Semanal", "Quincenal", "Mensual", "Guardar cambios"), bottom = "Volver")

@Preview(name = "17 · Notificaciones", showBackground = true, showSystemUi = true)
@Composable private fun NotificationsPreview() = ScreenPreviewFrame("Notificaciones", "Aprobaciones, rechazos y cambios de estado", sections = listOf("Préstamo aprobado", "Solicitud rechazada", "Contrato activado"), bottom = "Marcar como leída")

@Preview(name = "18 · Perfil administrador", showBackground = true, showSystemUi = true)
@Composable private fun AdminProfilePreview() = ScreenPreviewFrame("Perfil administrador", "Foto, logotipo, negocio y seguridad", sections = listOf("Editar foto y logotipo", "Datos del negocio", "Cambiar PIN", "Cerrar sesión"))

@Preview(name = "19 · Perfil empleado", showBackground = true, showSystemUi = true)
@Composable private fun EmployeeProfilePreview() = ScreenPreviewFrame("Perfil empleado", "Datos, ruta y permisos asignados", sections = listOf("Información personal", "Ruta asignada", "Permisos", "Cerrar sesión"))
