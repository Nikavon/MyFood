package myfood.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Empresa {
    private int id;
    private String tipoEmpresa;
    private int donoId;
    private String nome;
    private String endereco;
    private String tipoCozinha;
    private Map<Integer, Produto> produtos;

    public Empresa() {
        this.produtos = new LinkedHashMap<>();
    }

    public Empresa(int id, String tipoEmpresa, int donoId, String nome, String endereco, String tipoCozinha) {
        this.id = id;
        this.tipoEmpresa = tipoEmpresa;
        this.donoId = donoId;
        this.nome = nome;
        this.endereco = endereco;
        this.tipoCozinha = tipoCozinha;
        this.produtos = new LinkedHashMap<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipoEmpresa() { return tipoEmpresa; }
    public void setTipoEmpresa(String tipoEmpresa) { this.tipoEmpresa = tipoEmpresa; }

    public int getDonoId() { return donoId; }
    public void setDonoId(int donoId) { this.donoId = donoId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTipoCozinha() { return tipoCozinha; }
    public void setTipoCozinha(String tipoCozinha) { this.tipoCozinha = tipoCozinha; }

    public Map<Integer, Produto> getProdutos() { return produtos; }
    public void setProdutos(Map<Integer, Produto> produtos) { this.produtos = produtos; }
}
