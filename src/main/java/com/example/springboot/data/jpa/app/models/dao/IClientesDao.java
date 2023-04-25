package com.example.springboot.data.jpa.app.models.dao;

import com.example.springboot.data.jpa.app.models.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IClientesDao extends JpaRepository<Cliente, Long> {
    // Aquí puede haber métodos personalizados
    @Query("select c from Cliente c left join fetch c.facturas f where c.id = ?1")
    Cliente fetchByIdWithFacturas(Long id);
}
