package com.galileo.cu.unidades.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.galileo.cu.commons.models.Operaciones;
import com.galileo.cu.commons.models.Unidades;

@RepositoryRestResource(exported = false)
public interface OperacionesRepository extends CrudRepository<Operaciones, Long> {
	@Query("SELECT o FROM Operaciones o WHERE o.unidades.Id=:unidad")
	public List<Operaciones> listaOperaciones(long unidad);

	long countByUnidades(Unidades unidad);
}
