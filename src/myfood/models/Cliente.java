package myfood.models;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private List<Integer> pedidos;

    public Cliente() {
        super();
        this.pedidos = new ArrayList<>();
    }

    public Cliente(int id, String nome, String email, String senha, String endereco) {
        super(id, nome, email, senha, endereco);
        this.pedidos = new ArrayList<>();
    }

    public List<Integer> getPedidos() { return pedidos; }
    public void setPedidos(List<Integer> pedidos) { this.pedidos = pedidos; }
}
