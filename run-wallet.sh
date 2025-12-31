#!/bin/bash
# Script para ejecutar la aplicación Sistema Wallet

echo "════════════════════════════════════════"
echo "? WALLET - SISTEMA DE BILLETERA DIGITAL"
echo "════════════════════════════════════════"
echo ""
echo "🚀 Iniciando aplicación..."
echo ""

# Camiar al directorio de la aplicación
cd "$(dirname "$0")"

# Ejecutar la aplicación JAR
java -jar target/wallet-app-1.0.0-jar-with-dependencies.jar

echo ""
echo "✅ Aplicación terminada"
