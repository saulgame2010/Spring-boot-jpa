package com.example.springboot.data.jpa.app.models.services;

import com.example.springboot.data.jpa.app.models.dao.IUsuarioDao;
import com.example.springboot.data.jpa.app.models.entity.Role;
import com.example.springboot.data.jpa.app.models.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {
    private IUsuarioDao usuarioDao;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByUsername(username);
        if (usuario == null) {
            logger.error("Error login: no existe el usuario: " + username);
            throw new UsernameNotFoundException("El usuario no existe");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        for(Role role : usuario.getRoles()) {
            roles.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        if (roles.isEmpty()) {
            logger.error("Error login: el usuario " + username + " no tiene roles asignados");
            throw new UsernameNotFoundException("El usuario no tiene roles asignados");
        }
        return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, roles);
    }

    @Autowired
    public void setUsuarioDao(IUsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }
}
