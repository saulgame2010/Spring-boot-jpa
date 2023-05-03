package com.example.springboot.data.jpa.app.view.pdf;

import com.example.springboot.data.jpa.app.models.entity.Factura;
import com.example.springboot.data.jpa.app.models.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import java.awt.*;
import java.util.Map;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {
    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {
        Factura factura = (Factura) model.get("factura");

        PdfPTable tablaDatosCLiente = new PdfPTable(1);
        tablaDatosCLiente.setSpacingAfter(20);

        PdfPCell cell = null;
        cell = new PdfPCell(new Phrase("Datos del cliente"));
        cell.setBackgroundColor(new Color(184, 218, 255));
        cell.setPadding(8f);
        tablaDatosCLiente.addCell(cell);

        //tablaDatosCLiente.addCell("Datos del cliente");
        tablaDatosCLiente.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
        tablaDatosCLiente.addCell(factura.getCliente().getEmail());

        PdfPTable tablaDatosFactura = new PdfPTable(1);
        tablaDatosFactura.setSpacingAfter(20);
        // tablaDatosFactura.addCell("Datos de la factura");
        cell = new PdfPCell(new Phrase("Datos de la factura"));
        cell.setBackgroundColor(new Color(195, 230, 203));
        cell.setPadding(8f);
        tablaDatosFactura.addCell(cell);

        tablaDatosFactura.addCell("Folio: " + factura.getId());
        tablaDatosFactura.addCell("Descripción: " + factura.getDescripcion());
        tablaDatosFactura.addCell("Fecha: " + factura.getCreateAt());

        PdfPTable tablaDetalleFactura = new PdfPTable(4);
        tablaDetalleFactura.setWidths(new float[] {3.5f, 1, 1, 1});
        tablaDetalleFactura.setSpacingAfter(20);
        tablaDetalleFactura.addCell("Producto");
        tablaDetalleFactura.addCell("Precio");
        tablaDetalleFactura.addCell("Cantidad");
        tablaDetalleFactura.addCell("Total");
        for(ItemFactura item : factura.getItems()) {
            tablaDetalleFactura.addCell(item.getProducto().getNombre());
            tablaDetalleFactura.addCell(item.getProducto().getPrecio().toString());
            cell = new PdfPCell(new Phrase(item.getCantidad().toString()));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            tablaDetalleFactura.addCell(cell);
            tablaDetalleFactura.addCell(item.calcularImporte().toString());
        }
        cell = new PdfPCell(new Phrase("Total: "));
        cell.setColspan(3);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tablaDetalleFactura.addCell(cell);
        tablaDetalleFactura.addCell(factura.getTotal().toString());

        document.add(tablaDatosCLiente);
        document.add(tablaDatosFactura);
        document.add(tablaDetalleFactura);
    }
}
