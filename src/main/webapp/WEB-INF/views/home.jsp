<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>AlkeWallet - Sistema de Billetera Digital</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Sistema de gestion de billetera digital con arquitectura limpia">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <!-- Header -->
        <header class="app-header">
            <h1>
                <span>🏦</span>
                <span>AlkeWallet</span>
            </h1>
            <p class="subtitle">Sistema de gestion de billetera digital con arquitectura limpia</p>
            <div class="tech-stack">
                <span class="tech-badge">Java 21</span>
                <span class="tech-badge">Jakarta EE 10</span>
                <span class="tech-badge">Jetty 12</span>
                <span class="tech-badge">JPA/Hibernate</span>
                <span class="tech-badge">SQLite</span>
            </div>
        </header>
        
        <!-- Main Card -->
        <main class="card" role="main">
            <div class="options-grid">
                <!-- Operaciones de Cuenta -->
                <section class="option-section" aria-labelledby="cuenta-operations">
                    <h3 id="cuenta-operations">💰 Operaciones de Cuenta</h3>
                    <ul role="list">
                        <li><a href="<%= request.getContextPath() %>/saldo">Consultar saldo</a></li>
                        <li><a href="<%= request.getContextPath() %>/deposito">Depositar fondos</a></li>
                        <li><a href="<%= request.getContextPath() %>/retiro">Retirar fondos</a></li>
                        <li><a href="<%= request.getContextPath() %>/transferencia">Transferir fondos</a></li>
                        <li>
                            <a href="<%= request.getContextPath() %>/historial">
                                Historial de transacciones
                                <span class="badge">Con paginacion y filtros</span>
                            </a>
                        </li>
                    </ul>
                </section>
                
                <!-- Gestion de Usuarios -->
                <section class="option-section" aria-labelledby="user-management">
                    <h3 id="user-management">👥 Gestion de Usuarios</h3>
                    <ul role="list">
                        <li><a href="<%= request.getContextPath() %>/usuarios/nuevo">Crear usuario</a></li>
                        <li><a href="<%= request.getContextPath() %>/usuarios/buscar">Buscar usuario</a></li>
                        <li>
                            <a href="<%= request.getContextPath() %>/usuarios/lista">
                                Listado de usuarios
                                <span class="badge">Con paginacion y filtros</span>
                            </a>
                        </li>
                    </ul>
                </section>
            </div>
            
            <div class="hint" role="complementary">
                <strong>💡 Funcionalidades destacadas</strong>
                <ul>
                    <li><strong>Paginacion inteligente:</strong> Visualiza hasta 20 registros por pagina</li>
                    <li><strong>Filtros avanzados:</strong> Busca por tipo, fechas, email y estado</li>
                    <li><strong>Persistencia automatica:</strong> Base de datos SQLite integrada</li>
                    <li><strong>Validaciones robustas:</strong> Formularios con validacion en servidor</li>
                </ul>
            </div>
        </main>
        
        <!-- Footer -->
        <footer class="app-footer" role="contentinfo">
            <p>© 2026 AlkeWallet - Proyecto academico con Clean Architecture</p>
            <p class="muted">Version 1.0.0 | <a href="https://github.com/margandona/wallet2.0" style="color: white;">Ver en GitHub</a></p>
        </footer>
    </div>
</body>
</html>
