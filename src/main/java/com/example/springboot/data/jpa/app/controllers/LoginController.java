package com.example.springboot.data.jpa.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class LoginController {
    @GetMapping("/login")
    // El objeto Principal se encarga de ver si el usuario ya está logueado
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
            Model model, Principal principal, RedirectAttributes flash) {
        if(principal != null) {
            flash.addFlashAttribute("info", "Ya ha iniciado sesión");
            return "redirect:/";
        }
        if(error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrecto, vuelve a intentar");
        }
        if(logout != null) {
            model.addAttribute("success", "Se ha cerrado la sesión");
        }
        return "login";
    }
}
