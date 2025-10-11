const express = require("express");
const router = express.Router();
const db = require("./database.js");

// Pacotes que instalamos
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { v4: uuidv4 } = require("uuid");

const JWT_SECRET = "seu-segredo-super-secreto-aqui-trocar-depois"; // Em um projeto real, isso viria de uma variável de ambiente

// ROTA DE REGISTRO (SIGNUP)
router.post("/signup", async (req, res) => {
  const { nome, email, password } = req.body;

  if (!nome || !email || !password) {
    return res
      .status(400)
      .json({ error: "Nome, email e senha são obrigatórios." });
  }

  try {
    // Criptografa a senha
    const senha_hash = await bcrypt.hash(password, 10);
    const id = uuidv4();

    const sql =
      "INSERT INTO usuarios (id, nome, email, senha_hash) VALUES (?,?,?,?)";
    const params = [id, nome, email, senha_hash];

    db.run(sql, params, function (err) {
      if (err) {
        // Trata o erro de email duplicado
        if (err.message.includes("UNIQUE constraint failed")) {
          return res.status(409).json({ error: "Este email já está em uso." });
        }
        return res.status(500).json({ error: err.message });
      }
      res
        .status(201)
        .json({ message: "Usuário criado com sucesso!", userId: id });
    });
  } catch (error) {
    res.status(500).json({ error: "Erro ao criar usuário." });
  }
});

// ROTA DE LOGIN
router.post("/login", (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ error: "Email e senha são obrigatórios." });
  }

  const sql = "SELECT * FROM usuarios WHERE email = ?";
  db.get(sql, [email], async (err, user) => {
    if (err) {
      return res.status(500).json({ error: err.message });
    }
    // Se o usuário não for encontrado
    if (!user) {
      return res.status(404).json({ error: "Usuário não encontrado." });
    }

    // Compara a senha enviada com o hash salvo no banco
    const isPasswordMatch = await bcrypt.compare(password, user.senha_hash);
    if (!isPasswordMatch) {
      return res.status(401).json({ error: "Senha inválida." });
    }

    // Se a senha estiver correta, gera o token JWT
    const token = jwt.sign(
      { id: user.id, email: user.email }, // Informações que vão dentro do token
      JWT_SECRET,
      { expiresIn: "1d" } // Token expira em 1 dia
    );

    res.json({
      message: "Login bem-sucedido!",
      token: token,
      user: {
        id: user.id,
        nome: user.nome,
        email: user.email,
      },
    });
  });
});

module.exports = router;
