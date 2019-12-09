package entidades;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Point;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name="end_device")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EndDevice extends AbstractEntity {
	
	private String identificador;
	
	@Column(columnDefinition = "GEOMETRY", name= "the_geom")
	private Point theGeom;

}
