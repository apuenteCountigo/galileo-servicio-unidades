package com.galileo.cu.unidades.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.galileo.cu.commons.models.UnidadesUsuarios;

@RepositoryRestResource(exported = false)
public interface UnidadesUsuariosRepository extends CrudRepository<UnidadesUsuarios, Long> {
	@Query("SELECT u FROM UnidadesUsuarios u WHERE u.unidad.Id=:unidad")
	public List<UnidadesUsuarios> listaUnidadesUsuarios(long unidad);
}
