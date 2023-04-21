package com.example.springboot.data.jpa.app.controllers;

import com.example.springboot.data.jpa.app.models.entity.*;
import com.example.springboot.data.jpa.app.models.services.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {
    private IClienteService iClienteService;
    @GetMapping("/form/{clienteId}")
    public String crear(@PathVariable(name = "clienteId") Long clienteId, Map<String, Object> model, RedirectAttributes flash) {
        Cliente cliente = iClienteService.findOne(clienteId);
        if(cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en el sistema");
            return "redirect:/listar";
        }
        Factura factura = new Factura();
        factura.setCliente(cliente);
        model.put("factura", factura);
        model.put("titulo", "Crear factura");
        return "factura/form";
    }

    @GetMapping(value = "/cargar-productos/{term}", produces = {"application/json"})
    public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
        return iClienteService.findProductoByNombre(term);
    }

    @Autowired
    public void setiClienteService(IClienteService iClienteService) {
        this.iClienteService = iClienteService;
    }
}
