package myfood.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Pedido {
    private int numero;
    private int clienteId;
    private int empresaId;
    private Map<Integer, Integer> itens;
    private boolean fechado;

    public Pedido() {
        this.itens = new LinkedHashMap<>();
        this.fechado = false;
    }

    public Pedido(int numero, int clienteId, int empresaId) {
        this.numero = numero;
        this.clienteId = clienteId;
        this.empresaId = empresaId;
        this.itens = new LinkedHashMap<>();
        this.fechado = false;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getEmpresaId() { return empresaId; }
    public void setEmpresaId(int empresaId) { this.empresaId = empresaId; }

    public Map<Integer, Integer> getItens() { return itens; }
    public void setItens(Map<Integer, Integer> itens) { this.itens = itens; }

    public boolean isFechado() { return fechado; }
    public void setFechado(boolean fechado) { this.fechado = fechado; }
}
