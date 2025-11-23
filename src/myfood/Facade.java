package myfood;

public class Facade {
    private static final MyFoodFacade impl = new MyFoodFacade();

    public void zerarSistema() { impl.zerarSistema(); }
    public void encerrarSistema() { impl.encerrarSistema(); }
    public int criarUsuario(String nome, String email, String senha, String endereco) {
        return impl.criarUsuario(nome, email, senha, endereco);
    }
    public int criarUsuario(String nome, String email, String senha, String endereco, String cpf) {
        return impl.criarUsuario(nome, email, senha, endereco, cpf);
    }

    public int criarUsuario(String nome, String email, String senha, String endereco, String veiculo, String placa) {
        return impl.criarUsuario(nome, email, senha, endereco, veiculo, placa);
    }
    public int login(String email, String senha) { return impl.login(email, senha); }
    public String getAtributoUsuario(int id, String atributo) { return impl.getAtributoUsuario(id, atributo); }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String tipoCozinha) {
        return impl.criarEmpresa(tipoEmpresa, dono, nome, endereco, tipoCozinha);
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String abre, String fecha, String tipoMercado) {
        return impl.criarEmpresa(tipoEmpresa, dono, nome, endereco, abre, fecha, tipoMercado);
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, boolean aberto24Horas, int numeroFuncionarios) {
        return impl.criarEmpresa(tipoEmpresa, dono, nome, endereco, aberto24Horas, numeroFuncionarios);
    }

    public String getEmpresasDoUsuario(int idDono) { return "{" + impl.getEmpresasDoUsuario(idDono).toString() + "}"; }
    public String getAtributoEmpresa(int empresa, String atributo) { return impl.getAtributoEmpresa(empresa, atributo); }
    public int getIdEmpresa(int idDono, String nome, int indice) { return impl.getIdEmpresa(idDono, nome, indice); }

    public void alterarFuncionamento(int mercado, String abre, String fecha) { impl.alterarFuncionamento(mercado, abre, fecha); }

    public void cadastrarEntregador(int empresa, int entregador) { impl.cadastrarEntregador(empresa, entregador); }
    public String getEntregadores(int empresa) { return "{" + impl.getEntregadores(empresa).toString() + "}"; }
    public String getEmpresas(int entregador) { return "{" + impl.getEmpresas(entregador).toString() + "}"; }
    public void liberarPedido(int numero) { impl.liberarPedido(numero); }
    public int obterPedido(int entregador) { return impl.obterPedido(entregador); }
    public int criarEntrega(int pedido, int entregador, String destino) { return impl.criarEntrega(pedido, entregador, destino); }
    public String getEntrega(int id, String atributo) { return impl.getEntrega(id, atributo); }
    public int getIdEntrega(int pedido) { return impl.getIdEntrega(pedido); }
    public void entregar(int entrega) { impl.entregar(entrega); }

    public int criarProduto(int empresa, String nome, float valor, String categoria) { return impl.criarProduto(empresa, nome, valor, categoria); }
    public void editarProduto(int produto, String nome, float valor, String categoria) { impl.editarProduto(produto, nome, valor, categoria); }
    public String getProduto(String nome, int empresa, String atributo) { return impl.getProduto(nome, empresa, atributo); }

    public String listarProdutos(int empresa) { return "{" + impl.listarProdutos(empresa).toString() + "}"; }

    public int criarPedido(int cliente, int empresa) { return impl.criarPedido(cliente, empresa); }
  
    public void adicionarProduto(int numero, int produto) { impl.adicionarProduto(numero, produto); }

    public void adcionarProduto(int numero, int produto) { impl.adicionarProduto(numero, produto); }
    public String getPedidos(int numero, String atributo) { return impl.getPedidos(numero, atributo); }
    public void fecharPedido(int numero) { impl.fecharPedido(numero); }
    public void removerProduto(int pedido, String produto) { impl.removerProduto(pedido, produto); }
    public int getNumeroPedido(int cliente, int empresa, int indice) { return impl.getNumeroPedido(cliente, empresa, indice); }

   
}
