<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 - Error interno</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>500</h1>
            <p>Ocurrio un error interno.</p>
            <p class="muted">Si el error persiste, revisa los logs del servidor.</p>
            <p><a href="<%= request.getContextPath() %>/">Volver al inicio</a></p>
        </div>
    </div>
</body>
</html>
