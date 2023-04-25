package com.example.springboot.data.jpa.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class LoginController {
    @GetMapping("/login")
    // El objeto Principal se encarga de ver si el usuario ya está logueado
    public String login(Model model, Principal principal, RedirectAttributes flash) {
        if(principal != null) {
            flash.addFlashAttribute("info", "Ya ha iniciado sesión");
            return "redirect:/";
        }
        return "login";
    }
}
