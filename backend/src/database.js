const sqlite3 = require("sqlite3").verbose();
const DB_SOURCE = "cantinmoci.db";

// Conecta ao banco de dados (ou o cria se não existir)
const db = new sqlite3.Database(DB_SOURCE, (err) => {
  if (err) {
    // Erro ao abrir o banco
    console.error(err.message);
    throw err;
  } else {
    console.log("Conectado ao banco de dados SQLite.");
    // Habilita chaves estrangeiras
    db.run("PRAGMA foreign_keys = ON;", (err) => {
      if (err) {
        console.error("Erro ao habilitar chaves estrangeiras:", err.message);
      }
    });
    // Executa a criação das tabelas
    createTables();
  }
});

// Função para criar as tabelas
const createTables = () => {
  const tablesScript = `
        CREATE TABLE IF NOT EXISTS usuarios (
            id TEXT PRIMARY KEY,
            nome TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            senha_hash TEXT NOT NULL
        );

        CREATE TABLE IF NOT EXISTS eventos (
            id TEXT PRIMARY KEY,
            usuario_id TEXT NOT NULL,
            nome TEXT NOT NULL,
            ano INTEGER NOT NULL,
            regional TEXT,
            tipo TEXT NOT NULL,
            FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
        );

        CREATE TABLE IF NOT EXISTS produtos (
            id TEXT PRIMARY KEY,
            evento_id TEXT NOT NULL,
            nome TEXT NOT NULL,
            qtd INTEGER NOT NULL,
            custo REAL NOT NULL,
            venda REAL NOT NULL,
            FOREIGN KEY (evento_id) REFERENCES eventos (id)
        );
        
        CREATE TABLE IF NOT EXISTS clientes (
            id TEXT PRIMARY KEY,
            evento_id TEXT NOT NULL,
            nome TEXT NOT NULL,
            telefone TEXT,
            FOREIGN KEY (evento_id) REFERENCES eventos (id)
        );

        CREATE TABLE IF NOT EXISTS vendas (
            id TEXT PRIMARY KEY,
            evento_id TEXT NOT NULL,
            cliente_id TEXT,

            total REAL NOT NULL,
            status TEXT NOT NULL CHECK(status IN ('paga', 'em_aberto')),
            data DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (evento_id) REFERENCES eventos (id),
            FOREIGN KEY (cliente_id) REFERENCES clientes (id)
        );
    `;

  db.exec(tablesScript, (err) => {
    if (err) {
      console.error("Erro ao criar tabelas:", err.message);
    } else {
      console.log("Tabelas criadas ou já existentes.");
    }
  });
};

module.exports = db;
