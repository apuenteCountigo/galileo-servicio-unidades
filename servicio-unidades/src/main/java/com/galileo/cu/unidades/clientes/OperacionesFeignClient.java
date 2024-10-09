package com.galileo.cu.unidades.clientes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.galileo.cu.commons.models.dto.OriginCascading;

@FeignClient(name="servicio-operaciones")
public interface OperacionesFeignClient {
	@DeleteMapping("/operaciones/{id}")
	void borrar(@PathVariable Long id, @RequestBody OriginCascading originCascading);
}
