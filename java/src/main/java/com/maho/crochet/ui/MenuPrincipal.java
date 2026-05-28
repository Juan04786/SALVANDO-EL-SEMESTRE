package com.maho.crochet.ui;

import com.maho.crochet.entity.*;
import com.maho.crochet.repository.*;
import com.maho.crochet.service.PdfService;
import com.maho.crochet.util.JpaUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * MenuPrincipal — Interfaz de consola para gestión CRUD.
 * Crocheterias Maho | CESDE Bello 2025 | Maryam
 *
 * Árbol de menús (ramas padre → hijos):
 *   Principal
 *   ├── 1. Productos (CRUD + stock)
 *   ├── 2. Usuarios  (CRUD)
 *   ├── 3. Pedidos   (CRUD + estado)
 *   ├── 4. Reportes  (PDF con iText)
 *   └── 0. Salir
 */
public class MenuPrincipal {

    private static final Scanner sc = new Scanner(System.in);

    private static final ProductoRepository productoRepo = new ProductoRepository();
    private static final UsuarioRepository  usuarioRepo  = new UsuarioRepository();
    private static final PedidoRepository   pedidoRepo   = new PedidoRepository();
    private static final PdfService         pdfService   = new PdfService();

    // ── COLORES ANSI para consola ────────────────────────────
    private static final String RESET  = "\033[0m";
    private static final String BOLD   = "\033[1m";
    private static final String TERRA  = "\033[38;5;130m";
    private static final String ROSE   = "\033[38;5;175m";
    private static final String GREEN  = "\033[32m";
    private static final String RED    = "\033[31m";

    public static void main(String[] args) {
        // Registrar shutdown hook para cerrar la conexión limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            JpaUtil.close();
            System.out.println(TERRA + "\n🧶 ¡Hasta pronto, Maryam!" + RESET);
        }));

        imprimirBanner();

        boolean corriendo = true;
        while (corriendo) {
            imprimirMenuPrincipal();
            int opcion = leerInt("Elige una opción");
            switch (opcion) {
                case 1  -> menuProductos();
                case 2  -> menuUsuarios();
                case 3  -> menuPedidos();
                case 4  -> menuReportesPdf();
                case 0  -> corriendo = false;
                default -> System.out.println(RED + "Opción inválida." + RESET);
            }
        }
    }

    // ================================================================
    //  BANNER
    // ================================================================
    private static void imprimirBanner() {
        System.out.println(TERRA + BOLD);
        System.out.println("  ╔══════════════════════════════════════╗");
        System.out.println("  ║     🧶  CROCHETERIAS MAHO  🧶        ║");
        System.out.println("  ║   Sistema de Gestión — CESDE 2025    ║");
        System.out.println("  ╚══════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private static void imprimirMenuPrincipal() {
        System.out.println(TERRA + "\n╔══ MENÚ PRINCIPAL ══════════════════════╗" + RESET);
        System.out.println("  1. " + BOLD + "Productos" + RESET + "   (CRUD + stock)");
        System.out.println("  2. " + BOLD + "Usuarios"  + RESET + "    (CRUD)");
        System.out.println("  3. " + BOLD + "Pedidos"   + RESET + "     (CRUD + estado)");
        System.out.println("  4. " + BOLD + "Reportes"  + RESET + "    (exportar a PDF)");
        System.out.println("  0. Salir");
        System.out.println(TERRA + "════════════════════════════════════════" + RESET);
    }

    // ================================================================
    //  MENÚ PRODUCTOS (rama padre → hijos)
    // ================================================================
    private static void menuProductos() {
        boolean en = true;
        while (en) {
            System.out.println(ROSE + "\n  ┌── PRODUCTOS ─────────────────────┐" + RESET);
            System.out.println("     1. Listar todos");
            System.out.println("     2. Buscar por ID");
            System.out.println("     3. Crear producto");
            System.out.println("     4. Actualizar producto");
            System.out.println("     5. Eliminar producto");
            System.out.println("     6. Ver stock bajo (< 3)");
            System.out.println("     0. Volver");
            int op = leerInt("Elige");
            switch (op) {
                case 1 -> listarProductos();
                case 2 -> buscarProductoPorId();
                case 3 -> crearProducto();
                case 4 -> actualizarProducto();
                case 5 -> eliminarProducto();
                case 6 -> stockBajo();
                case 0 -> en = false;
                default -> System.out.println(RED + "Opción inválida." + RESET);
            }
        }
    }

    private static void listarProductos() {
        List<Producto> lista = productoRepo.findAll();
        System.out.println(TERRA + "\n  📦 Productos (" + lista.size() + "):" + RESET);
        lista.forEach(p -> System.out.println("   " + p));
    }

    private static void buscarProductoPorId() {
        int id = leerInt("ID del producto");
        Optional<Producto> opt = productoRepo.findById(id);
        opt.ifPresentOrElse(
            p -> System.out.println(GREEN + "  Encontrado: " + p + RESET),
            ()  -> System.out.println(RED   + "  Producto no encontrado." + RESET)
        );
    }

    private static void crearProducto() {
        System.out.println(ROSE + "\n  ── Nuevo Producto ──" + RESET);
        String nombre = leerTexto("Nombre");
        String desc   = leerTexto("Descripción");
        BigDecimal precio = BigDecimal.valueOf(leerDouble("Precio (COP)"));
        int stock = leerInt("Stock inicial");
        String emoji = leerTexto("Emoji (ej: 🐻)");

        // Por simplicidad, categoría 1 (amigurumis) como ejemplo
        // En producción se mostraría un listado para elegir
        Categoria cat = new Categoria();
        cat.setId(1);

        Producto p = new Producto(nombre, desc, precio, stock, emoji, cat);
        productoRepo.save(p);
        System.out.println(GREEN + "  ✅ Producto creado: " + p + RESET);
    }

    private static void actualizarProducto() {
        int id = leerInt("ID del producto a actualizar");
        Optional<Producto> opt = productoRepo.findById(id);
        if (opt.isEmpty()) { System.out.println(RED + "No encontrado." + RESET); return; }

        Producto p = opt.get();
        System.out.println("  Actual: " + p);
        System.out.print("  Nuevo nombre (ENTER para mantener): ");
        String nombre = sc.nextLine();
        if (!nombre.isBlank()) p.setNombre(nombre);

        System.out.print("  Nuevo precio (0 para mantener): ");
        double precio = leerDouble("Precio");
        if (precio > 0) p.setPrecio(BigDecimal.valueOf(precio));

        System.out.print("  Nuevo stock (-1 para mantener): ");
        int stock = leerInt("Stock");
        if (stock >= 0) p.setStock(stock);

        productoRepo.update(p);
        System.out.println(GREEN + "  ✅ Producto actualizado." + RESET);
    }

    private static void eliminarProducto() {
        int id = leerInt("ID del producto a eliminar");
        if (!productoRepo.existsById(id)) { System.out.println(RED + "No encontrado." + RESET); return; }
        System.out.print(RED + "  ¿Segura? (s/n): " + RESET);
        if ("s".equalsIgnoreCase(sc.nextLine())) {
            productoRepo.deleteById(id);
            System.out.println(GREEN + "  ✅ Producto eliminado." + RESET);
        }
    }

    private static void stockBajo() {
        List<Producto> lista = productoRepo.findByStockMenorA(3);
        System.out.println(RED + "\n  ⚠️  Productos con stock < 3 (" + lista.size() + "):" + RESET);
        lista.forEach(p -> System.out.println("   " + p));
    }

    // ================================================================
    //  MENÚ USUARIOS
    // ================================================================
    private static void menuUsuarios() {
        boolean en = true;
        while (en) {
            System.out.println(ROSE + "\n  ┌── USUARIOS ─────────────────────┐" + RESET);
            System.out.println("     1. Listar todos");
            System.out.println("     2. Buscar por ID");
            System.out.println("     3. Crear usuario");
            System.out.println("     4. Desactivar usuario");
            System.out.println("     0. Volver");
            int op = leerInt("Elige");
            switch (op) {
                case 1 -> usuarioRepo.findAll().forEach(u -> System.out.println("   " + u));
                case 2 -> {
                    int id = leerInt("ID");
                    usuarioRepo.findById(id).ifPresentOrElse(
                        u -> System.out.println(GREEN + "  " + u + RESET),
                        ()  -> System.out.println(RED + "  No encontrado." + RESET)
                    );
                }
                case 3 -> crearUsuario();
                case 4 -> {
                    int id = leerInt("ID a desactivar");
                    usuarioRepo.findById(id).ifPresent(u -> {
                        u.setActivo(false);
                        usuarioRepo.update(u);
                        System.out.println(GREEN + "  Usuario desactivado." + RESET);
                    });
                }
                case 0 -> en = false;
                default -> System.out.println(RED + "Inválido." + RESET);
            }
        }
    }

    private static void crearUsuario() {
        System.out.println(ROSE + "\n  ── Nuevo Usuario ──" + RESET);
        String nombre = leerTexto("Nombre completo");
        String email  = leerTexto("Email");
        String pass   = leerTexto("Contraseña (se guardará como hash)");
        String tel    = leerTexto("Teléfono");
        String ciudad = leerTexto("Ciudad");

        Rol rol = new Rol();
        rol.setId(2); // 'user' por defecto

        Usuario u = new Usuario(nombre, email, "$2a$10$" + pass.hashCode(), tel, ciudad, rol);
        usuarioRepo.save(u);
        System.out.println(GREEN + "  ✅ Usuario creado: " + u + RESET);
    }

    // ================================================================
    //  MENÚ PEDIDOS
    // ================================================================
    private static void menuPedidos() {
        boolean en = true;
        while (en) {
            System.out.println(ROSE + "\n  ┌── PEDIDOS ──────────────────────┐" + RESET);
            System.out.println("     1. Listar todos");
            System.out.println("     2. Ver pedidos pendientes");
            System.out.println("     3. Cambiar estado de pedido");
            System.out.println("     4. Total de ventas");
            System.out.println("     0. Volver");
            int op = leerInt("Elige");
            switch (op) {
                case 1 -> pedidoRepo.findAll().forEach(p -> System.out.println("   " + p));
                case 2 -> pedidoRepo.findByEstado(Pedido.EstadoPedido.pendiente)
                                    .forEach(p -> System.out.println("   " + p));
                case 3 -> cambiarEstadoPedido();
                case 4 -> {
                    BigDecimal total = pedidoRepo.calcularTotalVentas();
                    System.out.println(GREEN + "\n  💰 Total de ventas: $" +
                        String.format("%,.0f", total) + " COP" + RESET);
                }
                case 0 -> en = false;
                default -> System.out.println(RED + "Inválido." + RESET);
            }
        }
    }

    private static void cambiarEstadoPedido() {
        int id = leerInt("ID del pedido");
        System.out.println("  Estados: 1-pendiente  2-en_proceso  3-enviado  4-entregado  5-cancelado");
        int op = leerInt("Nuevo estado");
        Pedido.EstadoPedido[] estados = Pedido.EstadoPedido.values();
        if (op < 1 || op > estados.length) { System.out.println(RED + "Inválido." + RESET); return; }

        pedidoRepo.findById(id).ifPresentOrElse(p -> {
            p.setEstado(estados[op - 1]);
            pedidoRepo.update(p);
            System.out.println(GREEN + "  ✅ Pedido #" + id + " → " + estados[op-1] + RESET);
        }, () -> System.out.println(RED + "  Pedido no encontrado." + RESET));
    }

    // ================================================================
    //  MENÚ REPORTES PDF (dependencia adicional iText)
    // ================================================================
    private static void menuReportesPdf() {
        System.out.println(ROSE + "\n  ┌── REPORTES PDF (iText) ─────────┐" + RESET);
        System.out.println("     1. Productos con stock bajo");
        System.out.println("     2. Pedidos pendientes");
        System.out.println("     3. Catálogo completo");
        System.out.println("     0. Volver");
        int op = leerInt("Elige");
        try {
            switch (op) {
                case 1 -> pdfService.generarReporteStockBajo("reporte_stock_bajo.pdf", 3);
                case 2 -> pdfService.generarReportePedidosPendientes("reporte_pedidos_pendientes.pdf");
                case 3 -> pdfService.generarCatalogoPdf("catalogo_maho.pdf");
                case 0 -> {}
                default -> System.out.println(RED + "Inválido." + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "  Error generando PDF: " + e.getMessage() + RESET);
        }
    }

    // ================================================================
    //  HELPERS DE LECTURA
    // ================================================================
    private static int leerInt(String label) {
        System.out.print(TERRA + "  → " + label + ": " + RESET);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double leerDouble(String label) {
        System.out.print(TERRA + "  → " + label + ": " + RESET);
        try {
            return Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String leerTexto(String label) {
        System.out.print(TERRA + "  → " + label + ": " + RESET);
        return sc.nextLine().trim();
    }
}
