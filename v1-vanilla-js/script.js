// Aguarda o HTML carregar completamente
document.addEventListener("DOMContentLoaded", () => {
  // ESTADO DA APLICAÇÃO (nossos "bancos de dados" em memória)
  let produtos = JSON.parse(localStorage.getItem("cantinMoci_produtos")) || [];
  let vendas = JSON.parse(localStorage.getItem("cantinMoci_vendas")) || [];
  let carrinho = [];

  // --- SELETORES DE ELEMENTOS ---
  const telaEstoque = document.getElementById("telaEstoque");
  const telaCaixa = document.getElementById("telaCaixa");
  const telaRelatorio = document.getElementById("telaRelatorio");
  const formProduto = document.getElementById("formProduto");
  const listaProdutosDiv = document.getElementById("listaProdutos");
  const produtosParaVendaDiv = document.getElementById("produtosParaVenda");
  const itensCarrinhoUl = document.getElementById("itensCarrinho");
  const totalVendaSpan = document.getElementById("totalVenda");
  const btnFinalizarVenda = document.getElementById("btnFinalizarVenda");

  const isDonationCheckbox = document.getElementById("isDonation");
  const custoProdutoInput = document.getElementById("custoProduto");

  // --- NAVEGAÇÃO ENTRE TELAS ---
  document.getElementById("btnNavEstoque").addEventListener("click", () => {
    telaEstoque.classList.remove("hidden");
    telaCaixa.classList.add("hidden");
    telaRelatorio.classList.add("hidden");
    renderizarListaProdutos();
  });

  document.getElementById("btnNavCaixa").addEventListener("click", () => {
    telaEstoque.classList.add("hidden");
    telaCaixa.classList.remove("hidden");
    telaRelatorio.classList.add("hidden");
    renderizarProdutosParaVenda();
  });

  document.getElementById("btnNavRelatorio").addEventListener("click", () => {
    telaEstoque.classList.add("hidden");
    telaCaixa.classList.add("hidden");
    telaRelatorio.classList.remove("hidden");
    atualizarRelatorio();
  });

  // --- LÓGICA DO CHECKBOX DE DOAÇÃO ---
  isDonationCheckbox.addEventListener("change", () => {
    if (isDonationCheckbox.checked) {
      custoProdutoInput.value = "0";
      custoProdutoInput.disabled = true;
      custoProdutoInput.required = false;
    } else {
      custoProdutoInput.value = "";
      custoProdutoInput.disabled = false;
      custoProdutoInput.required = true;
    }
  });

  // --- LÓGICA DA TELA DE ESTOQUE ---
  formProduto.addEventListener("submit", (e) => {
    e.preventDefault();

    const id = document.getElementById("produtoId").value;
    const nome = document.getElementById("nomeProduto").value.trim();
    const qtdAdicionar = parseInt(document.getElementById("qtdProduto").value);
    const venda = parseFloat(document.getElementById("vendaProduto").value);

    let custoAdicionar;
    if (isDonationCheckbox.checked) {
      custoAdicionar = 0;
    } else {
      custoAdicionar = parseFloat(custoProdutoInput.value);
    }

    if (!nome || isNaN(qtdAdicionar) || isNaN(venda) || isNaN(custoAdicionar)) {
      alert(
        "Por favor, preencha todos os campos corretamente com valores válidos."
      );
      return;
    }

    if (id) {
      const index = produtos.findIndex((p) => p.id == id);
      if (index !== -1) {
        produtos[index] = {
          ...produtos[index],
          nome,
          qtd: qtdAdicionar,
          custo: custoAdicionar,
          venda,
        };
      }
    } else {
      const produtoExistente = produtos.find(
        (p) => p.nome.toLowerCase() === nome.toLowerCase()
      );

      if (produtoExistente) {
        const custoTotalExistente =
          produtoExistente.custo * produtoExistente.qtd;
        const custoTotalAdicionar = custoAdicionar * qtdAdicionar;

        const novaQtdTotal = produtoExistente.qtd + qtdAdicionar;
        const novoCustoTotal = custoTotalExistente + custoTotalAdicionar;

        const novoCustoMedio =
          novaQtdTotal > 0 ? novoCustoTotal / novaQtdTotal : 0;

        produtoExistente.qtd = novaQtdTotal;
        produtoExistente.custo = novoCustoMedio;
      } else {
        const novoProduto = {
          id: Date.now(),
          nome,
          qtd: qtdAdicionar,
          custo: custoAdicionar,
          venda,
        };
        produtos.push(novoProduto);
      }
    }

    salvarProdutos();
    renderizarListaProdutos();
    resetarFormulario();
  });

  function resetarFormulario() {
    formProduto.reset();
    document.getElementById("produtoId").value = "";
    formProduto.querySelector('button[type="submit"]').innerText =
      "Salvar Produto";
    isDonationCheckbox.checked = false;
    custoProdutoInput.disabled = false;
    custoProdutoInput.required = true;
  }

  function salvarProdutos() {
    localStorage.setItem("cantinMoci_produtos", JSON.stringify(produtos));
  }

  function renderizarListaProdutos() {
    listaProdutosDiv.innerHTML = "";
    produtos.forEach((p) => {
      const nome = p.nome || "Produto sem nome";
      const qtd = p.qtd || 0;
      const custo = p.custo || 0;
      const venda = p.venda || 0;
      const id = p.id;

      const produtoDiv = document.createElement("div");
      produtoDiv.className = "produto-item";
      const custoDisplay = custo === 0 ? "(Doação)" : `R$ ${custo.toFixed(2)}`;

      produtoDiv.innerHTML = `
        <div>
            <strong>${nome}</strong><br>
            <small>Estoque: ${qtd} un. | Custo Ponderado: ${custoDisplay} | Venda: R$ ${venda.toFixed(
        2
      )}</small>
        </div>
        <div>
            <button onclick="prepararEdicao(${id})" class="btn-editar">Editar</button>
            <button onclick="excluirProduto(${id})" class="btn-excluir">Excluir</button>
        </div>
      `;
      listaProdutosDiv.appendChild(produtoDiv);
    });
  }

  window.prepararEdicao = (id) => {
    const produto = produtos.find((p) => p.id === id);
    if (!produto) return;

    document.getElementById("produtoId").value = produto.id;
    document.getElementById("nomeProduto").value = produto.nome;
    document.getElementById("qtdProduto").value = produto.qtd;
    custoProdutoInput.value = produto.custo;
    document.getElementById("vendaProduto").value = produto.venda;

    if (produto.custo === 0) {
      isDonationCheckbox.checked = true;
      custoProdutoInput.disabled = true;
      custoProdutoInput.required = false;
    } else {
      isDonationCheckbox.checked = false;
      custoProdutoInput.disabled = false;
      custoProdutoInput.required = true;
    }

    formProduto.querySelector('button[type="submit"]').innerText =
      "Atualizar Produto";
    window.scrollTo(0, 0);
  };

  // ===== FUNÇÃO CORRIGIDA E VERIFICADA =====
  window.excluirProduto = (id) => {
    if (confirm("Tem certeza que deseja excluir este produto?")) {
      // 1. Filtra o array na memória, criando uma nova lista sem o item excluído.
      produtos = produtos.filter((p) => p.id !== id);

      // 2. SALVA a nova lista no localStorage. Este é o passo crucial.
      salvarProdutos();

      // 3. Renderiza a lista na tela para o usuário ver a mudança.
      renderizarListaProdutos();
    }
  };
  // ==========================================

  // --- LÓGICA DA TELA DE CAIXA ---
  function renderizarProdutosParaVenda() {
    produtosParaVendaDiv.innerHTML = "";
    produtos.forEach((p) => {
      if (p.qtd > 0) {
        const btnProduto = document.createElement("button");
        btnProduto.innerText = `${p.nome} (R$ ${p.venda.toFixed(2)})`;
        btnProduto.onclick = () => adicionarAoCarrinho(p.id);
        produtosParaVendaDiv.appendChild(btnProduto);
      }
    });
  }

  window.adicionarAoCarrinho = (id) => {
    const produtoEstoque = produtos.find((p) => p.id === id);
    if (!produtoEstoque) return;
    const itemCarrinho = carrinho.find((item) => item.id === id);
    const qtdNoCarrinho = itemCarrinho ? itemCarrinho.qtd : 0;
    if (produtoEstoque.qtd > qtdNoCarrinho) {
      if (itemCarrinho) {
        itemCarrinho.qtd++;
      } else {
        carrinho.push({ ...produtoEstoque, qtd: 1 });
      }
      renderizarCarrinho();
    } else {
      alert(`Estoque de ${produtoEstoque.nome} esgotado!`);
    }
  };

  window.aumentarDoCarrinho = (id) => {
    adicionarAoCarrinho(id);
  };

  window.diminuirDoCarrinho = (id) => {
    const itemCarrinho = carrinho.find((item) => item.id === id);
    if (!itemCarrinho) return;
    if (itemCarrinho.qtd > 1) {
      itemCarrinho.qtd--;
    } else {
      carrinho = carrinho.filter((item) => item.id !== id);
    }
    renderizarCarrinho();
  };

  function renderizarCarrinho() {
    itensCarrinhoUl.innerHTML = "";
    let total = 0;
    carrinho.forEach((item) => {
      const li = document.createElement("li");
      li.className = "item-carrinho";
      li.innerHTML = `
        <span class="nome">${item.nome}</span>
        <span class="controles">
            <button onclick="diminuirDoCarrinho(${item.id})">-</button>
            <span>${item.qtd}</span>
            <button onclick="aumentarDoCarrinho(${item.id})">+</button>
        </span>
        <span class="subtotal">R$ ${(item.qtd * item.venda).toFixed(2)}</span>
      `;
      itensCarrinhoUl.appendChild(li);
      total += item.qtd * item.venda;
    });
    totalVendaSpan.innerText = total.toFixed(2);
  }

  btnFinalizarVenda.addEventListener("click", () => {
    if (carrinho.length === 0) return;
    carrinho.forEach((itemCarrinho) => {
      const produtoEstoque = produtos.find((p) => p.id === itemCarrinho.id);
      if (produtoEstoque) {
        produtoEstoque.qtd -= itemCarrinho.qtd;
      }
    });
    const totalDaVenda = carrinho.reduce(
      (acc, item) => acc + item.qtd * item.venda,
      0
    );
    vendas.push({ id: Date.now(), itens: [...carrinho], total: totalDaVenda });
    localStorage.setItem("cantinMoci_vendas", JSON.stringify(vendas));
    salvarProdutos();
    carrinho = [];
    renderizarCarrinho();
    renderizarProdutosParaVenda();
    alert("Venda realizada com sucesso!");
  });

  // --- LÓGICA DA TELA DE RELATÓRIO E AÇÕES ---
  function atualizarRelatorio() {
    const custoDoEstoqueAtual = produtos.reduce(
      (acc, p) => acc + p.custo * p.qtd,
      0
    );
    const custoDosItensVendidos = vendas
      .flatMap((venda) => venda.itens)
      .reduce((acc, item) => acc + item.custo * item.qtd, 0);
    const totalInvestidoReal = custoDoEstoqueAtual + custoDosItensVendidos;
    const totalArrecadado = vendas.reduce((acc, v) => acc + v.total, 0);
    const lucroBruto = totalArrecadado - custoDosItensVendidos;
    document.getElementById("totalInvestido").innerText =
      totalInvestidoReal.toFixed(2);
    document.getElementById("totalArrecadado").innerText =
      totalArrecadado.toFixed(2);
    document.getElementById("lucroBruto").innerText = lucroBruto.toFixed(2);
  }

  const btnExportar = document.getElementById("btnExportar");
  const inputImportar = document.getElementById("inputImportar");

  btnExportar.addEventListener("click", () => {
    if (produtos.length === 0 && vendas.length === 0) {
      alert("Não há dados para exportar.");
      return;
    }
    const dadosParaExportar = { produtos: produtos, vendas: vendas };
    const dadosString = JSON.stringify(dadosParaExportar, null, 2);
    const blob = new Blob([dadosString], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `backup_cantinmoci_${new Date()
      .toISOString()
      .slice(0, 10)}.json`;
    a.click();
    URL.revokeObjectURL(url);
    alert("Backup gerado com sucesso!");
  });

  inputImportar.addEventListener("change", (event) => {
    const arquivo = event.target.files[0];
    if (!arquivo) return;
    if (
      !confirm(
        "Tem certeza que deseja importar os dados? Isso irá SOBRESCREVER todos os dados atuais."
      )
    ) {
      inputImportar.value = "";
      return;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const dados = JSON.parse(e.target.result);
        if (dados.produtos && dados.vendas) {
          produtos = dados.produtos;
          vendas = dados.vendas;
          salvarProdutos();
          localStorage.setItem("cantinMoci_vendas", JSON.stringify(vendas));
          alert("Dados importados com sucesso!");
          renderizarListaProdutos();
          renderizarProdutosParaVenda();
          atualizarRelatorio();
        } else {
          alert("Arquivo inválido. Formato não reconhecido.");
        }
      } catch (error) {
        alert("Erro ao ler o arquivo.");
      } finally {
        inputImportar.value = "";
      }
    };
    reader.readAsText(arquivo);
  });

  const btnResetarCaixa = document.getElementById("btnResetarCaixa");

  btnResetarCaixa.addEventListener("click", () => {
    const confirmacao = prompt(
      "ATENÇÃO! Esta ação é irreversível e irá zerar TODO o histórico de vendas, o custo e o lucro.\n\n" +
        "O seu estoque de PRODUTOS NÃO será alterado.\n\n" +
        "Para confirmar, digite ZERAR abaixo:"
    );
    if (confirmacao && confirmacao.trim().toUpperCase() === "ZERAR") {
      vendas = [];
      localStorage.setItem("cantinMoci_vendas", JSON.stringify(vendas));
      atualizarRelatorio();
      alert("O caixa foi zerado com sucesso!");
    } else {
      alert("Operação cancelada.");
    }
  });

  // --- INICIALIZAÇÃO DA APLICAÇÃO ---
  renderizarListaProdutos();
});
