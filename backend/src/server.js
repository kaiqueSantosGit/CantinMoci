const express = require("express");
const cors = require("cors");
const db = require("./database.js");
const routes = require("./routes.js"); // Importa nosso novo arquivo de rotas

const app = express();
const PORT = 3333;

// ===== NOVAS CONFIGURAÇÕES =====
app.use(cors()); // Habilita o CORS para todas as rotas
app.use(express.json());
app.use("/api", routes); // Diz ao Express para usar nossas rotas com o prefixo /api
// ===============================

// Rota de teste (pode manter ou remover)
app.get("/", (request, response) => {
  response.json({ message: "Backend do CantinMoci está online!" });
});

app.listen(PORT, () => console.log(`Servidor rodando na porta ${PORT}`));
