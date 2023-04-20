package com.example.springboot.data.jpa.app.controllers;

import com.example.springboot.data.jpa.app.controllers.util.paginator.PageRender;
import com.example.springboot.data.jpa.app.models.entity.Cliente;
import com.example.springboot.data.jpa.app.models.services.IClienteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
    private IClienteService iClienteDao;
    private Logger log = LoggerFactory.getLogger(getClass());

    // La expresión regular ':.+' hace que Spring no trunque la extensión del archivo, ya que por defecto la manda alv
    @GetMapping("/uploads/{filename:.+}")
    // El Resource indica que vamos a responder con un recurso, en este caso, la imagen
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
        Path pathFoto = Paths.get("uploads").resolve(filename).toAbsolutePath();
        log.info("Path foto: " + pathFoto);
        Resource recurso = null;
        try {
            recurso = new UrlResource(pathFoto.toUri());
            if(!recurso.exists() && !recurso.isReadable()) {
                throw new RuntimeException("Error: No se puede cargar la imagen: " + pathFoto);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
        Cliente cliente = iClienteDao.findOne(id);
        if(cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en el sistema");
            return "redirect:/listar";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Detalle del cliente: " + cliente.getNombre());
        return "ver";
    }

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
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam(name = "file")MultipartFile foto,
                          RedirectAttributes flash, SessionStatus status) {
        if(result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de cliente");
            return "form";
        }
        if(!foto.isEmpty()) {
            String uniqueFilename = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
            Path rootPath = Paths.get("uploads").resolve(Objects.requireNonNull(uniqueFilename));
            Path absolutePath = rootPath.toAbsolutePath();
            log.info("RoothPath: " + rootPath);
            log.info("RoothAbsolutePath: " + absolutePath);
            try {
                Files.copy(foto.getInputStream(), absolutePath);
                flash.addFlashAttribute("info", "Has subido correctamente: '" + uniqueFilename + "'");
                cliente.setFoto(uniqueFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
