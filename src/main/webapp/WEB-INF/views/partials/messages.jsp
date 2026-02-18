<% if (request.getAttribute("error") != null) { %>
    <div class="alert error"><%= request.getAttribute("error") %></div>
<% } %>
<% if (request.getAttribute("success") != null) { %>
    <div class="alert success"><%= request.getAttribute("success") %></div>
<% } %>
