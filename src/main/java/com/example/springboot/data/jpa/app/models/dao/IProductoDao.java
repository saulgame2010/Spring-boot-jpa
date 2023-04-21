package com.example.springboot.data.jpa.app.models.dao;

import com.example.springboot.data.jpa.app.models.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductoDao extends JpaRepository<Producto, Long> {
    @Query("SELECT p FROM Producto p WHERE p.nombre like %?1%")
    List<Producto> findProductoByNombre(String term);

    List<Producto> findByNombreLikeIgnoreCase(String term);
}
