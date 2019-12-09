package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import entidades.AreaProdutiva;

public class AreaProdutivaDAO {

	private static AreaProdutivaDAO instance;
	protected EntityManager entityManager;

	public static AreaProdutivaDAO getInstance() {
		if (instance == null) {
			instance = new AreaProdutivaDAO();
		}

		return instance;
	}

	private AreaProdutivaDAO() {
		entityManager = getEntityManager();
	}

	private EntityManager getEntityManager() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("meuProjetoJpa");
		if (entityManager == null) {
			entityManager = factory.createEntityManager();
		}

		return entityManager;
	}
	
	public void inserirAreaProdutiva(AreaProdutiva area, Float[] x1, Float[] y1) {
		/*
		 * SELECT * FROM adicionar_area('', 'ÁREA PRODUTIVA MISSAL 2', array[-54.1445, -54.1443, -54.1440, -54.1440, -54.1445], 
							 array[-25.0456, -25.0453, -25.0454, -25.0456, -25.0456]);

		 */
		Query query = this.entityManager.createNativeQuery("SELECT adicionar_area(:descricao, :nome, :x1, :y1)");
		query.setParameter("descricao", area.getDescricao());
		query.setParameter("nome", area.getNome());
		query.setParameter("x1", x1);
		query.setParameter("y1", y1);
		
		query.getResultList();
		System.out.println("Inseriu area produtiva");
	}
	
	public void removerAreaProdutiva(String nome) {
		Query query = this.entityManager.createNativeQuery("SELECT * FROM remover_area(:nome)");
		query.setParameter("nome", nome);
		
		query.getResultList();
		System.out.println("Removeu área produtiva");
	}




}
