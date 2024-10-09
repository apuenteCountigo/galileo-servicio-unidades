package com.galileo.cu.unidades.repositorios;

import org.springframework.data.repository.CrudRepository;

import com.galileo.cu.commons.models.Estados;

public interface EstadosRepository extends CrudRepository<Estados, Long>{
	Estados findByDescripcion(String Descripcion);
}
