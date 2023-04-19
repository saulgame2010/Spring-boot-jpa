package com.example.springboot.data.jpa.app.controllers;

import com.example.springboot.data.jpa.app.controllers.util.paginator.PageRender;
import com.example.springboot.data.jpa.app.models.entity.Cliente;
import com.example.springboot.data.jpa.app.models.services.IClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
    private IClienteService iClienteDao;

    @GetMapping("/listar")
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Pageable pageableRequest = PageRequest.of(page, 4);
        Page<Cliente> clientes = iClienteDao.findAll(pageableRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }

    @GetMapping("/form")
    public String formCrear(Model model) {
        Cliente cliente = new Cliente();
        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Formulario de cliente");
        return "form";
    }

    @PostMapping("/form")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {
        if(result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de cliente");
            return "form";
        }
        String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito" : "Cliente creado con éxito";
        iClienteDao.save(cliente);
        status.setComplete();
        flash.addFlashAttribute("success", mensajeFlash);
        return "redirect:/listar";
    }

    @GetMapping("/form/{id}")
    public String edit(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        Cliente cliente = null;
        if(id > 0) {
            cliente = iClienteDao.findOne(id);
            if(cliente == null) {
                flash.addFlashAttribute("error", "El cliente no existe en la Base de datos");
                return "redirect:/listar";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del cliente no puede ser 0");
            return "redirect:/listar";
        }
        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Editar cliente");
        return "form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        if(id > 0) {
            iClienteDao.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito");
        } else {
            return "redirect:/listar";
        }
        return "redirect:/listar";
    }

    @Autowired
    public void setiClienteDao(IClienteService iClienteDao) {
        this.iClienteDao = iClienteDao;
    }
}
