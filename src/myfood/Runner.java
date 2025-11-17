package myfood;

import java.util.List;

public class Runner {
    private static int fails = 0;

    private static void ok(String name) { System.out.println("[OK] " + name); }
    private static void fail(String name, String detail) { System.out.println("[FAIL] " + name + " - " + detail); fails++; }

    private static void assertEq(Object expected, Object actual, String name) {
        if (expected == null) {
            if (actual == null) ok(name); else fail(name, "expected=null but actual=" + actual);
        } else if (expected.equals(actual)) {
            ok(name);
        } else {
            fail(name, "expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        Facade f = new Facade();
        f.zerarSistema();

        int usuario = f.criarUsuario("Ana", "ana@example.com", "senha", "Rua A, 123");
        assertEq("Ana", f.getAtributoUsuario(usuario, "nome"), "criarUsuario / getAtributoUsuario");

        int empresa = f.criarEmpresa("restaurante", usuario, "BelaComida", "Av. B, 45", "Italiana");
        assertEq("BelaComida", f.getAtributoEmpresa(empresa, "nome"), "criarEmpresa / getAtributoEmpresa");

        int produto = f.criarProduto(empresa, "Pizza", 25.0f, "Prato");
        String produtosStr = f.listarProdutos(empresa);
        List<String> produtos = parseList(produtosStr);
        if (produtos.contains("Pizza")) ok("criarProduto / listarProdutos"); else fail("criarProduto / listarProdutos", "lista=" + produtosStr);

        int pedido = f.criarPedido(usuario, empresa);
        f.adcionarProduto(pedido, produto);
        String produtosNoPedido = f.getPedidos(pedido, "produtos");
        if (produtosNoPedido.contains(String.valueOf(produto))) ok("adcionarProduto / getPedidos"); else fail("adcionarProduto / getPedidos", "conteudo=" + produtosNoPedido);

        f.fecharPedido(pedido);
        assertEq("true", f.getPedidos(pedido, "fechado"), "fecharPedido");

        System.out.println();
        if (fails == 0) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println(fails + " TEST(S) FAILED");
        }

        System.exit(fails == 0 ? 0 : 1);
    }

    private static java.util.List<String> parseList(String s) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (s == null) return out;
        s = s.trim();
        if (s.startsWith("{") && s.endsWith("}")) s = s.substring(1, s.length()-1).trim();
        if (s.startsWith("[") && s.endsWith("]")) s = s.substring(1, s.length()-1).trim();
        if (s.isEmpty()) return out;
        for (String part : s.split(",")) {
            String p = part.trim();
            if (!p.isEmpty()) out.add(p);
        }
        return out;
    }
}
