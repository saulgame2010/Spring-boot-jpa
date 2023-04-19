package com.example.springboot.data.jpa.app.models.services;

import com.example.springboot.data.jpa.app.models.dao.IClientesDao;
import com.example.springboot.data.jpa.app.models.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService {
    private IClientesDao clientesDao;

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

    @Autowired
    public void setClientesDao(IClientesDao clientesDao) {
        this.clientesDao = clientesDao;
    }
}
