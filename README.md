[README.md](https://github.com/user-attachments/files/31238856/README.1.md)
# 🌮 TaCobrao - Sistema de Gestión de Préstamos y Cobranzas

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-Jetpack%20Compose-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-blue.svg?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Database](https://img.shields.io/badge/Database-Room-4285F4.svg?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![DI](https://img.shields.io/badge/Dependency%20Injection-Dagger%20Hilt-A4C639.svg?style=for-the-badge)](https://dagger.dev/hilt/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

> **TaCobrao** es una solución móvil integral desarrollada en **Kotlin** y **Jetpack Compose** para optimizar, automatizar y auditar la administración de préstamos, gestión de clientes, rutas de cobro diarias y emisión de comprobantes o contratos de pago.

---

## 📌 Tabla de Contenidos
- [✨ Características Principales](#-características-principales)
- [🏗️ Arquitectura del Sistema](#️-arquitectura-del-sistema)
- [🛠️ Tecnologías y Librerías](#️-tecnologías-y-librerías)
- [📱 Módulos y Funcionalidades](#-módulos-y-funcionalidades)
- [📂 Estructura del Proyecto](#-estructura-del-proyecto)
- [🚀 Instalación y Configuración](#-instalación-y-configuración)
- [👥 Autores](#-autores)

---

## ✨ Características Principales

- 🔐 **Gestión de Usuarios y Roles:** Autenticación segura con soporte para *Administrador* y *Empleado/Cobrador*, incluyendo flujo de solicitudes y registro de admins.
- 👥 **Módulo de Clientes:** Registro detallado, historial crediticio, estados (activo/inactivo) y eliminación lógica (*Soft Delete*).
- 💰 **Administración de Préstamos:** 
  - Cálculo automático de cuotas y fechas de vencimiento.
  - Generación de tablas de amortización según la frecuencia configurada (Diario, Semanal, Quincenal, Mensual).
  - Cálculo dinámico de moras e historial de estados del préstamo.
- 🗺️ **Ruta de Cobro Diaria:** Asignación y seguimiento en tiempo real de los cobros pendientes por cobrador.
- 💵 **Cierre de Caja:** Reportes y balance diario de cobros realizados e incidencias.
- 🧾 **Tickets y Contratos:** Generación de recibos/comprobantes imprimibles y contratos para los clientes.
- 📊 **Dashboard Ejecutivo:** Métricas en tiempo real con estadísticas de cartera, cobros del día, clientes en mora e ingresos.
- 🔔 **Notificaciones Internas:** Sistema de alertas para vencimientos y eventos administrativos.

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue rigurosamente los principios de **Clean Architecture** y el patrón **MVVM (Model-View-ViewModel)**, garantizando escalabilidad, testabilidad y separación clara de responsabilidades:

```
┌───────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                   │
│          Views / Screens ─── ViewModel / StateState       │
└─────────────────────────────┬─────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────┐
│                     Domain Layer                          │
│          Use Cases ─── Domain Models ─── Repositories     │
└─────────────────────────────┬─────────────────────────────┘
                              │
┌─────────────────────────────▼─────────────────────────────┐
│                      Data Layer                           │
│     Room Database (Entities / DAOs) ─── Mappers / Repos   │
└───────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
- **Persistencia Local:** [Room Database](https://developer.android.com/training/data-storage/room) con TypeConverters y Flow
- **Inyección de Dependencias:** [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Asincronía & Reactividad:** Kotlin Coroutines & `StateFlow` / `SharedFlow`
- **Navegación:** Jetpack Compose Navigation
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`)

---

## 📱 Módulos y Funcionalidades

### 1. 🔑 Autenticación & Control de Acceso
- Login multitareas (Cobrador / Administrador).
- Flujo de aprobación para nuevos administradores.

### 2. 📋 Gestión de Préstamos & Amortización
- `LoanCalculator` & `PaymentSchedule`: Algoritmo automatizado para el desglose de amortización.
- `AplicarMoraUseCase`: Recargo automático configurable ante atrasos.
- `RegistrarAbonoUseCase`: Procesamiento inmediato de abonos parciales o totales actualizando el saldo en tiempo real.

### 3. 💵 Finanzas & Cierre de Caja
- Mapeo de transacciones e historial (`CashClosureEntity`, `TransaccionEntity`).
- Control de cuadre diario para cobradores.

---

## 📂 Estructura del Proyecto

```
com.example.ap2_proyectofinal_angelraonel_joserafael/
├── data/                  # Implementación de datos y persistencia
│   ├── database/          # Room DB (PrestamosDatabase, Converters)
│   ├── local/             # DAOs y Entidades (Cliente, Prestamo, Cuota, Transaccion, User, Config)
│   ├── mapper/            # Mapeadores Entity <-> Domain
│   └── repository/        # Implementación de Repositorios
├── di/                    # Módulos de Inyección de Dependencias (Hilt)
├── domain/                # Lógica de Negocio Pura
│   ├── model/             # Modelos de Dominio
│   ├── repository/        # Interfases de Repositorios
│   └── usecases/          # Casos de Uso (RegistrarAbono, AplicarMora, CalculateLoan, etc.)
├── ui/                    # Interfaz de Usuario y ViewModels (Jetpack Compose)
└── MainActivity.kt        # Punto de entrada de la aplicación
```

---

## 🚀 Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/AP2_ProyectoFinal_AngelRaonel_JoseRafael.git
   cd AP2_ProyectoFinal_AngelRaonel_JoseRafael
   ```

2. **Abrir en Android Studio:**
   - Se recomienda **Android Studio Hedgehog / Iguana / Jellyfish** o posterior.
   - Asegúrate de tener configurado el **JDK 17**.

3. **Sincronizar dependencias de Gradle:**
   - El proyecto cargará automáticamente las librerías necesarias especificadas en `build.gradle.kts`.

4. **Ejecutar la Aplicación:**
   - Selecciona un emulador o dispositivo físico con **Android 8.0 (API 26)** o superior y presiona `Run (Shift + F10)`.

---

## 👥 Autores

Proyecto Final desarrollado para la asignatura **Aplicada II**:

- 👨‍💻 **Angel Raonel Guerrero Antigua** - *Desarrollador Lead / Arquitectura & Android*
- 👨‍💻 **José Rafael** - *Desarrollador / Lógica de Negocio & Persistencia*

---
<p center="align">
  <sub>Desarrollado con paciencia y Kotlin para el control eficiente de préstamos.</sub>
</p>
