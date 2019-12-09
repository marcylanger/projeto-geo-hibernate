package entidades;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.locationtech.jts.geom.Point;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Gateway extends AbstractEntity {
	
	private String identificador;
	
	@Column(name="raio_alcance")
	private Double raioAlcance;
	
	@Column(columnDefinition = "GEOMETRY", name= "the_geom")
	private Point theGeom;

}
