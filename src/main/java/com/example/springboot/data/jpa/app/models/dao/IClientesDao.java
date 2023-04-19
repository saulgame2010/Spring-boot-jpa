package com.example.springboot.data.jpa.app.models.dao;

import com.example.springboot.data.jpa.app.models.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClientesDao extends JpaRepository<Cliente, Long> {
    // Aquí puede haber métodos personalizados
}
