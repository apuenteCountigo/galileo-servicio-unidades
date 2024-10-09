package com.galileo.cu.unidades.repositorios;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.galileo.cu.commons.models.Usuarios;

@RepositoryRestResource(exported = false)
public interface UsuariosRepository extends CrudRepository<Usuarios, Long> {

}
