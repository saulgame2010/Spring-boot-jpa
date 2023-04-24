package com.example.springboot.data.jpa.app.models.services;

import com.example.springboot.data.jpa.app.models.dao.IClientesDao;
import com.example.springboot.data.jpa.app.models.dao.IFacturaDao;
import com.example.springboot.data.jpa.app.models.dao.IProductoDao;
import com.example.springboot.data.jpa.app.models.entity.Cliente;
import com.example.springboot.data.jpa.app.models.entity.Factura;
import com.example.springboot.data.jpa.app.models.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService {
    private IClientesDao clientesDao;
    private IProductoDao productoDao;
    private IFacturaDao facturaDao;

    @Transactional(readOnly = true)
    @Override
    public List<Cliente> findAll() {
        return (List<Cliente>) clientesDao.findAll();
    }

    @Transactional
    @Override
    public void save(Cliente cliente) {
        clientesDao.save(cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public Cliente findOne(Long id) {
        return clientesDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        clientesDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        return clientesDao.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findProductoByNombre(String nombre) {
        return productoDao.findByNombreLikeIgnoreCase("%" + nombre + "%");
    }

    @Override
    @Transactional
    public void saveFactura(Factura factura) {
        facturaDao.save(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findProductoById(Long id) {
        return productoDao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Factura findFacturaById(Long id) {
        return facturaDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteFactura(Long id) {
        facturaDao.deleteById(id);
    }

    @Autowired
    public void setClientesDao(IClientesDao clientesDao) {
        this.clientesDao = clientesDao;
    }

    @Autowired
    public void setProductoDao(IProductoDao productoDao) {
        this.productoDao = productoDao;
    }

    @Autowired
    public void setFacturaDao(IFacturaDao facturaDao) {
        this.facturaDao = facturaDao;
    }
}
