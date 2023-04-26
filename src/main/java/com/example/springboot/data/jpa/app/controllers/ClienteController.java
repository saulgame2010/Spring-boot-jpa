package com.example.springboot.data.jpa.app.controllers;

import com.example.springboot.data.jpa.app.controllers.util.paginator.PageRender;
import com.example.springboot.data.jpa.app.models.entity.Cliente;
import com.example.springboot.data.jpa.app.models.services.IClienteService;
import com.example.springboot.data.jpa.app.models.services.IUploadFileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
    private IClienteService iClienteService;
    private IUploadFileService iUploadFileService;
    protected  final Logger logger = LoggerFactory.getLogger(getClass());

    // La expresión regular ':.+' hace que Spring no trunque la extensión del archivo, ya que por defecto la manda alv
    @Secured("ROLE_USER")
    @GetMapping("/uploads/{filename:.+}")
    // El Resource indica que vamos a responder con un recurso, en este caso, la imagen
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
        Resource recurso = null;
        try {
            recurso = iUploadFileService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    @Secured("ROLE_USER")
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        Cliente cliente = iClienteService.fetchByIdWithFacturas(id); //iClienteService.findOne(id);
        if(cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en el sistema");
            return "redirect:/listar";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Detalle del cliente: " + cliente.getNombre());
        return "ver";
    }

    @GetMapping({"/listar", "/"})
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
                         Authentication authentication, HttpServletRequest httpServletRequest) {
        if(authentication != null) {
            logger.info("El usuario '" + authentication.getName() + "' ha iniciado sesión con éxito desde el controlador");
            if(hasRole("ROLE_ADMIN")) {
                logger.info("Hola ".concat(authentication.getName()).concat(" eres el Lord"));
            } else {
                logger.info("Hola ".concat(authentication.getName()).concat(" no eres el Señor"));
            }
        }
        SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(httpServletRequest, "ROLE_");
        if(securityContext.isUserInRole("ADMIN")) {
            logger.info("Tienes acceso perro admin");
        } else {
            logger.info("No tienes acceso de admin");
        }
        Pageable pageableRequest = PageRequest.of(page, 4);
        Page<Cliente> clientes = iClienteService.findAll(pageableRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/form")
    public String formCrear(Model model) {
        Cliente cliente = new Cliente();
        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Formulario de cliente");
        return "form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/form")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam(name = "file")MultipartFile foto,
                          RedirectAttributes flash, SessionStatus status) {
        if(result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de cliente");
            return "form";
        }
        if(!foto.isEmpty()) {
            if(cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null && cliente.getFoto().length() > 0) {
                iUploadFileService.delete(cliente.getFoto());
            }
            String uniqueFilename = null;
            try {
                uniqueFilename = iUploadFileService.copy(foto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            flash.addFlashAttribute("info", "Has subido correctamente: '" + uniqueFilename + "'");
            cliente.setFoto(uniqueFilename);
        }
        String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito" : "Cliente creado con éxito";
        iClienteService.    save(cliente);
        status.setComplete();
        flash.addFlashAttribute("success", mensajeFlash);
        return "redirect:/listar";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/form/{id}")
    public String edit(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        Cliente cliente = null;
        if(id > 0) {
            cliente = iClienteService.findOne(id);
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

    @Secured("ROLE_ADMIN")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        if(id > 0) {
            Cliente cliente = iClienteService.findOne(id);
            iClienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito");
            if(iUploadFileService.delete(cliente.getFoto())) {
                flash.addFlashAttribute("info", "Se eliminó la foto: " + cliente.getFoto() + " con éxito");
            }
        } else {
            return "redirect:/listar";
        }
        return "redirect:/listar";
    }

    private boolean hasRole(String rol) {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context == null) {
            return false;
        }
        Authentication authentication = context.getAuthentication();
        if(authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for(GrantedAuthority authority : authorities) {
            if(rol.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    @Autowired
    public void setiClienteService(IClienteService iClienteService) {
        this.iClienteService = iClienteService;
    }

    @Autowired
    public void setiUploadFileService(IUploadFileService iUploadFileService) {
        this.iUploadFileService = iUploadFileService;
    }
}
