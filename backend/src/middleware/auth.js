const jwt = require("jsonwebtoken");
const JWT_SECRET = "seu-segredo-super-secreto-aqui-trocar-depois";

function authMiddleware(req, res, next) {
  const authHeader = req.headers.authorization;

  if (!authHeader) {
    return res.status(401).json({ error: "Nenhum token fornecido." });
  }

  // O token vem no formato "Bearer TOKEN_LONGO"
  const parts = authHeader.split(" ");
  if (parts.length !== 2) {
    return res.status(401).json({ error: "Erro no formato do token." });
  }

  const [scheme, token] = parts;
  if (!/^Bearer$/i.test(scheme)) {
    return res.status(401).json({ error: "Token mal formatado." });
  }

  jwt.verify(token, JWT_SECRET, (err, decoded) => {
    if (err) {
      return res.status(401).json({ error: "Token inválido." });
    }

    // Se o token for válido, anexa o id do usuário na requisição
    req.userId = decoded.id;
    return next(); // Permite que a requisição continue
  });
}

module.exports = authMiddleware;
