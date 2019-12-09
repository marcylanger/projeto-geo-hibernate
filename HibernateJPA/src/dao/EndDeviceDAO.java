package dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import entidades.EndDevice;

public class EndDeviceDAO {

	private static EndDeviceDAO instance;
	protected EntityManager entityManager;

	public static EndDeviceDAO getInstance() {
		if (instance == null) {
			instance = new EndDeviceDAO();
		}

		return instance;
	}

	private EndDeviceDAO() {
		entityManager = getEntityManager();
	}

	private EntityManager getEntityManager() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("meuProjetoJpa");
		if (entityManager == null) {
			entityManager = factory.createEntityManager();
		}

		return entityManager;
	}

	public void inserirEndDevice(EndDevice no, Float x1, Float y1) {
		Query query = this.entityManager.createNativeQuery("SELECT * FROM adicionar_no(:identificador, :x1, :y1)");
		query.setParameter("identificador", no.getIdentificador());
		query.setParameter("x1", x1);
		query.setParameter("y1", y1);

		query.getResultList();
		System.out.println("Inseriu nó");
	}

	public void removerEndDevice(String identificador) {
		Query query = this.entityManager.createNativeQuery("SELECT * FROM remover_no(:identificador)");
		query.setParameter("identificador", identificador);

		query.getResultList();
		System.out.println("Removeu nó");
	}

	public List<EndDevice> listarNosFromArea(String identificadorArea) {

		Query query = this.entityManager.createNativeQuery("SELECT id FROM nos_area(:identificador)");
		query.setParameter("identificador", identificadorArea);

		List<BigInteger> ids = (List<BigInteger>) query.getResultList();

		List<EndDevice> nos = new ArrayList<EndDevice>();
		for (BigInteger id : ids) {
			EndDevice endDevice = entityManager.find(EndDevice.class, Long.parseLong(id.toString()));
			nos.add(endDevice);
		}
		System.out.println("Listou nós de uma área produtiva");

		return nos;

	}

	public List<EndDevice> listarNos() {
		System.out.println("Listou todos os nós");
		return entityManager.createQuery("FROM " + EndDevice.class.getName()).getResultList();

	}

	public Integer getQuantidadeNosFromArea(String identificadorArea) {

		Query query = this.entityManager.createNativeQuery("SELECT * FROM qtde_nos_area(:identificador)");
		query.setParameter("identificador", identificadorArea);

		System.out.println("Retornou a quantidade de nós de uma área produtiva");
		return Integer.parseInt(query.getResultList().get(0).toString());

	}
	
	public Boolean verificaPosicaoNo(String identificadorNo, String identificadorGateway) {
		Query query = this.entityManager.createNativeQuery("SELECT * FROM verifica_posicao_no(:identificadorNo, :identificadorGateway)");
		query.setParameter("identificadorNo", identificadorNo);
		query.setParameter("identificadorGateway", identificadorGateway);

		System.out.println("Verificou se o nó está no raio de alcance do gateway");
		return (Boolean) query.getSingleResult();
	}
}
