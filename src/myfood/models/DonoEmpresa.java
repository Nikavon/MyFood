package myfood.models;

import java.util.ArrayList;
import java.util.List;

public class DonoEmpresa extends Usuario {
    private List<Integer> empresas;

    public DonoEmpresa() {
        super();
        this.empresas = new ArrayList<>();
    }

    public DonoEmpresa(int id, String nome, String email, String senha, String endereco) {
        super(id, nome, email, senha, endereco);
        this.empresas = new ArrayList<>();
    }

    public List<Integer> getEmpresas() { return empresas; }
    public void setEmpresas(List<Integer> empresas) { this.empresas = empresas; }
}
