const express = require("express");
const app = express();
const PORT = 3333; // Define a porta do nosso servidor

// Middleware para permitir que o servidor entenda JSON
app.use(express.json());

// Uma rota de teste para verificar se o servidor está no arS
app.get("/", (request, response) => {
  response.json({ message: "Backend do CantinMoci está online!" });
});

// Inicia o servidor e fica "ouvindo" por requisições
app.listen(PORT, () => console.log(`Servidor rodando na porta ${PORT}`));
