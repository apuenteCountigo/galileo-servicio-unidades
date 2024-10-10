package com.galileo.cu.unidades.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.galileo.cu.commons.models.Balizas;
import com.galileo.cu.commons.models.Operaciones;
import com.galileo.cu.commons.models.Unidades;

@RepositoryRestResource(exported = false)
public interface BalizasRepository extends CrudRepository<Balizas, Long> {
	@Query("SELECT b FROM Balizas b WHERE b.unidades.Id=:unidad")
	public List<Balizas> listaBalizas(long unidad);

	long countByUnidades(Unidades unidad);
}
