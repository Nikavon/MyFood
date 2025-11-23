package myfood;

import java.util.*;

public class MyFoodFacade {

    private Map<Integer, Map<String, String>> usuarios = new HashMap<>();
    private Map<Integer, Map<String, String>> empresas = new HashMap<>();
    private Map<Integer, Map<String, String>> produtos = new HashMap<>();
    private Map<Integer, Map<String, String>> pedidos = new HashMap<>();
    private Map<Integer, Map<String, String>> entregas = new HashMap<>();
    private Map<Integer, Integer> pedidoParaEntrega = new HashMap<>();
    private Set<Integer> entregadorOcupado = new HashSet<>();
    // entregador -> lista de empresas nas quais trabalha (ordem de cadastro)
    private Map<Integer, List<Integer>> entregadorEmpresas = new HashMap<>();
    // empresa -> lista de entregadores (ordem de cadastro)
    private Map<Integer, List<Integer>> empresaEntregadores = new HashMap<>();

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
        // limpar estruturas de entregas e entregadores
        entregas.clear();
        pedidoParaEntrega.clear();
        entregadorOcupado.clear();
        entregadorEmpresas.clear();
        empresaEntregadores.clear();
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

    // Sobre carga para criar um usuario do tipo entregador
    public int criarUsuario(String nome, String email, String senha, String endereco, String veiculo, String placa) {
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (email == null || email.trim().isEmpty()) throw new RuntimeException("Email invalido");
        if (!email.contains("@")) throw new RuntimeException("Email invalido");
        if (senha == null || senha.trim().isEmpty()) throw new RuntimeException("Senha invalido");
        if (endereco == null || endereco.trim().isEmpty()) throw new RuntimeException("Endereco invalido");
        if (veiculo == null || veiculo.trim().isEmpty()) throw new RuntimeException("Veiculo invalido");
        if (placa == null || placa.trim().isEmpty()) throw new RuntimeException("Placa invalido");

        // checar placa primeiro (prioridade de erro esperada pelos testes)
        for (var entry : usuarios.entrySet()) {
            Map<String, String> u = entry.getValue();
            if (placa.equals(u.get("placa"))) throw new RuntimeException("Placa invalido");
        }
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
        u.put("veiculo", veiculo);
        u.put("placa", placa);
        usuarios.put(id, u);
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

    // Nova sobrecarga para criar mercados com horario e tipoMercado
    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String abre, String fecha, String tipoMercado) {
        if (tipoEmpresa == null || tipoEmpresa.trim().isEmpty()) throw new RuntimeException("Tipo de empresa invalido");
        if (!"mercado".equals(tipoEmpresa)) throw new RuntimeException("Tipo de empresa invalido");
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (endereco == null || endereco.trim().isEmpty()) throw new RuntimeException("Endereco da empresa invalido");

        // valida dono
        Map<String,String> donoUser = usuarios.get(dono);
        if (donoUser == null || donoUser.get("cpf") == null || donoUser.get("cpf").isEmpty()) {
            throw new RuntimeException("Usuario nao pode criar uma empresa");
        }

        // valida horario: se algum campo vazio, analisar o outro para decidir a mensagem
        // tratar explicitamente empty-string (abre="" ou fecha="") como formato invalido
        if ((abre != null && abre.length() == 0) || (fecha != null && fecha.length() == 0)) {
            throw new RuntimeException("Formato de hora invalido");
        }
        boolean abreEmpty = (abre == null || abre.trim().isEmpty());
        boolean fechaEmpty = (fecha == null || fecha.trim().isEmpty());
        if (abreEmpty || fechaEmpty) {
            String s = abreEmpty ? fecha : abre; // o que não está vazio
            if (s == null || s.trim().isEmpty()) throw new RuntimeException("Horario invalido");
            String ss = s.trim();
            if (ss.contains(":")) {
                String[] ps = ss.split(":");
                if (ps.length == 2) {
                    try {
                        int hh = Integer.parseInt(ps[0]);
                        int mm = Integer.parseInt(ps[1]);
                        if (hh < 0 || hh > 23 || mm < 0 || mm > 59) throw new RuntimeException("Formato de hora invalido");
                        else throw new RuntimeException("Horario invalido");
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException("Formato de hora invalido");
                    }
                } else throw new RuntimeException("Formato de hora invalido");
            } else throw new RuntimeException("Formato de hora invalido");
        }
        int[] t1 = parseHora(abre);
        int[] t2 = parseHora(fecha);

        if (t1 == null || t2 == null) throw new RuntimeException("Formato de hora invalido");
        // checar range: quando ambos horários estão presentes e fora de faixa -> Horario invalido
        if (!isHoraValida(t1) || !isHoraValida(t2)) throw new RuntimeException("Horario invalido");
        int minutes1 = t1[0] * 60 + t1[1];
        int minutes2 = t2[0] * 60 + t2[1];
        if (minutes1 >= minutes2) throw new RuntimeException("Horario invalido");

        // valida tipoMercado
        if (tipoMercado == null || tipoMercado.trim().isEmpty()) throw new RuntimeException("Tipo de mercado invalido");
        String tm = tipoMercado.trim();
        if (!tm.equals("supermercado") && !tm.equals("minimercado") && !tm.equals("atacadista")) {
            throw new RuntimeException("Tipo de mercado invalido");
        }

        // checar regras de nome/endereco
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
        e.put("abre", abre);
        e.put("fecha", fecha);
        e.put("tipoMercado", tm);
        empresas.put(id, e);
        return id;
    }

    // Nova sobrecarga para criar farmacias com atributo aberto24Horas e numeroFuncionarios
    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, boolean aberto24Horas, int numeroFuncionarios) {
        if (tipoEmpresa == null || tipoEmpresa.trim().isEmpty()) throw new RuntimeException("Tipo de empresa invalido");
        if (!"farmacia".equals(tipoEmpresa)) throw new RuntimeException("Tipo de empresa invalido");
        if (nome == null || nome.trim().isEmpty()) throw new RuntimeException("Nome invalido");
        if (endereco == null || endereco.trim().isEmpty()) throw new RuntimeException("Endereco da empresa invalido");

        // valida dono
        Map<String,String> donoUser = usuarios.get(dono);
        if (donoUser == null || donoUser.get("cpf") == null || donoUser.get("cpf").isEmpty()) {
            throw new RuntimeException("Usuario nao pode criar uma empresa");
        }

        if (numeroFuncionarios <= 0) throw new RuntimeException("Numero de funcionarios invalido");

        // checar regras de nome/endereco
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
        e.put("aberto24Horas", String.valueOf(aberto24Horas));
        e.put("numeroFuncionarios", String.valueOf(numeroFuncionarios));
        empresas.put(id, e);
        return id;
    }

    // Cadastra um entregador em uma empresa
    public void cadastrarEntregador(int empresa, int entregador) {
        if (!empresas.containsKey(empresa)) throw new RuntimeException("Empresa nao cadastrada");
        Map<String,String> u = usuarios.get(entregador);
        if (u == null) throw new RuntimeException("Usuario nao cadastrado.");
        if (u.getOrDefault("veiculo", "").isEmpty() || u.getOrDefault("placa", "").isEmpty()) {
            throw new RuntimeException("Usuario nao e um entregador");
        }

        List<Integer> eList = empresaEntregadores.getOrDefault(empresa, new ArrayList<>());
        if (eList.contains(entregador)) throw new RuntimeException("Entregador ja cadastrado");
        eList.add(entregador);
        empresaEntregadores.put(empresa, eList);

        List<Integer> empList = entregadorEmpresas.getOrDefault(entregador, new ArrayList<>());
        if (!empList.contains(empresa)) empList.add(empresa);
        entregadorEmpresas.put(entregador, empList);
    }

    // Retorna lista de emails dos entregadores de uma empresa
    public List<String> getEntregadores(int empresa) {
        List<String> res = new ArrayList<>();
        List<Integer> eList = empresaEntregadores.getOrDefault(empresa, new ArrayList<>());
        for (Integer uid : eList) {
            Map<String,String> u = usuarios.get(uid);
            if (u != null) res.add(u.getOrDefault("email", ""));
        }
        return res;
    }

    // Retorna lista de [nome,endereco] das empresas vinculadas a um entregador
    public List<List<String>> getEmpresas(int entregador) {
        Map<String,String> u = usuarios.get(entregador);
        if (u == null) throw new RuntimeException("Usuario nao cadastrado.");
        if (u.getOrDefault("veiculo", "").isEmpty() || u.getOrDefault("placa", "").isEmpty()) {
            throw new RuntimeException("Usuario nao e um entregador");
        }
        List<List<String>> res = new ArrayList<>();
        List<Integer> empList = entregadorEmpresas.getOrDefault(entregador, new ArrayList<>());
        for (Integer eid : empList) {
            Map<String,String> e = empresas.get(eid);
            if (e != null) res.add(List.of(e.getOrDefault("nome", ""), e.getOrDefault("endereco", "")));
        }
        return res;
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

    // altera funcionamento de um mercado
    public void alterarFuncionamento(int mercado, String abre, String fecha) {
        Map<String, String> e = empresas.get(mercado);
        if (e == null) throw new RuntimeException("Nao e um mercado valido");
        String tipo = e.getOrDefault("tipoEmpresa", "");
        if (!"mercado".equals(tipo)) throw new RuntimeException("Nao e um mercado valido");
        // tratar explicitamente empty-string (abre="" ou fecha="") como formato invalido
        if ((abre != null && abre.length() == 0) || (fecha != null && fecha.length() == 0)) {
            throw new RuntimeException("Formato de hora invalido");
        }
        boolean abreEmpty2 = (abre == null || abre.trim().isEmpty());
        boolean fechaEmpty2 = (fecha == null || fecha.trim().isEmpty());
        if (abreEmpty2 || fechaEmpty2) {
            String s = abreEmpty2 ? fecha : abre;
            if (s == null || s.trim().isEmpty()) throw new RuntimeException("Horario invalido");
            String ss = s.trim();
            if (ss.contains(":")) {
                String[] ps = ss.split(":");
                if (ps.length == 2) {
                    try {
                        int hh = Integer.parseInt(ps[0]);
                        int mm = Integer.parseInt(ps[1]);
                        if (hh < 0 || hh > 23 || mm < 0 || mm > 59) throw new RuntimeException("Formato de hora invalido");
                        else throw new RuntimeException("Horario invalido");
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException("Formato de hora invalido");
                    }
                } else throw new RuntimeException("Formato de hora invalido");
            } else throw new RuntimeException("Formato de hora invalido");
        }
        int[] t1 = parseHora(abre);
        int[] t2 = parseHora(fecha);
        if (t1 == null || t2 == null) throw new RuntimeException("Formato de hora invalido");
        if (!isHoraValida(t1) || !isHoraValida(t2)) throw new RuntimeException("Horario invalido");
        int minutes1 = t1[0] * 60 + t1[1];
        int minutes2 = t2[0] * 60 + t2[1];
        if (minutes1 >= minutes2) throw new RuntimeException("Horario invalido");
        e.put("abre", abre);
        e.put("fecha", fecha);
    }

    private int[] parseHora(String hora) {
        if (hora == null) return null;
        hora = hora.trim();
        String[] parts = hora.split(":");
        if (parts.length != 2) return null;
        try {
            int hh = Integer.parseInt(parts[0]);
            int mm = Integer.parseInt(parts[1]);
            // ensure two-digit format for format validation (range checked by caller)
            if (parts[0].length() != 2 || parts[1].length() != 2) return null;
            return new int[] { hh, mm };
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean isHoraValida(int[] t) {
        if (t == null || t.length != 2) return false;
        int hh = t[0];
        int mm = t[1];
        return hh >= 0 && hh <= 23 && mm >= 0 && mm <= 59;
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

    // libera um pedido preparado para ficar 'pronto' para entrega
    public void liberarPedido(int numero) {
        Map<String,String> ped = pedidos.get(numero);
        if (ped == null) throw new RuntimeException("Pedido nao encontrado");
        String estado = ped.getOrDefault("estado", "");
        if ("pronto".equals(estado)) throw new RuntimeException("Pedido ja liberado");
        if (!"preparando".equals(estado)) throw new RuntimeException("Nao e possivel liberar um produto que nao esta sendo preparado");
        ped.put("estado", "pronto");
    }

    // retorna o id do pedido pronto mais antigo para o entregador
    public int obterPedido(int entregador) {
        Map<String,String> u = usuarios.get(entregador);
        if (u == null) throw new RuntimeException("Usuario nao cadastrado.");
        if (u.getOrDefault("veiculo", "").isEmpty() || u.getOrDefault("placa", "").isEmpty()) throw new RuntimeException("Usuario nao e um entregador");
        List<Integer> empresasDoEntregador = entregadorEmpresas.getOrDefault(entregador, new ArrayList<>());
        if (empresasDoEntregador.isEmpty()) throw new RuntimeException("Entregador nao estar em nenhuma empresa.");

        // procurar pedidos prontos de farmacias primeiro
        Integer chosen = null;
        for (var entry : pedidos.entrySet()) {
            int pid = entry.getKey();
            Map<String,String> ped = entry.getValue();
            String estado = ped.getOrDefault("estado", "");
            if (!"pronto".equals(estado)) continue;
            int eid = Integer.parseInt(ped.getOrDefault("empresa", "-1"));
            if (!empresasDoEntregador.contains(eid)) continue;
            Map<String,String> emp = empresas.get(eid);
            String tipo = emp == null ? "" : emp.getOrDefault("tipoEmpresa", "");
            if ("farmacia".equals(tipo)) {
                if (chosen == null || pid < chosen) chosen = pid;
            }
        }
        if (chosen != null) return chosen;

        // se não achar farmacia, procurar por outros
        for (var entry : pedidos.entrySet()) {
            int pid = entry.getKey();
            Map<String,String> ped = entry.getValue();
            String estado = ped.getOrDefault("estado", "");
            if (!"pronto".equals(estado)) continue;
            int eid = Integer.parseInt(ped.getOrDefault("empresa", "-1"));
            if (!empresasDoEntregador.contains(eid)) continue;
            if (chosen == null || pid < chosen) chosen = pid;
        }
        if (chosen == null) throw new RuntimeException("Nao existe pedido para entrega");
        return chosen;
    }

    // cria a entrega e vincula ao pedido, muda estado do pedido para 'entregando'
    public int criarEntrega(int pedido, int entregador, String destino) {
        // checar estado do pedido primeiro (mensagens de erro priorizadas nos testes)
        Map<String,String> ped = pedidos.get(pedido);
        if (ped == null) throw new RuntimeException("Pedido nao encontrado");
        String estado = ped.getOrDefault("estado", "");
        if (!"pronto".equals(estado)) throw new RuntimeException("Pedido nao esta pronto para entrega");

        Map<String,String> u = usuarios.get(entregador);
        if (u == null) throw new RuntimeException("Usuario nao cadastrado.");
        if (u.getOrDefault("veiculo", "").isEmpty() || u.getOrDefault("placa", "").isEmpty()) throw new RuntimeException("Nao e um entregador valido");
        if (entregadorOcupado.contains(entregador)) throw new RuntimeException("Entregador ainda em entrega");

        // criar entrega
        int id = entregas.isEmpty() ? 1 : Collections.max(entregas.keySet()) + 1;
        Map<String,String> ent = new HashMap<>();

        int cid = Integer.parseInt(ped.getOrDefault("cliente", "-1"));
        Map<String,String> cliente = usuarios.get(cid);
        String clienteNome = cliente == null ? "" : cliente.getOrDefault("nome", "");
        String clienteEndereco = cliente == null ? "" : cliente.getOrDefault("endereco", "");

        int eid = Integer.parseInt(ped.getOrDefault("empresa", "-1"));
        Map<String,String> emp = empresas.get(eid);
        String empresaNome = emp == null ? "" : emp.getOrDefault("nome", "");

        String entregadorNome = u.getOrDefault("nome", "");

        String destinoFinal = (destino == null || destino.trim().isEmpty()) ? clienteEndereco : destino;

        // produtos do pedido
        String lista = ped.getOrDefault("produtos", "");
        List<String> nomes = new ArrayList<>();
        if (!lista.isEmpty()) {
            String[] parts = lista.split(",");
            for (String p : parts) {
                if (p == null || p.isBlank()) continue;
                try {
                    int pid = Integer.parseInt(p);
                    Map<String,String> prod = produtos.get(pid);
                    if (prod != null) nomes.add(prod.getOrDefault("nome", ""));
                } catch (NumberFormatException ex) {}
            }
        }

        ent.put("cliente", clienteNome);
        ent.put("empresa", empresaNome);
        ent.put("pedido", String.valueOf(pedido));
        ent.put("entregador", entregadorNome);
        ent.put("destino", destinoFinal);
        ent.put("produtos", "{" + nomes.toString().replace("[", "[") + "}");

        entregas.put(id, ent);
        pedidoParaEntrega.put(pedido, id);
        ped.put("estado", "entregando");
        entregadorOcupado.add(entregador);
        return id;
    }

    public String getEntrega(int id, String atributo) {
        Map<String,String> ent = entregas.get(id);
        if (ent == null) throw new RuntimeException("Nao existe entrega com esse id");
        if (atributo == null || atributo.trim().isEmpty()) throw new RuntimeException("Atributo invalido");
        if (!ent.containsKey(atributo)) throw new RuntimeException("Atributo nao existe");
        return ent.getOrDefault(atributo, "");
    }

    public int getIdEntrega(int pedido) {
        Integer id = pedidoParaEntrega.get(pedido);
        if (id == null) throw new RuntimeException("Nao existe entrega com esse id");
        return id;
    }

    public void entregar(int entrega) {
        Map<String,String> ent = entregas.get(entrega);
        if (ent == null) throw new RuntimeException("Nao existe nada para ser entregue com esse id");
        int pedido = Integer.parseInt(ent.getOrDefault("pedido", "-1"));
        Map<String,String> ped = pedidos.get(pedido);
        if (ped == null) throw new RuntimeException("Pedido nao encontrado");
        ped.put("estado", "entregue");
        // liberar entregador
        String entregadorNome = ent.getOrDefault("entregador", "");
        Integer entregadorId = null;
        for (var entry : usuarios.entrySet()) {
            if (entry.getValue().getOrDefault("nome", "").equals(entregadorNome)) {
                entregadorId = entry.getKey(); break;
            }
        }
        if (entregadorId != null) entregadorOcupado.remove(entregadorId);
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
