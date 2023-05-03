package com.example.springboot.data.jpa.app.view.xlsx;

import com.example.springboot.data.jpa.app.models.entity.Factura;
import com.example.springboot.data.jpa.app.models.entity.ItemFactura;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.util.Map;

@Component("factura/ver.xlsx")
public class FacturaXlsxView extends AbstractXlsxView {
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        Factura factura = (Factura) model.get("factura");
        Sheet sheet = workbook.createSheet("Factura Spring");

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Datos del cliente");

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getEmail());

        sheet.createRow(4).createCell(0).setCellValue("Datos de la factura");
        sheet.createRow(5).createCell(0).setCellValue("Folio: " + factura.getId());
        sheet.createRow(6).createCell(0).setCellValue("Descripción: " + factura.getDescripcion());
        sheet.createRow(7).createCell(0).setCellValue("Fecha: " + factura.getCreateAt());

        Row header = sheet.createRow(9);
        header.createCell(0).setCellValue("Producto");
        header.createCell(1).setCellValue("Precio");
        header.createCell(2).setCellValue("Cantidad");
        header.createCell(3).setCellValue("Total");
        int numRow = 10;
        for(ItemFactura item : factura.getItems()) {
            Row rowItem = sheet.createRow(numRow++);
            rowItem.createCell(0).setCellValue(item.getProducto().getNombre());
            rowItem.createCell(1).setCellValue(item.getProducto().getPrecio());
            rowItem.createCell(2).setCellValue(item.getCantidad());
            rowItem.createCell(3).setCellValue(item.calcularImporte());
        }
        Row filaTotal = sheet.createRow(numRow);
        filaTotal.createCell(2).setCellValue("Gran total");
        filaTotal.createCell(3).setCellValue(factura.getTotal());
    }
}
