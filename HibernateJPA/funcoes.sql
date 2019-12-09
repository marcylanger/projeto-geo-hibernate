
SELECT AddGeometryColumn('', 'end_device', 'the_geom1', 4326, 'POINT', 2);


ALTER TABLE end_device DROP COLUMN the_geom;
SELECT AddGeometryColumn('', 'end_device', 'the_geom', 4326, 'POINT', 2);
INSERT INTO end_device(created, identificador, the_geom) 
VALUES (now(), 'PONTO MISSAL 1', st_pointfromtext('POINT(-54.1443 -25.0454)', 4326));

INSERT INTO end_device(created, identificador, the_geom) 
VALUES (now(), 'PONTO MISSAL 2', st_pointfromtext('POINT(-54.1441 -25.0455)', 4326));

INSERT INTO end_device(created, identificador, the_geom) 
VALUES (now(), 'PONTO MISSAL 3', st_pointfromtext('POINT(-25.0454 -54.1441)', 4326));
select * from end_device;

ALTER TABLE gateway DROP COLUMN the_geom;
ALTER TABLE gateway RENAME COLUMN rario_alcance to raio_alcance;
SELECT AddGeometryColumn('', 'gateway', 'the_geom', 4326, 'POINT', 2);
INSERT INTO gateway(created, identificador, raio_alcance, the_geom) 
VALUES (now(), 'GATEWAY MISSAL 1234', 0.3, st_pointfromtext('POINT(-54.1443 -25.0454)', 4326));
select * from gateway;

ALTER TABLE area_produtiva DROP COLUMN the_geom;
ALTER TABLE area_produtiva RENAME COLUMN descriçao to descricao;
SELECT AddGeometryColumn('', 'area_produtiva', 'the_geom', 4326, 'POLYGON', 2);
INSERT INTO area_produtiva(created, descricao, nome, the_geom) 
VALUES (now(), '', 'ÁREA PRODUTIVA MISSAL', ST_POLYGONFROMTEXT('POLYGON((-54.1445 -25.0456, -54.1443 -25.0453, -54.1440 -25.0454, -54.1440 -25.0456, -54.1445 -25.0456))', 4326));
select * from area_produtiva;


--- retornar quantos nós há em uma área produtiva
CREATE OR REPLACE FUNCTION qtde_nos_area(area_produtiva_nome varchar) 
RETURNS INTEGER AS 
$$
DECLARE
	V_QTDE INTEGER;
BEGIN
	SELECT COUNT(e.*)
	FROM area_produtiva a, end_device e
	WHERE  
	ST_CONTAINS(a.the_geom, e.the_geom)
	AND a.nome = area_produtiva_nome INTO V_QTDE;
	
	RETURN V_QTDE;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM qtde_nos_area('ÁREA PRODUTIVA MISSAL');



--- listar os nós de uma área produtiva

CREATE OR REPLACE FUNCTION nos_area(area_produtiva_nome varchar) 
RETURNS TABLE(id BIGINT, identificador VARCHAR(255), the_geom GEOMETRY) AS
$$

BEGIN
	RETURN QUERY SELECT e.id, e.identificador, e.the_geom
	FROM area_produtiva a, end_device e
	WHERE  
	ST_CONTAINS(a.the_geom, e.the_geom) 
	AND a.nome = area_produtiva_nome;
END;
$$ LANGUAGE 'plpgsql';

SELECT id, identificador, the_geom FROM nos_area('ÁREA PRODUTIVA MISSAL');



--- listar os gateways de uma área produtiva

CREATE OR REPLACE FUNCTION gateways_area(area_produtiva_nome varchar) 
RETURNS TABLE(id BIGINT, identificador VARCHAR(255), the_geom GEOMETRY) AS
$$

BEGIN
	RETURN QUERY SELECT g.id, g.identificador, g.the_geom
	FROM area_produtiva a, gateway g
	WHERE  
	ST_CONTAINS(a.the_geom, g.the_geom) 
	AND a.nome = area_produtiva_nome;
END;
$$ LANGUAGE 'plpgsql';

SELECT id, identificador, the_geom FROM gateways_area('ÁREA PRODUTIVA MISSAL');

--- determinar raio de posicionamento dos nós de acordo com a posição do Gateway

CREATE OR REPLACE FUNCTION raio_abrangencia_gateway(gateway_identificador varchar) 
RETURNS GEOMETRY AS 
$$
DECLARE
	v_raio GEOMETRY;
BEGIN
	SELECT ST_TRANSFORM(ST_BUFFER(ST_TRANSFORM(THE_GEOM, 29192), raio_alcance), 4326) as raio_posicionamento
	FROM gateway
	WHERE identificador = gateway_identificador INTO v_raio;
	
	RETURN v_raio;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM raio_abrangencia_gateway('GATEWAY MISSAL');

CREATE OR REPLACE FUNCTION verifica_posicao_no(identificador_no varchar, identificador_gateway varchar)
RETURNS BOOLEAN AS
$$
DECLARE 
	v_area_cobertura GEOMETRY;
	v_no GEOMETRY;
BEGIN
	SELECT raio_abrangencia_gateway FROM raio_abrangencia_gateway(identificador_gateway) into v_area_cobertura;
	SELECT the_geom FROM end_device where identificador = identificador_no into v_no;
	
	if(ST_CONTAINS(v_area_cobertura, v_no)) THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;	
END;
$$ LANGUAGE plpgsql;

SELECT * FROM verifica_posicao_no('PONTO MISSAL 3', 'GATEWAY MISSAL');

--- adicionar nova área produtiva
CREATE OR REPLACE FUNCTION adicionar_area(descricao varchar, nome varchar, x float[], y float[]) 
RETURNS BOOLEAN
AS $$
DECLARE
	tam int; 
	pontos text = 'POLYGON((';
BEGIN

	tam := array_length(x, 1);
	raise notice 'TAM %', tam;

	for i in 1..array_upper(x, 1) loop
	 	pontos :=pontos || x[i] || ' ' || y[i];
	 	if(i < tam) then
	 		pontos := pontos || ',';
		else
			pontos := pontos || '))';
	 	end if;
	end loop;
raise notice 'PONTOS %', pontos;
	INSERT INTO area_produtiva(created, descricao, nome, the_geom) 
	VALUES (now(), descricao, nome, ST_POLYGONFROMTEXT(pontos, '4326'));

	RETURN TRUE;
	
END;
$$ language plpgsql;

SELECT * FROM adicionar_area('', 'ÁREA PRODUTIVA MISSAL 2', array[-54.1445, -54.1443, -54.1440, -54.1440, -54.1445], 
							 array[-25.0456, -25.0453, -25.0454, -25.0456, -25.0456]);
select * from area_produtiva;

--- remover área produtiva e todos os pontos de nós e gateways
CREATE OR REPLACE FUNCTION remover_area(nome_area varchar) 
RETURNS boolean 
AS $$
DECLARE
	v_dados_nos refcursor;
    v_linha_no record;
	v_dados_gateways refcursor;
    v_linha_gateway record;
BEGIN
	
	
	open v_dados_nos for execute 'SELECT * FROM nos_area('''||nome_area||''')';
    loop fetch v_dados_nos into v_linha_no;
   	 exit when not found;
	 	PERFORM * FROM remover_no(v_linha_no.identificador);
    end loop;

	open v_dados_gateways for execute 'SELECT * FROM gateways_area('''||nome_area||''')';
    loop fetch v_dados_gateways into v_linha_gateway;
   	 exit when not found;
	 	PERFORM * FROM remover_gateway(v_linha_gateway.identificador);
    end loop;
	
	DELETE FROM area_produtiva WHERE nome = nome_area;

	return true;


END;
$$ language plpgsql;

SELECT * FROM remover_area('ÁREA PRODUTIVA MISSAL');
select * from area_produtiva;


--- incluir nó
CREATE OR REPLACE FUNCTION adicionar_no(identificador varchar, x1 float, y1 float) 
RETURNS BOOLEAN
AS $$
DECLARE
	v_ponto text;
BEGIN
	
	v_ponto := 'POINT(' || x1 || ' ' || y1 || ')';
	raise notice 'ponto %', v_ponto;
	INSERT INTO end_device(created, identificador, the_geom) 
	VALUES (now(), identificador, st_pointfromtext(v_ponto, 4326));
	
	RETURN TRUE;

END;
$$ language plpgsql;

SELECT * FROM adicionar_no('PONTO MISSAL 3', -54.1443, -25.0454);
select * from end_device;


--- remover nó
CREATE OR REPLACE FUNCTION remover_no(identificador_no varchar) 
RETURNS boolean
AS $$
BEGIN
	
	DELETE FROM end_device a WHERE a.identificador = identificador_no;
	
	RETURN TRUE;

END;
$$ language plpgsql;

SELECT * FROM remover_no('PONTO MISSAL 3');
select * from end_device;

--- incluir gateway
DROP FUNCTION adicionar_gateway(identificador varchar, raio float, x1 float, y1 float);
CREATE OR REPLACE FUNCTION adicionar_gateway(identificador varchar, raio float, x1 float, y1 float) 
RETURNS boolean
AS $$
DECLARE
	v_ponto text;
BEGIN
	
	v_ponto := 'POINT(' || x1 || ' ' || y1 || ')';
	raise notice 'ponto %', v_ponto;
	INSERT INTO gateway(created, identificador, raio_alcance, the_geom)
	VALUES (now(), identificador, raio, st_pointfromtext(v_ponto, 4326));

	RETURN TRUE;

END;
$$ language plpgsql;

SELECT * FROM adicionar_gateway('GATEWAY MISSAL 22', 50, -54.1445, -25.0455);
select * from gateway;

--- remover gateway
CREATE OR REPLACE FUNCTION remover_gateway(identificador_no varchar) 
RETURNS boolean
AS $$
BEGIN
	
	DELETE FROM gateway WHERE identificador = identificador_no;
	
	RETURN TRUE;

END;
$$ language plpgsql;


SELECT * FROM remover_gateway('GATEWAY MISSAL 2');
select * from gateway;
