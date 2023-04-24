package com.example.springboot.data.jpa.app.models.dao;

import com.example.springboot.data.jpa.app.models.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFacturaDao extends JpaRepository<Factura, Long> {
}
