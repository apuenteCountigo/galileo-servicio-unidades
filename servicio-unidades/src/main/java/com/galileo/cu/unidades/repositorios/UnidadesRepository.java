package com.galileo.cu.unidades.repositorios;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import com.galileo.cu.commons.models.Unidades;
import com.galileo.cu.commons.models.dto.UnidadesResumenDTO;

@RepositoryRestResource(collectionResourceRel = "unidades", path = "unidades")
public interface UnidadesRepository extends PagingAndSortingRepository<Unidades, Long> {

	/*
	 * @Query("SELECT u FROM Unidades u WHERE "
	 * +
	 * "((:fechaFin!=null and :fechaInicio!=null and u.fechaCreacion between :fechaInicio and :fechaFin) "
	 * +
	 * "or (:fechaFin=null and :fechaInicio!=null and u.fechaCreacion >=:fechaInicio) "
	 * + "or (:fechaFin=null and :fechaInicio=null)) "
	 * + "AND (:denominacion='' or u.denominacion like %:denominacion%) "
	 * + "AND (:responsable='' or u.responsable like %:responsable%) "
	 * +
	 * "AND (:provinciaDescripcion='' or u.provincia.descripcion like %:provinciaDescripcion%) "
	 * + "AND (:provinciaId=0 or u.provincia.Id=:provinciaId) "
	 * + "AND (:localidad='' or u.localidad like %:localidad%) ")
	 * public Page<Unidades> filtrar1(String denominacion, String responsable,
	 * String provinciaDescripcion,
	 * int provinciaId, String localidad,
	 * 
	 * @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime
	 * fechaInicio,
	 * 
	 * @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
	 * Pageable p);
	 */

	@Query("SELECT un FROM UnidadesUsuarios uu JOIN uu.unidad un WHERE uu.usuario.id = :idusuario "
			+ "AND (:denominacion='' or un.denominacion like %:denominacion%)")
	public Page<Unidades> asignadas(long idusuario, String denominacion, Pageable p);

	@Query("SELECT un FROM UnidadesUsuarios uu JOIN uu.unidad un JOIN uu.usuario u WHERE ((u.perfil.id > 1 AND uu.usuario.id = :idAuth) OR (u.id != :idAuth AND :idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id=1))) "
			+ "AND ((:fechaFin!=null and :fechaInicio!=null and u.fechaCreacion between :fechaInicio and :fechaFin) "
			+ "OR (:fechaFin=null and :fechaInicio!=null and u.fechaCreacion >=:fechaInicio) "
			+ "OR (:fechaFin=null and :fechaInicio=null)) "
			+ "AND (:denominacion='' or un.denominacion like %:denominacion%) "
			+ "AND (:responsable='' or un.responsable like %:responsable%) "
			+ "AND (:provinciaDescripcion='' or un.provincia.descripcion like %:provinciaDescripcion%) "
			+ "AND (:provinciaId=0 or un.provincia.Id=:provinciaId) "
			+ "AND (:localidad='' or un.localidad like %:localidad%) ")
	public Page<Unidades> filtrar(int idAuth, String denominacion, String responsable, String provinciaDescripcion,
			int provinciaId, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, String localidad, Pageable p);

	@Query("SELECT un FROM Unidades un WHERE "
			+ "( "
			+ "	("
			+ "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id=1)) "
			+ "		AND un.id NOT IN (SELECT uu.unidad.id FROM UnidadesUsuarios uu WHERE uu.usuario.id=:idUsuario) "
			+ "	)"
			+ "	OR ( "
			+ "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id = 2)) "
			+ "		AND (un.id=(SELECT uu.unidad.id FROM UnidadesUsuarios uu WHERE uu.usuario.id=:idAuth AND uu.estado.id=6) AND un.id NOT IN (SELECT uu.unidad.id FROM UnidadesUsuarios uu WHERE uu.usuario.id=:idUsuario)) "
			+ "		) "
			+ ") "
			+ "AND (:denominacion='' or un.denominacion like %:denominacion%)")
	public Page<Unidades> sinasignar(long idAuth, long idUsuario, String denominacion, Pageable p);

	@Query("SELECT un FROM Unidades un WHERE "
			+ "( "
			+ "	(:idAuth IN (SELECT id FROM Usuarios WHERE perfil.id=1)) "
			+ "	OR ( "
			+ "		(:idAuth IN (SELECT id FROM Usuarios WHERE perfil.id > 1)) "
			+ "        AND (un.id IN (SELECT uu.unidad.id FROM UnidadesUsuarios uu WHERE uu.usuario.id=:idAuth AND (uu.expira IS NULL OR uu.expira > CURRENT_DATE))) "
			+ "		) "
			+ ") "
			+ "AND (:denominacion='' or un.denominacion like %:denominacion%) "
			+ "AND (:responsable='' or un.responsable like %:responsable%) "
			+ "AND (un.provincia is null OR :provinciaId=0 OR un.provincia.Id=:provinciaId) "
			+ "AND (:localidad='' or un.localidad like %:localidad%) ")
	Page<Unidades> filtro_gestion_unidad(long idAuth, int provinciaId, String denominacion, String responsable,
			String localidad, Pageable p);

	Unidades findFirstByDenominacion(String descripcion);

	@Query("SELECT new com.galileo.cu.commons.models.dto.UnidadesResumenDTO("
			+ "(SELECT COUNT(*) FROM Operaciones o WHERE o.unidades.id=:idunidad "
			+ " AND ( "
			+ "	(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id=1)) "
			+ "	OR ( "
			+ "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id=2)) "
			+ "		AND ( "
			+ "				(o.unidades.id=(SELECT un.unidad.id FROM UnidadesUsuarios un WHERE un.usuario.id=:idAuth AND un.estado.id=6)) "
			+ "			OR "
			+ "				(o.id IN (SELECT p.idEntidad FROM Permisos p WHERE p.tipoEntidad.id=6 AND p.usuarios.id=:idAuth) ) "
			+ "			OR "
			+ "				(o.id IN (SELECT ob.operaciones.id FROM Objetivos ob WHERE ob.id IN (SELECT p.idEntidad FROM Permisos p WHERE p.tipoEntidad.id=8 AND p.usuarios.id=:idAuth)) ) "
			+ "			)"
			+ "		) "
			+ " OR ("
			+ "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id>2)) "
			+ "		AND ("
			+ "			o.id IN (SELECT p.idEntidad FROM Permisos p WHERE p.tipoEntidad.id=6 AND p.usuarios.id=:idAuth) "
			+ "			OR "
			+ "			o.id IN ("
			+ "				SELECT ob.operaciones.id FROM Objetivos ob WHERE ob.id IN "
			+ "					(SELECT p.idEntidad FROM Permisos p WHERE p.tipoEntidad.id=8 AND p.usuarios.id=:idAuth)"
			+ "				) "
			+ "			) "
			+ " 	) "
			+ ")), "
			+ "(SELECT COUNT(*) FROM Objetivos b WHERE b.operaciones.unidades.id=:idunidad), "
			+ "(SELECT COUNT(*) FROM UnidadesUsuarios uu WHERE uu.unidad.id=:idunidad), "
			+ "(SELECT COUNT(*) FROM Balizas bz WHERE bz.unidades.id=:idunidad ) "
			+ ") FROM Unidades u WHERE u.id=:idunidad")
	public UnidadesResumenDTO resumen(long idAuth, long idunidad);

	/*
	 * @Query("SELECT new com.galileo.cu.commons.models.dto.UnidadesResumenDTO( "
	 * + "(SELECT COUNT(*) FROM Operaciones o WHERE o.unidades.id=:idunidad "
	 * + " AND ("
	 * + "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id>2)) "
	 * + "		AND ("
	 * +
	 * "			o.id IN (SELECT idEntidad FROM Permisos WHERE tipoEntidad.id=6 AND usuarios.id=:idAuth) "
	 * + "			OR "
	 * + "			o.id=(SELECT ob.operaciones.id FROM Objetivos ob WHERE "
	 * +
	 * "					ob.id IN (SELECT p.idEntidad FROM Permisos p WHERE p.tipoEntidad.id=8 AND p.usuarios.id=:idAuth))"
	 * + "			) "
	 * + " 	) "
	 * + "), "
	 * +
	 * "(SELECT COUNT(*) FROM Objetivos b WHERE b.operaciones.unidades.id=:idunidad "
	 * + " AND ("
	 * + "		(:idAuth IN (SELECT up FROM Usuarios up WHERE up.perfil.id>2)) "
	 * + "		AND ("
	 * +
	 * "			o.id IN (SELECT idEntidad FROM Permisos WHERE tipoEntidad.id=8 AND usuarios.id=:idAuth) "
	 * + "			OR "
	 * +
	 * "			o.operaciones.id IN (SELECT p.idEntidad FROM Permisos p WHERE p.tipoEntidad.id=6 AND p.usuarios.id=:idAuth)"
	 * + "			) "
	 * + " 	) "
	 * + "), "
	 * + "(SELECT COUNT(*) FROM UnidadesUsuarios uu WHERE uu.unidad.id=:idunidad), "
	 * + "(SELECT COUNT(*) FROM Balizas bz WHERE bz.unidades.id=:idunidad "
	 * + "		AND (bz.estados.descripcion='Averiada' "
	 * + "		OR bz.estados.descripcion='Perdida' "
	 * + "		OR bz.estados.descripcion='Baja' "
	 * + "		OR bz.estados.descripcion='Disponible' "
	 * + "		OR bz.estados.descripcion='En Instalación')"
	 * + ")) FROM Unidades u WHERE u.id=:idunidad")
	 * public UnidadesResumenDTO resumen(long idunidad);
	 */

	@RestResource(exported = false)
	@Query(value = "CALL crearTablaPos(:idUnidad)", nativeQuery = true)
	void crearTablaPos(String idUnidad);

	@RestResource(exported = false)
	@Query(value = "CALL borrarTablaPos(:idUnidad)", nativeQuery = true)
	void borrarTablaPos(String idUnidad);

	/*
	 * @Query("SELECT un FROM Unidades un WHERE " + "(:perfil=2 "
	 * +
	 * "AND un.id NOT IN (SELECT DISTINCT u FROM UnidadesUsuarios uu LEFT JOIN uu.unidad u WHERE uu.usuario.id = :idUsuario) "
	 * + ") or (:perfil=1 "
	 * +
	 * "AND un.id NOT IN (SELECT DISTINCT u FROM UnidadesUsuarios uu LEFT JOIN uu.unidad u) "
	 * + " )"
	 * + "AND (:denominacion='' or un.denominacion like %:denominacion%) "
	 * + "AND (:responsable='' or un.responsable like %:responsable%) "
	 * +
	 * "AND (:provinciaDescripcion='' or un.provincia.descripcion like %:provinciaDescripcion%) "
	 * + "AND (:provinciaId=0 or un.provincia.Id=:provinciaId) "
	 * + "AND (:localidad='' or un.localidad like %:localidad%) ")
	 * Page<Unidades> filtro_sinasignar(int perfil, long idUsuario, String
	 * denominacion, String responsable,
	 * String provinciaDescripcion, int provinciaId, String localidad, Pageable p);
	 * 
	 * @Query("SELECT distinct un FROM UnidadesUsuarios uu JOIN uu.unidad un JOIN uu.usuario u WHERE "
	 * + "(:perfil=2 "
	 * + "AND uu.usuario.id = :idUsuario " + ") or (:perfil=1 " + ") "
	 * + "AND (:denominacion='' or un.denominacion like %:denominacion%) "
	 * + "AND (:responsable='' or un.responsable like %:responsable%) "
	 * +
	 * "AND (:provinciaDescripcion='' or un.provincia.descripcion like %:provinciaDescripcion%) "
	 * + "AND (:provinciaId=0 or un.provincia.Id=:provinciaId) "
	 * + "AND (:localidad='' or un.localidad like %:localidad%) ")
	 * public Page<Unidades> filtro_asignadas(int perfil, long idUsuario, String
	 * denominacion, String responsable,
	 * String provinciaDescripcion, int provinciaId, String localidad, Pageable p);
	 */
}
