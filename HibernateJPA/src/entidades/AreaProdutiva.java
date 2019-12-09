package entidades;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.locationtech.jts.geom.Polygon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity(name="area_produtiva")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AreaProdutiva extends AbstractEntity {

	private String nome;
	
	private String descricao;
	
	@Column(columnDefinition = "GEOMETRY", name= "the_geom")
	private Polygon theGeom;
	
	
}
