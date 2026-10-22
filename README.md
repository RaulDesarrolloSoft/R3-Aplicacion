# 💊 Sistema de Gestión Farmacia "Cristo Medico"

Este proyecto consiste en una aplicación de escritorio desarrollada en **Java Swing** para la gestión integral de una farmacia (inventario, punto de venta, reportes y usuarios), complementada con un entorno web local simulado que implementa medidas de seguridad (HTTPS, dominios locales y cabeceras HTTP) y documentación legal básica, cumpliendo con los estándares del **Reto 3: R3. Asegura, conecta y evalúa tu despliegue web**.

## 👨‍💻 Autor
- **Estudiante:** Raúl Velázquez González
- **Matrícula:** 24002373
- **Módulo:** Desarrollo de Aplicaciones Web y Móviles (DAWyM)
- **Asesor:** César Alejandro López Castañeda

---

## 🚀 Características Principales

- **Autenticación de Usuarios:** Acceso diferenciado por roles (Administrador y Empleado).
- **Gestión de Inventario:** Alta, baja y modificación de productos farmacéuticos (con control de stock y fechas de caducidad).
- **Punto de Venta (POS):** Interfaz para seleccionar productos, calcular subtotales y totalizar ventas.
- **Reportes de Ventas:** Visualización de ventas del día, historial general y total recaudado.
- **Módulo de Usuarios (Admin):** Registro y gestión del personal.
- **Entorno Web Seguro:** Dominio local personalizado, certificado SSL local y cabeceras de seguridad HTTP para la sección legal.

---

## 🛠️ Tecnologías y Herramientas Utilizadas

- **Lenguaje:** Java (JDK 1.8)
- **Interfaz Gráfica:** Java Swing
- **Base de Datos:** MySQL Server 8.0
- **Conector DB:** `mysql-connector-j-8.0.31.jar`
- **Servidor Web (Simulación):** XAMPP (Apache)
- **Seguridad Web:** `mkcert` (Certificados SSL locales)
- **IDE:** Apache NetBeans IDE 8.2

---

## 📋 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:
1. **Java Development Kit (JDK)** versión 8 o superior.
2. **MySQL Server** (con MySQL Workbench o phpMyAdmin).
3. **Apache NetBeans** (o cualquier IDE compatible con Java).
4. **XAMPP** (solo si deseas probar la configuración del dominio local y SSL).

---

## ⚙️ Instalación y Configuración

### 1. Base de Datos
1. Abre tu gestor de base de datos MySQL.
2. Crea una base de datos llamada `cristo_medico`.
3. Ejecuta el script `cristo_medico.sql` (incluido en la carpeta `database/` o en la raíz del proyecto) para crear las tablas e índices necesarios.

### 2. Aplicación de Escritorio
1. Clona este repositorio o descarga el código fuente.
2. Abre el proyecto en NetBeans.
3. Asegúrate de que la librería `mysql-connector-j-8.0.31.jar` esté agregada al *Classpath* del proyecto (carpeta `Libraries`).
4. Abre el archivo `src/cristomedico/Conexion.java` y configura tus credenciales de MySQL:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/cristo_medico";
   private static final String USER = "root";
   private static final String PASSWORD = "tu_contraseña";
