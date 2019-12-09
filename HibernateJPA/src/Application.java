import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import dao.AreaProdutivaDAO;
import dao.EndDeviceDAO;
import dao.GatewayDAO;
import entidades.AreaProdutiva;
import entidades.EndDevice;
import entidades.Gateway;

public class Application {

	private static EntityManagerFactory emf;

	public static void main(String[] args) {
		emf = Persistence.createEntityManagerFactory("meuProjetoJpa");

		System.out.println("Carregou aplicação");

		inserirGateway();
		//removerGateway();
		inserirEndDevice();
		//removerEndDevice();
		listarNosFromArea();
		getQuantidadeNosFromArea();
		listarNos();
		//inserirAreaProdutiva();
		//removerAreaProdutiva();
		listarGatewaysFromArea();
		verificaPosicaoNo();
		
	}

	private static void inserirGateway() {
		System.out.println("Inserir Gateway");
		Gateway g = new Gateway();
		g.setIdentificador("GATEWAY TESTE 123432");
		g.setRaioAlcance(50.0);

		GatewayDAO gDao = GatewayDAO.getInstance();

		gDao.inserirGateway(g, -54.1446F, -25.0457F);
	}

	private static void removerGateway() {
		System.out.println("Remover Gateway");
		GatewayDAO gDao = GatewayDAO.getInstance();

		gDao.removerGateway("GATEWAY MISSAL 2");
	}

	private static void inserirEndDevice() {
		System.out.println("Inserir Nó");
		EndDevice no = new EndDevice();
		no.setIdentificador("NÓ 123432");

		EndDeviceDAO noDao = EndDeviceDAO.getInstance();

		noDao.inserirEndDevice(no, -54.1446F, -25.0457F);
	}

	private static void removerEndDevice() {
		System.out.println("Remover Nó");
		EndDeviceDAO noDao = EndDeviceDAO.getInstance();

		noDao.removerEndDevice("NÓ 123432");
	}

	private static void listarNosFromArea() {
		System.out.println("Listas Nós de uma área produtiva");
		EndDeviceDAO noDao = EndDeviceDAO.getInstance();

		List<EndDevice> nos = noDao.listarNosFromArea("ÁREA PRODUTIVA MISSAL");

		for (EndDevice endDevice : nos) {
			System.out.println(endDevice);
		}

	}

	private static void getQuantidadeNosFromArea() {
		System.out.println("Quantidade de Nós de uma área produtiva");
		EndDeviceDAO noDao = EndDeviceDAO.getInstance();

		System.out.println(noDao.getQuantidadeNosFromArea("ÁREA PRODUTIVA MISSAL"));
	}

	private static void listarNos() {
		System.out.println("Listar todos os nós");
		EndDeviceDAO noDao = EndDeviceDAO.getInstance();

		List<EndDevice> nos = noDao.listarNos();

		for (EndDevice endDevice : nos) {
			System.out.println(endDevice);
		}
	}
	
	private static void inserirAreaProdutiva() {
		System.out.println("Inserir Área Produtiva");
		AreaProdutiva area = new AreaProdutiva();
		area.setNome("ÁREA PRODUTIVA TESTE");
		area.setDescricao("DESCRIÇÃO DA ÁREA PRODUTIVA");

		AreaProdutivaDAO areaDao = AreaProdutivaDAO.getInstance();

		Float[] x1 = {-54.1445F, -54.1443F, -54.1440F, -54.1440F, -54.1445F};
		Float[] y1 = {-25.0456F, -25.0453F, -25.0454F, -25.0456F, -25.0456F};
		
		areaDao.inserirAreaProdutiva(area, x1, y1);
	}
	
	private static void removerAreaProdutiva() {
		System.out.println("Remover Área Produtiva");
		AreaProdutivaDAO areaDao = AreaProdutivaDAO.getInstance();
		
		areaDao.removerAreaProdutiva("ÁREA PRODUTIVA MISSAL");
	}
	
	private static void listarGatewaysFromArea() {
		System.out.println("Listar Gateways de uma área produtiva");
		GatewayDAO gDao = GatewayDAO.getInstance();

		
		List<Gateway> gateways = gDao.listarGatewayFromArea("ÁREA PRODUTIVA MISSAL");

		for (Gateway gateway : gateways) {
			System.out.println(gateway);
		}

	}
	
	private static void verificaPosicaoNo() {
		System.out.println("Verificar se o nós está dentro da área de cobertura do gateway");
		EndDeviceDAO noDao = EndDeviceDAO.getInstance();

		Boolean resultado = noDao.verificaPosicaoNo("NÓ 123432", "GATEWAY TESTE 123432");

		System.out.println("O nó está dentro da área de cobertura do gateway?");
		System.out.println(resultado ? "Sim" : "Não");
	}
	
	
	

}
