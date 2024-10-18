#!/bin/bash

# Obtener todas las direcciones IPv4 usando ipconfig y findstr
IP_LOCAL=$(ipconfig | findstr "IPv4" | awk 'NR==2 {print $NF}')

# Verificar si se obtuvo la IP
if [ -z "$IP_LOCAL" ]; then
    echo "Error: No se pudo obtener la IP del adaptador Wi-Fi."
    exit 1
fi

echo "IP obtenida: $IP_LOCAL"

# Definir las rutas de los archivos en el proyecto
ENV_TS="src/environments/ip-config.ts"
NETWORK_CONFIG="android/app/src/main/res/xml/network_security_config.xml"

# Verificar que los archivos existen antes de intentar reemplazar las IPs
if [ ! -f "$ENV_TS" ]; then
    echo "Error: El archivo $ENV_TS no existe."
    exit 1
fi

if [ ! -f "$NETWORK_CONFIG" ]; then
    echo "Error: El archivo $NETWORK_CONFIG no existe."
    exit 1
fi

# Reemplazar la IP en environment.ts
sed -i "s/ipLocal = '[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}';/ipLocal = '$IP_LOCAL';/" "$ENV_TS"
echo "IP actualizada en environment.ts: $IP_LOCAL"

# Reemplazar la IP en network_security_config.xml
sed -i "s/[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}/$IP_LOCAL/" "$NETWORK_CONFIG"
echo "IP actualizada en network_security_config.xml: $IP_LOCAL"

echo "IP actualizada correctamente en todos los archivos."
