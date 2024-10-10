package com.galileo.cu.unidades.controlador;

import java.util.List;

import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galileo.cu.commons.models.AccionEntidad;
import com.galileo.cu.commons.models.Operaciones;
import com.galileo.cu.commons.models.TipoEntidad;
import com.galileo.cu.commons.models.Trazas;
import com.galileo.cu.commons.models.Unidades;
import com.galileo.cu.commons.models.UnidadesUsuarios;
import com.galileo.cu.commons.models.Usuarios;
import com.galileo.cu.commons.models.dto.OriginCascading;
import com.galileo.cu.unidades.repositorios.EstadosRepository;
import com.galileo.cu.unidades.clientes.OperacionesFeignClient;
import com.galileo.cu.unidades.repositorios.BalizasRepository;
import com.galileo.cu.unidades.repositorios.OperacionesRepository;
import com.galileo.cu.unidades.repositorios.TrazasRepository;
import com.galileo.cu.unidades.repositorios.UnidadesRepository;
import com.galileo.cu.unidades.repositorios.UnidadesUsuariosRepository;
import com.galileo.cu.unidades.repositorios.UsuariosRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RepositoryEventHandler(Unidades.class)
public class UnidadesEventHandler {

	@Autowired
	private OperacionesRepository operacionesrepo;

	@Autowired
	private OperacionesFeignClient operacionesFeignClient;

	@Autowired
	BalizasRepository balizasRepo;

	@Autowired
	OperacionesRepository opRepo;

	@Autowired
	EstadosRepository estadosrepo;

	@Autowired
	UnidadesUsuariosRepository uniUserRepo;

	@Autowired
	HttpServletRequest req;

	@Autowired
	TrazasRepository trazasRepo;

	@Autowired
	UnidadesRepository uniRepo;

	@Autowired
	UsuariosRepository usuRepo;

	@Autowired
	ObjectMapper objectMapper;

	String descripcionTraza;

	@HandleBeforeCreate
	public void handleUnidadesCreate(Unidades unidad) {
		System.out.println("Antes de Crear Unidades");
		/*
		 * Enumeration<String> names = req.getHeaderNames();
		 * while (names.hasMoreElements())
		 * System.out.println(names.nextElement());
		 */

		System.out.println(req.getHeader("Authorization"));

		/* Validando Autorización */
		try {
			ValidateAuthorization val = new ValidateAuthorization();
			System.out.println("REQUEST HandleBeforeCreate: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Error el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			System.out.println("Error Antes de Crear Unidad Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Error Antes de Crear Unidad Validando Autorización: " + e.getMessage());
		}
	}

	@HandleAfterCreate
	public void handleUnidadesAfterCreate(Unidades unidad) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			System.out.println("REQUEST HandleAfterCreate: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Error el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			System.out.println("Error Antes de Crear Unidad Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Error Antes de Crear Unidad Validando Autorización: " + e.getMessage());
		}

		try {
			uniRepo.crearTablaPos(unidad.getId().toString());
		} catch (Exception e) {
			System.out.println("Error Despues de Crear Unidad Ejecutando Procedimiento Almacenado: " + e.getMessage());
			// throw new RuntimeException("Error Antes de Crear Unidad Ejecutando
			// Procedimiento Almacenado: ");
		}

		try {
			System.out.println("Insertar la Creación de Unidad en la Trazabilidad AfterCreate");
			Trazas traza = new Trazas();
			AccionEntidad accion = new AccionEntidad();
			Usuarios usuario = new Usuarios();
			TipoEntidad entidad = new TipoEntidad();

			entidad.setId(5);
			accion.setId(1);
			usuario.setId(Long.parseLong(val.getJwtObjectMap().getId()));

			traza.setAccionEntidad(accion);
			traza.setTipoEntidad(entidad);
			traza.setUsuario(usuario);
			traza.setIdEntidad(unidad.getId().intValue());
			traza.setDescripcion("Fue Creada una nueva Unidad: " + unidad.getDenominacion());
			trazasRepo.save(traza);

		} catch (Exception e) {
			System.out.println("Error al Insertar la Unidad en la Trazabilidad");
			System.out.println(e.getMessage());
			throw new RuntimeException("Error al Insertar Unidad en la Trazabilidad");
		}

	}

	@HandleBeforeSave
	public void handleUnidadesBeforeSave(Unidades unidad) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			System.out.println("REQUEST HandleBeforeSave: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Error el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			System.out.println("Error Antes de Eliminar la Unidad Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Error Antes de Eliminar la Unidad Validando Autorización: " + e.getMessage());
		}
	}

	@HandleAfterSave
	public void handleUnidadesAfterSave(Unidades unidad) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			System.out.println("REQUEST HandleAfterSave: " + req.getMethod());
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Error el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			System.out.println("Error Despues de Actualizar la Unidad Validando Autorización: " + e.getMessage());
			throw new RuntimeException(
					"Error Despues de Actualizar la Unidad Validando Autorización: " + e.getMessage());
		}

		ActualizarTraza(val, unidad.getId().intValue(), 5, 3,
				"Fue Actualizada la Unidad: " + unidad.getDenominacion(),
				"Error Insertando la Actualización de la Unidad: " + unidad.getDenominacion() + " en la Trazabilidad");
	}

	@HandleBeforeDelete
	public void handleUnidadesDelete(Unidades unidad) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Fallo el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			log.error("Fallo Antes de Eliminar la Unidad Validando Autorización: ", e.getMessage());
			throw new RuntimeException("Fallo Antes de Eliminar la Unidad Validando Autorización");
		}

		long idUnidad = unidad.getId();
		String err = "Fallo, la unidad no puede ser eliminada, porque existen elementos relacionados a ella.";

		try {
			long qty = opRepo.countByUnidades(unidad);
			if (qty > 0) {
				log.error("La unidad tiene {} operaciones relacionadas.", qty);
				log.error(err);
				throw new RuntimeException(err);
			}
		} catch (Exception e) {
			if (e.getMessage().contains("Fallo")) {
				throw new RuntimeException(e.getMessage());
			}
			err = "Fallo verificando operaciones relacionadas con la unidad";
			log.error(err, e.getMessage());
			throw new RuntimeException(err);
		}

		try {
			long qty = balizasRepo.countByUnidades(unidad);
			if (qty > 0) {
				log.error("La unidad tiene {} balizas relacionadas.", qty);
				log.error(err);
				throw new RuntimeException(err);
			}
		} catch (Exception e) {
			if (e.getMessage().contains("Fallo")) {
				throw new RuntimeException(e.getMessage());
			}
			err = "Fallo verificando balizas relacionadas con la unidad";
			log.error(err, e.getMessage());
			throw new RuntimeException(err);
		}

		try {
			List<UnidadesUsuarios> uniUser = uniUserRepo.listaUnidadesUsuarios(idUnidad);
			if (uniUser.size() > 0) {
				// for (UnidadesUsuarios uniUser2 : uniUser) {
				// Usuarios usu = usuRepo.findById(uniUser2.getUsuario().getId()).get();
				// log.info("Usuario: {}, idUnidad: {}", usu.getTip(), usu.getUnidad().getId());
				// uniUserRepo.delete(uniUser2);
				// }
				log.error(err);
				throw new RuntimeException(err);
			}
		} catch (Exception e) {
			if (e.getMessage().contains("Fallo")) {
				throw new RuntimeException(e.getMessage());
			}
			log.error("Fallo al eliminar la unidad", e.getMessage());
			throw new RuntimeException("Fallo al eliminar la unidad");
		}

		try {
			uniRepo.borrarTablaPos(unidad.getId().toString());
		} catch (Exception e) {
			err = "Fallo intentando eliminar tabla de posiciones";
			log.error(err, e.getMessage());
			throw new RuntimeException(err);
		}

	}

	@HandleAfterDelete
	public void handleUnidadesAfterDelete(Unidades unidad) {
		/* Validando Autorización */
		ValidateAuthorization val = new ValidateAuthorization();
		try {
			val.setObjectMapper(objectMapper);
			val.setReq(req);
			if (!val.Validate()) {
				throw new RuntimeException("Error el Usuario Enviado no Coincide con el Autenticado ");
			}
		} catch (Exception e) {
			log.error("Error Despues de Eliminar la Unidad Validando Autorización: " + e.getMessage());
			throw new RuntimeException("Error Despues de Eliminar la Unidad Validando Autorización: " + e.getMessage());
		}

		try {
			System.out.println("Eliminar la Baliza en la Trazabilidad AfterDelete");
			Trazas traza = new Trazas();
			AccionEntidad accion = new AccionEntidad();
			Usuarios usuario = new Usuarios();
			TipoEntidad entidad = new TipoEntidad();

			entidad.setId(5);
			accion.setId(2);
			usuario.setId(Long.parseLong(val.getJwtObjectMap().getId()));

			traza.setAccionEntidad(accion);
			traza.setTipoEntidad(entidad);
			traza.setUsuario(usuario);
			traza.setIdEntidad(unidad.getId().intValue());
			traza.setDescripcion("Fue Eliminada la Unidad: " + unidad.getDenominacion());
			trazasRepo.save(traza);

		} catch (Exception e) {
			log.error("Error al Insertar la Eliminación de la Unidad en la Trazabilidad");
			log.error(e.getMessage());
			throw new RuntimeException("Error al Insertar la Eliminación de la Unidad en la Trazabilidad");
		}
	}

	private void ActualizarTraza(ValidateAuthorization val, int idEntidad, int idTipoEntidad,
			int idAccion, String trazaDescripcion, String errorMessage) {
		try {
			System.out.println("Eliminar el Objetivo en la Trazabilidad AfterDelete");
			Trazas traza = new Trazas();
			AccionEntidad accion = new AccionEntidad();
			Usuarios usuario = new Usuarios();
			TipoEntidad entidad = new TipoEntidad();

			entidad.setId(idTipoEntidad);
			accion.setId(idAccion);
			usuario.setId(Long.parseLong(val.getJwtObjectMap().getId()));

			traza.setAccionEntidad(accion);
			traza.setTipoEntidad(entidad);
			traza.setUsuario(usuario);
			traza.setIdEntidad(idEntidad);
			traza.setDescripcion(trazaDescripcion);
			trazasRepo.save(traza);
		} catch (Exception e) {
			log.error(errorMessage);
			log.error(e.getMessage());
			throw new RuntimeException(errorMessage);
		}
	}
}
