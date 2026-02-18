<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>AlkeWallet - Web</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>🏦 AlkeWallet Web</h1>
            <p>Sistema de gestion de billetera digital con arquitectura limpia.</p>
            
            <div class="hint">
                <strong>💰 Operaciones de Cuenta</strong>
                <ul>
                    <li><a href="<%= request.getContextPath() %>/saldo">Consultar saldo</a></li>
                    <li><a href="<%= request.getContextPath() %>/deposito">Depositar fondos</a></li>
                    <li><a href="<%= request.getContextPath() %>/retiro">Retirar fondos</a></li>
                    <li><a href="<%= request.getContextPath() %>/transferencia">Transferir fondos</a></li>
                    <li><a href="<%= request.getContextPath() %>/historial">Historial de transacciones</a> <span class="badge">Con paginacion y filtros</span></li>
                </ul>
            </div>
            
            <div class="hint">
                <strong>👥 Gestion de Usuarios</strong>
                <ul>
                    <li><a href="<%= request.getContextPath() %>/usuarios/nuevo">Crear usuario</a></li>
                    <li><a href="<%= request.getContextPath() %>/usuarios/buscar">Buscar usuario</a></li>
                    <li><a href="<%= request.getContextPath() %>/usuarios/lista">Listado de usuarios</a> <span class="badge">Con paginacion y filtros</span></li>
                </ul>
            </div>
            
            <p class="muted" style="margin-top: 2rem;">Stack: Java 21 + Jakarta EE 10 + Jetty 12 + JPA/Hibernate + SQLite</p>
        </div>
    </div>
</body>
</html>
