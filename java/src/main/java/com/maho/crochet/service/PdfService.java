package com.maho.crochet.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.maho.crochet.entity.Pedido;
import com.maho.crochet.entity.Producto;
import com.maho.crochet.repository.PedidoRepository;
import com.maho.crochet.repository.ProductoRepository;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PdfService — Integración de la dependencia adicional iText.
 *
 * Genera reportes PDF reales usando datos de la base de datos:
 *  1. Reporte de productos con stock bajo
 *  2. Reporte de pedidos pendientes
 *  3. Catálogo completo de productos
 *
 * Cumple el requisito: "la dependencia adicional debe tener
 * un caso de uso real que interactúe con los datos de la BD."
 */
public class PdfService {

    // Colores de la paleta Crocheterias Maho
    private static final BaseColor COLOR_TERRA    = new BaseColor(139, 94, 60);
    private static final BaseColor COLOR_ROSE     = new BaseColor(194, 116, 138);
    private static final BaseColor COLOR_LIGHT    = new BaseColor(245, 230, 211);
    private static final BaseColor COLOR_DARK     = new BaseColor(74, 44, 20);
    private static final BaseColor COLOR_WHITE    = BaseColor.WHITE;

    private final ProductoRepository productoRepo = new ProductoRepository();
    private final PedidoRepository pedidoRepo     = new PedidoRepository();

    // ── Fuentes ──────────────────────────────────────────────
    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD,   BaseColor.WHITE);
    private static final Font FONT_H2    = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD,   COLOR_DARK);
    private static final Font FONT_BODY  = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_DARK);
    private static final Font FONT_BOLD  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   COLOR_DARK);
    private static final Font FONT_TH    = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   BaseColor.WHITE);
    private static final Font FONT_SMALL = new Font(Font.FontFamily.HELVETICA,  9, Font.ITALIC, BaseColor.GRAY);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ================================================================
    //  1. REPORTE: Productos con stock bajo
    // ================================================================
    public void generarReporteStockBajo(String rutaSalida, int umbral) throws Exception {
        List<Producto> productos = productoRepo.findByStockMenorA(umbral);

        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
        doc.open();

        agregarEncabezado(doc, "Reporte: Stock Bajo");
        agregarParrafo(doc, "Generado: " + LocalDateTime.now().format(FMT), FONT_SMALL);
        agregarParrafo(doc, "Productos con menos de " + umbral + " unidades en stock.", FONT_BODY);
        doc.add(Chunk.NEWLINE);

        if (productos.isEmpty()) {
            agregarParrafo(doc, "No hay productos con stock bajo en este momento.", FONT_BODY);
        } else {
            PdfPTable tabla = crearTabla(new float[]{3f, 2f, 1.5f, 2f});
            agregarEncabezadoTabla(tabla, "Producto", "Categoría", "Stock", "Precio");

            for (Producto p : productos) {
                tabla.addCell(celdaDatos(p.getNombre(), FONT_BODY));
                tabla.addCell(celdaDatos(p.getCategoria().getNombre(), FONT_BODY));

                // Resaltar en rojo si stock es 0
                Font fStock = p.getStock() == 0
                    ? new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.RED)
                    : FONT_BOLD;
                tabla.addCell(celdaDatos(String.valueOf(p.getStock()), fStock));
                tabla.addCell(celdaDatos(formatPrecio(p.getPrecio()), FONT_BODY));
            }
            doc.add(tabla);
        }

        agregarPie(doc);
        doc.close();
        System.out.println("[PDF] Reporte stock bajo generado: " + rutaSalida);
    }

    // ================================================================
    //  2. REPORTE: Pedidos pendientes
    // ================================================================
    public void generarReportePedidosPendientes(String rutaSalida) throws Exception {
        List<Pedido> pedidos = pedidoRepo.findByEstado(Pedido.EstadoPedido.pendiente);

        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
        doc.open();

        agregarEncabezado(doc, "Pedidos Pendientes");
        agregarParrafo(doc, "Generado: " + LocalDateTime.now().format(FMT), FONT_SMALL);
        doc.add(Chunk.NEWLINE);

        if (pedidos.isEmpty()) {
            agregarParrafo(doc, "No hay pedidos pendientes en este momento. 🎉", FONT_BODY);
        } else {
            agregarParrafo(doc, "Total de pedidos pendientes: " + pedidos.size(), FONT_BOLD);
            doc.add(Chunk.NEWLINE);

            PdfPTable tabla = crearTabla(new float[]{1f, 2.5f, 2f, 1.5f, 2.5f});
            agregarEncabezadoTabla(tabla, "ID", "Cliente", "Total", "Estado", "Fecha");

            for (Pedido p : pedidos) {
                tabla.addCell(celdaDatos(String.valueOf(p.getId()), FONT_BODY));
                tabla.addCell(celdaDatos(p.getUsuario().getNombre(), FONT_BODY));
                tabla.addCell(celdaDatos(formatPrecio(p.getTotal()), FONT_BOLD));
                tabla.addCell(celdaDatos(p.getEstado().name(), FONT_BODY));
                tabla.addCell(celdaDatos(p.getFechaPedido().format(FMT), FONT_SMALL));
            }
            doc.add(tabla);
        }

        agregarPie(doc);
        doc.close();
        System.out.println("[PDF] Reporte pedidos pendientes generado: " + rutaSalida);
    }

    // ================================================================
    //  3. CATÁLOGO DE PRODUCTOS
    // ================================================================
    public void generarCatalogoPdf(String rutaSalida) throws Exception {
        List<Producto> productos = productoRepo.findAll();

        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(doc, new FileOutputStream(rutaSalida));
        doc.open();

        agregarEncabezado(doc, "Catálogo de Productos");
        agregarParrafo(doc, "Crocheterias Maho | " + LocalDateTime.now().format(FMT), FONT_SMALL);
        doc.add(Chunk.NEWLINE);

        PdfPTable tabla = crearTabla(new float[]{3f, 2f, 1.5f, 1.5f, 1f});
        agregarEncabezadoTabla(tabla, "Producto", "Descripción", "Precio", "Stock", "Disponible");

        for (Producto p : productos) {
            tabla.addCell(celdaDatos(p.getEmoji() + " " + p.getNombre(), FONT_BOLD));
            String desc = p.getDescripcion();
            if (desc != null && desc.length() > 50) desc = desc.substring(0, 47) + "...";
            tabla.addCell(celdaDatos(desc != null ? desc : "—", FONT_SMALL));
            tabla.addCell(celdaDatos(formatPrecio(p.getPrecio()), FONT_BODY));
            tabla.addCell(celdaDatos(String.valueOf(p.getStock()), FONT_BODY));
            tabla.addCell(celdaDatos(Boolean.TRUE.equals(p.getDisponible()) ? "Sí" : "No", FONT_BODY));
        }
        doc.add(tabla);

        // Resumen
        doc.add(Chunk.NEWLINE);
        Paragraph resumen = new Paragraph("Total de productos: " + productos.size(), FONT_BOLD);
        resumen.setAlignment(Element.ALIGN_RIGHT);
        doc.add(resumen);

        agregarPie(doc);
        doc.close();
        System.out.println("[PDF] Catálogo generado: " + rutaSalida);
    }

    // ================================================================
    //  HELPERS PRIVADOS
    // ================================================================
    private void agregarEncabezado(Document doc, String titulo) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_TERRA);
        cell.setPadding(16);
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph p = new Paragraph("🧶 Crocheterias Maho", FONT_TITLE);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        Paragraph sub = new Paragraph(titulo,
            new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, COLOR_LIGHT));
        sub.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(sub);
        header.addCell(cell);
        doc.add(header);
        doc.add(Chunk.NEWLINE);
    }

    private void agregarParrafo(Document doc, String texto, Font fuente) throws DocumentException {
        Paragraph p = new Paragraph(texto, fuente);
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private void agregarPie(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph pie = new Paragraph(
            "Crocheterias Maho · Bello, Antioquia · Proyecto CESDE 2025",
            new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY)
        );
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);
    }

    private PdfPTable crearTabla(float[] anchos) throws DocumentException {
        PdfPTable tabla = new PdfPTable(anchos.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(anchos);
        tabla.setSpacingBefore(6f);
        return tabla;
    }

    private void agregarEncabezadoTabla(PdfPTable tabla, String... columnas) {
        for (String col : columnas) {
            PdfPCell cell = new PdfPCell(new Phrase(col, FONT_TH));
            cell.setBackgroundColor(COLOR_TERRA);
            cell.setPadding(8);
            cell.setBorderColor(COLOR_LIGHT);
            tabla.addCell(cell);
        }
    }

    private PdfPCell celdaDatos(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto != null ? texto : "—", fuente));
        cell.setPadding(6);
        cell.setBorderColor(COLOR_LIGHT);
        return cell;
    }

    private String formatPrecio(BigDecimal precio) {
        if (precio == null) return "$0";
        return String.format("$%,.0f", precio);
    }
}
