package com.example.springboot.data.jpa.app.controllers;

import com.example.springboot.data.jpa.app.models.entity.Cliente;
import com.example.springboot.data.jpa.app.models.services.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {
    private IClienteService iClienteService;
    @GetMapping("/listar")
    public List<Cliente> listarRest() {
        return iClienteService.findAll();
    }

    @Autowired
    public void setiClienteService(IClienteService iClienteService) {
        this.iClienteService = iClienteService;
    }
}
