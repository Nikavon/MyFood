package myfood;

import java.util.*;

public class MyFoodFacade {

    private Map<Integer, Map<String, String>> usuarios = new HashMap<>();
    private Map<Integer, Map<String, String>> empresas = new HashMap<>();
    private Map<Integer, Map<String, String>> produtos = new HashMap<>();
    private Map<Integer, Map<String, String>> pedidos = new HashMap<>();

    private int nextUserId = 1;
    private int nextEmpresaId = 1;
    private int nextProdutoId = 1;
    private int nextPedidoId = 1;

    public void zerarSistema() {
        usuarios.clear();
        empresas.clear();
        produtos.clear();
        pedidos.clear();
        nextUserId = nextEmpresaId = nextProdutoId = nextPedidoId = 1;
    }

    public void encerrarSistema() {
        // não precisa fazer nada
    }

    public int criarUsuario(String nome, String email, String senha, String endereco) {
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (email == null || email.trim().isEmpty()) throw new RuntimeException("Email invalido");
        if (!email.contains("@")) throw new RuntimeException("Email invalido");
        if (senha == null || senha.trim().isEmpty()) throw new RuntimeException("Senha invalido");
        if (endereco == null || endereco.trim().isEmpty()) throw new RuntimeException("Endereco invalido");
        for (var entry : usuarios.entrySet()) {
            Map<String, String> u = entry.getValue();
            if (email.equals(u.get("email"))) throw new RuntimeException("Conta com esse email ja existe");
        }

        int id = nextUserId++;
        Map<String, String> u = new HashMap<>();
        u.put("nome", nome);
        u.put("email", email);
        u.put("senha", senha);
        u.put("endereco", endereco);
        usuarios.put(id, u);
        return id;
    }

    public int criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) throw new RuntimeException("CPF invalido");
        if (cpf.length() != 14) throw new RuntimeException("CPF invalido");
        int id = criarUsuario(nome, email, senha, endereco);
        usuarios.get(id).put("cpf", cpf);
        return id;
    }

    public int login(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty())
            throw new RuntimeException("Login ou senha invalidos");
        for (var entry : usuarios.entrySet()) {
            Map<String, String> u = entry.getValue();
            if (email.equals(u.get("email")) && senha.equals(u.get("senha"))) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Login ou senha invalidos");
    }

    public String getAtributoUsuario(int id, String atributo) {
        Map<String, String> u = usuarios.get(id);
        if (u == null) throw new RuntimeException("Usuario nao cadastrado.");
        if (atributo == null || atributo.trim().isEmpty()) throw new RuntimeException("Atributo invalido");
        return u.getOrDefault(atributo, "");
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String tipoCozinha) {
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (endereco == null || endereco.trim().isEmpty()) throw new RuntimeException("Endereco invalido");
        Map<String,String> donoUser = usuarios.get(dono);
        if (donoUser == null || donoUser.get("cpf") == null || donoUser.get("cpf").isEmpty()) {
            throw new RuntimeException("Usuario nao pode criar uma empresa");
        }

        for (var entry : empresas.entrySet()) {
            Map<String,String> e = entry.getValue();
            String existingName = e.get("nome");
            String existingEndereco = e.get("endereco");
            int existingDono = Integer.parseInt(e.get("dono"));
            if (existingName != null && existingName.equals(nome)) {
                if (existingDono != dono) {
                    throw new RuntimeException("Empresa com esse nome ja existe");
                } else {
                    if (existingEndereco != null && existingEndereco.equals(endereco)) {
                        throw new RuntimeException("Proibido cadastrar duas empresas com o mesmo nome e local");
                    }
                }
            }
        }

        int id = nextEmpresaId++;
        Map<String, String> e = new HashMap<>();
        e.put("tipoEmpresa", tipoEmpresa);
        e.put("dono", String.valueOf(dono));
        e.put("nome", nome);
        e.put("endereco", endereco);
        e.put("tipoCozinha", tipoCozinha);
        empresas.put(id, e);
        return id;
    }

    public List<List<String>> getEmpresasDoUsuario(int idDono) {
        Map<String,String> donoUser = usuarios.get(idDono);
        if (donoUser == null || donoUser.get("cpf") == null || donoUser.get("cpf").isEmpty()) {
            throw new RuntimeException("Usuario nao pode criar uma empresa");
        }
        List<List<String>> lista = new ArrayList<>();
        for (var entry : empresas.entrySet()) {
            Map<String, String> e = entry.getValue();
            if (Integer.parseInt(e.get("dono")) == idDono) {
                lista.add(List.of(
                    e.get("nome"),
                    e.get("endereco")
                ));
            }
        }
        return lista;
    }

    public String getAtributoEmpresa(int empresa, String atributo) {
        Map<String, String> e = empresas.get(empresa);
        if (e == null) throw new RuntimeException("Empresa nao cadastrada");
        if (atributo == null || atributo.trim().isEmpty()) throw new RuntimeException("Atributo invalido");
        if (atributo.equals("dono")) {
            int idDono = Integer.parseInt(e.get("dono"));
            Map<String,String> donoUser = usuarios.get(idDono);
            if (donoUser == null) return "";
            return donoUser.getOrDefault("nome", "");
        }
        if (!e.containsKey(atributo)) throw new RuntimeException("Atributo invalido");
        return e.getOrDefault(atributo, "");
    }

    public int getIdEmpresa(int idDono, String nome, int indice) {
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (indice < 0) throw new RuntimeException("Indice invalido");
        List<Integer> lista = new ArrayList<>();
        for (var entry : empresas.entrySet()) {
            Map<String, String> e = entry.getValue();
            if (Integer.parseInt(e.get("dono")) == idDono && e.get("nome").equals(nome)) {
                lista.add(entry.getKey());
            }
        }
        if (lista.isEmpty()) throw new RuntimeException("Nao existe empresa com esse nome");
        if (indice >= lista.size()) throw new RuntimeException("Indice maior que o esperado");
        return lista.get(indice);
    }

    public int criarProduto(int empresa, String nome, float valor, String categoria) {
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (valor <= 0f) throw new RuntimeException("Valor invalido");
        if (categoria == null || categoria.trim().isEmpty()) throw new RuntimeException("Categoria invalido");
        if (!empresas.containsKey(empresa)) throw new RuntimeException("Empresa nao encontrada");
        for (var entry : produtos.entrySet()) {
            Map<String, String> pexist = entry.getValue();
            if (Integer.parseInt(pexist.get("empresa")) == empresa && nome.equals(pexist.get("nome"))) {
                throw new RuntimeException("Ja existe um produto com esse nome para essa empresa");
            }
        }

        int id = nextProdutoId++;
        Map<String, String> p = new HashMap<>();
        p.put("empresa", String.valueOf(empresa));
        p.put("nome", nome);
        p.put("valor", String.format(java.util.Locale.US, "%.2f", valor));
        p.put("categoria", categoria);
        produtos.put(id, p);
        return id;
    }

    public void editarProduto(int produto, String nome, float valor, String categoria) {
        Map<String, String> p = produtos.get(produto);
        if (p == null) throw new RuntimeException("Produto nao cadastrado");
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (valor <= 0f) throw new RuntimeException("Valor invalido");
        if (categoria == null || categoria.trim().isEmpty()) throw new RuntimeException("Categoria invalido");

        int empresa = Integer.parseInt(p.get("empresa"));
        for (var entry : produtos.entrySet()) {
            if (entry.getKey() == produto) continue;
            Map<String, String> other = entry.getValue();
            if (Integer.parseInt(other.get("empresa")) == empresa && nome.equals(other.get("nome"))) {
                throw new RuntimeException("Ja existe um produto com esse nome para essa empresa");
            }
        }

        p.put("nome", nome);
        p.put("valor", String.format(java.util.Locale.US, "%.2f", valor));
        p.put("categoria", categoria);
    }

    public String getProduto(String nome, int empresa, String atributo) {
        if (atributo == null || atributo.trim().isEmpty()) throw new RuntimeException("Atributo invalido");
        for (var entry : produtos.entrySet()) {
            Map<String, String> p = entry.getValue();
            if (nome.equals(p.get("nome")) && Integer.parseInt(p.get("empresa")) == empresa) {
                if (atributo.equals("valor")) {
                    return p.getOrDefault("valor", "");
                } else if (atributo.equals("categoria")) {
                    return p.getOrDefault("categoria", "");
                } else if (atributo.equals("empresa")) {
                    int eid = Integer.parseInt(p.get("empresa"));
                    Map<String,String> e = empresas.get(eid);
                    return e == null ? "" : e.getOrDefault("nome", "");
                } else if (atributo.equals("nome")) {
                    return p.getOrDefault("nome", "");
                } else {
                    throw new RuntimeException("Atributo nao existe");
                }
            }
        }
        throw new RuntimeException("Produto nao encontrado");
    }

    public List<String> listarProdutos(int empresa) {
        if (!empresas.containsKey(empresa)) throw new RuntimeException("Empresa nao encontrada");
        List<String> lista = new ArrayList<>();
        for (var entry : produtos.entrySet()) {
            Map<String, String> p = entry.getValue();
            if (Integer.parseInt(p.get("empresa")) == empresa) {
                String nome = p.get("nome");
                if (nome != null && !nome.isEmpty()) lista.add(nome);
            }
        }
        return lista;
    }

    public int criarPedido(int cliente, int empresa) {
        Map<String,String> user = usuarios.get(cliente);
        if (user == null) throw new RuntimeException("Usuario nao cadastrado.");
        if (user.get("cpf") != null && !user.get("cpf").isEmpty()) throw new RuntimeException("Dono de empresa nao pode fazer um pedido");
        if (!empresas.containsKey(empresa)) throw new RuntimeException("Empresa nao cadastrada");
        for (var entry : pedidos.entrySet()) {
            Map<String,String> ped = entry.getValue();
            if (Integer.parseInt(ped.get("cliente")) == cliente && Integer.parseInt(ped.get("empresa")) == empresa) {
                String estado = ped.getOrDefault("estado", "");
                if ("aberto".equals(estado)) {
                    throw new RuntimeException("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
                }
            }
        }

        int id = nextPedidoId++;
        Map<String, String> ped = new HashMap<>();
        ped.put("cliente", String.valueOf(cliente));
        ped.put("empresa", String.valueOf(empresa));
        ped.put("produtos", "");
        ped.put("estado", "aberto");
        pedidos.put(id, ped);
        return id;
    }

    public void adicionarProduto(int numero, int produtoId) {
        Map<String, String> ped = pedidos.get(numero);
        if (ped == null) throw new RuntimeException("Nao existe pedido em aberto");
        String estado = ped.getOrDefault("estado", "");
        if (!"aberto".equals(estado)) throw new RuntimeException("Nao e possivel adcionar produtos a um pedido fechado");
        Map<String,String> prod = produtos.get(produtoId);
        if (prod == null) throw new RuntimeException("Produto invalido");
        int empresaDoPedido = Integer.parseInt(ped.get("empresa"));
        int empresaDoProduto = Integer.parseInt(prod.get("empresa"));
        if (empresaDoPedido != empresaDoProduto) throw new RuntimeException("O produto nao pertence a essa empresa");

        String atual = ped.getOrDefault("produtos", "");
        ped.put("produtos", atual + produtoId + ",");
    }

    public void adcionarProduto(int numero, int produtoId) { adicionarProduto(numero, produtoId); }

    public String getPedidos(int numero, String atributo) {
        Map<String, String> ped = pedidos.get(numero);
        if (ped == null) throw new RuntimeException("Pedido nao encontrado");
        if (atributo == null || atributo.trim().isEmpty()) throw new RuntimeException("Atributo invalido");

        switch (atributo) {
            case "cliente": {
                int cid = Integer.parseInt(ped.get("cliente"));
                Map<String,String> u = usuarios.get(cid);
                return u == null ? "" : u.getOrDefault("nome", "");
            }
            case "empresa": {
                int eid = Integer.parseInt(ped.get("empresa"));
                Map<String,String> e = empresas.get(eid);
                return e == null ? "" : e.getOrDefault("nome", "");
            }
            case "estado": {
                String s = ped.getOrDefault("estado", "");
                return s;
            }
            case "produtos": {
                String lista = ped.getOrDefault("produtos", "");
                if (lista.isEmpty()) return "{[]}";
                String[] parts = lista.split(",");
                List<String> names = new ArrayList<>();
                for (String p : parts) {
                    if (p == null || p.isBlank()) continue;
                    try {
                        int pid = Integer.parseInt(p);
                        Map<String, String> prodm = produtos.get(pid);
                        if (prodm != null) names.add(prodm.getOrDefault("nome", ""));
                    } catch (NumberFormatException ex) {
                    }
                }
                return "{" + names.toString().replace("[", "[") + "}";
            }
            case "valor": {
                String lista = ped.getOrDefault("produtos", "");
                if (lista.isEmpty()) return String.format(Locale.US, "%.2f", 0f);
                String[] parts = lista.split(",");
                float sum = 0f;
                int eid = Integer.parseInt(ped.get("empresa"));
                for (String part : parts) {
                    if (part == null || part.isBlank()) continue;
                    try {
                        int pid = Integer.parseInt(part);
                        Map<String, String> p = produtos.get(pid);
                        if (p != null && Integer.parseInt(p.getOrDefault("empresa", "-1")) == eid) {
                            try { sum += Float.parseFloat(p.getOrDefault("valor", "0")); } catch (Exception ex) { /* ignore */ }
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
                return String.format(Locale.US, "%.2f", sum);
            }
            default:
                throw new RuntimeException("Atributo nao existe");
        }
    }

    public void fecharPedido(int numero) {
        Map<String, String> ped = pedidos.get(numero);
        if (ped == null) throw new RuntimeException("Pedido nao encontrado");
        String estado = ped.getOrDefault("estado", "");
        if (!"aberto".equals(estado)) throw new RuntimeException("Pedido nao encontrado");
        ped.put("estado", "preparando");
    }

    public void removerProduto(int pedido, String produto) {
        if (produto == null || produto.trim().isEmpty()) throw new RuntimeException("Produto invalido");
        Map<String, String> ped = pedidos.get(pedido);
        if (ped == null) throw new RuntimeException("Pedido nao encontrado");
        String estado = ped.getOrDefault("estado", "");
        if (!"aberto".equals(estado)) throw new RuntimeException("Nao e possivel remover produtos de um pedido fechado");

        String lista = ped.getOrDefault("produtos", "");
        if (lista.isEmpty()) throw new RuntimeException("Produto nao encontrado");
        int eid = Integer.parseInt(ped.get("empresa"));
        Integer foundPid = null;
        for (var entry : produtos.entrySet()) {
            int pid = entry.getKey();
            Map<String,String> p = entry.getValue();
            if (produto.equals(p.get("nome")) && Integer.parseInt(p.getOrDefault("empresa","-1")) == eid) {
                if (lista.contains(pid + ",")) { foundPid = pid; break; }
            }
        }
        if (foundPid == null) throw new RuntimeException("Produto nao encontrado");
        String target = foundPid + ",";
        int idx = lista.indexOf(target);
        if (idx < 0) throw new RuntimeException("Produto nao encontrado");
        StringBuilder sb = new StringBuilder();
        sb.append(lista.substring(0, idx));
        sb.append(lista.substring(idx + target.length()));
        ped.put("produtos", sb.toString());
    }

    public int getNumeroPedido(int cliente, int empresa, int indice) {
        List<Integer> lista = new ArrayList<>();
        for (var entry : pedidos.entrySet()) {
            Map<String, String> p = entry.getValue();
            if (Integer.parseInt(p.get("cliente")) == cliente &&
                Integer.parseInt(p.get("empresa")) == empresa) {
                lista.add(entry.getKey());
            }
        }
        if (indice < 0 || indice >= lista.size()) return -1;
        return lista.get(indice);
    }

}
