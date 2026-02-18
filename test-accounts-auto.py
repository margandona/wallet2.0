#!/usr/bin/env python3
"""
Script para simular pruebas interactivas de gestión de cuentas.
Este script pasa comandos a la aplicación Java.
"""

import subprocess
import time

# Datos de prueba
print("=" * 80)
print("🧪 TEST DE GESTIÓN DE CUENTAS - WALLET")
print("=" * 80)
print()
print("📋 PLAN DE PRUEBAS:")
print("  1. Depositar $1,000,000 a maik (cuenta 7159002131)")
print("  2. Consultar saldo de maik")
print("  3. Retirar $500,000 de marcos (cuenta 7158900319)")
print("  4. Consultar saldo final de marcos")
print()
print("Presione ENTER para iniciar...")
input()

# Commands para la app
commands = [
    "2",           # Opción: Gestión de Cuentas
    "2",           # Opción: Depositar
    "7159002131",  # Número de cuenta maik
    "1000000",     # Monto
    "prueba deposito",  # Descripción
    "s",           # Confirmar
    # Volver...
    "0",           # Volver al menú principal
    # Consultar saldo
    "2",           # Gestión de Cuentas
    "4",           # Consultar Saldo
    "7159002131",  # Número de cuenta
    "0",           # Volver
    # Retirar
    "2",           # Gestión de Cuentas
    "3",           # Retirar
    "7158900319",  # Número de marcos
    "500000",      # Monto
    "prueba retiro",  # Descripción
    "s",           # Confirmar
    "0",           # Volver
    # Consultar saldo final
    "2",           # Gestión de Cuentas
    "4",           # Consultar Saldo
    "7158900319",  # Número de marcos
    "0",           # Volver
    # Salir
    "0"            # Salir
]

# Concatenar todos los comandos con newlines
input_str = "\n".join(commands)

print("🚀 Iniciando aplicación...\n")

try:
    # Ejecutar la app con input
    process = subprocess.Popen(
        ["java", "-jar", "target/wallet-app-1.0.0-jar-with-dependencies.jar"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        cwd=r"C:\Users\marga\Desktop\NeekWorld\boot android\wallet"
    )
    
    # Enviar todos los comandos
    stdout, _ = process.communicate(input=input_str, timeout=30)
    
    print(stdout)
    
except subprocess.TimeoutExpired:
    print("⏱️ Timeout - la app tardó demasiado")
    process.kill()
except Exception as e:
    print(f"❌ Error: {e}")

print("\n" + "=" * 80)
print("✓ Test completado. Verificando base de datos...")
print("=" * 80 + "\n")

# Ejecutar verificación
subprocess.run(["python", "verify_db.py"], cwd=r"C:\Users\marga\Desktop\NeekWorld\boot android\wallet")
