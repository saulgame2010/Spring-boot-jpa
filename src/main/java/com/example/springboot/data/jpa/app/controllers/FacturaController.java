package com.example.springboot.data.jpa.app.controllers;

import com.example.springboot.data.jpa.app.models.entity.*;
import com.example.springboot.data.jpa.app.models.services.*;
import jakarta.validation.Valid;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {
    private final Logger log = LoggerFactory.getLogger(getClass());
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

    @PostMapping("/form")
    public String guardar(@Valid Factura factura, BindingResult result, Model model,
                          @RequestParam(name = "item_id[]", required = false) Long[]itemId,
                          @RequestParam(name = "cantidad[]", required = false) Integer[]cantidad,
                          RedirectAttributes flash, SessionStatus status) {
        if(result.hasErrors()) {
            model.addAttribute("titulo", "Crear Factura");
            return "factura/form";
        }
        if(itemId == null || itemId.length == 0) {
            model.addAttribute("titulo", "Crear Factura");
            model.addAttribute("error", "La factura no puede no tener líneas");
            return "factura/form";
        }
        for(int i = 0; i < itemId.length; i++) {
            Producto producto = iClienteService.findProductoById(itemId[i]);
            ItemFactura itemFactura = new ItemFactura();
            itemFactura.setCantidad(cantidad[i]);
            itemFactura.setProducto(producto);
            factura.addItem(itemFactura);
            log.info("ID: " + itemId[i].toString() + " cantidad: " + cantidad[i].toString());
        }
        iClienteService.saveFactura(factura);
        status.setComplete();
        flash.addFlashAttribute("success", "Se agregó la factura correctamente");
        return "redirect:/ver/" + factura.getCliente().getId();
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Factura factura = iClienteService.fetchByIdWithClienteWithItemFacturaWithProducto(id); //iClienteService.findFacturaById(id);
        if(factura == null) {
            flash.addFlashAttribute("error", "No existe la factura en el sistema");
            return "redirect:/listar";
        }
        model.addAttribute("factura", factura);
        model.addAttribute("titulo", "Factura: ".concat(factura.getDescripcion()));
        return "factura/ver";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Factura factura = iClienteService.findFacturaById(id);
        if(factura != null) {
            iClienteService.deleteFactura(id);
            flash.addFlashAttribute("success", "Se eliminó correctamente la factura");
            return "redirect:/ver/" + factura.getCliente().getId();
        }
        flash.addFlashAttribute("error", "La factura no existe en el sistema");
        return "redirect:/listar";
    }

    @Autowired
    public void setiClienteService(IClienteService iClienteService) {
        this.iClienteService = iClienteService;
    }
}
