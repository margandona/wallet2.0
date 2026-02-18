<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 - No encontrado</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/app.css">
</head>
<body>
    <div class="page">
        <div class="card">
            <h1>404</h1>
            <p>La pagina solicitada no existe.</p>
            <p><a href="<%= request.getContextPath() %>/">Volver al inicio</a></p>
        </div>
    </div>
</body>
</html>
