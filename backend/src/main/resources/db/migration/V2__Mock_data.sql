-- Script para inserir dados de teste (mock) no banco de dados.

-- Inserir Usuários (Clientes e um Vendedor adicional)
-- A senha para todos é 'senha123'
INSERT INTO usuarios (nome, email, senha, perfil) VALUES
                                                      ('Ana Silva', 'ana.silva@email.com', '$2a$10$w4.g.y.T.xZ7K.rYc.QZ..42eENyYl1Rj8d2T/oE/O3e2jYh.MvI2', 'CLIENTE'),
                                                      ('Bruno Costa', 'bruno.costa@email.com', '$2a$10$w4.g.y.T.xZ7K.rYc.QZ..42eENyYl1Rj8d2T/oE/O3e2jYh.MvI2', 'CLIENTE'),
                                                      ('Carla Dias', 'carla.dias@email.com', '$2a$10$w4.g.y.T.xZ7K.rYc.QZ..42eENyYl1Rj8d2T/oE/O3e2jYh.MvI2', 'CLIENTE'),
                                                      ('Daniel Faria', 'daniel.faria@email.com', '$2a$10$w4.g.y.T.xZ7K.rYc.QZ..42eENyYl1Rj8d2T/oE/O3e2jYh.MvI2', 'CLIENTE'),
                                                      ('Eduarda Lima', 'eduarda.lima@email.com', '$2a$10$w4.g.y.T.xZ7K.rYc.QZ..42eENyYl1Rj8d2T/oE/O3e2jYh.MvI2', 'CLIENTE'),
                                                      ('Fernanda Souza', 'fernanda.souza@email.com', '$2a$10$w4.g.y.T.xZ7K.rYc.QZ..42eENyYl1Rj8d2T/oE/O3e2jYh.MvI2', 'CLIENTE'),
                                                      ('Vendedor Loja', 'vendedor.loja@vendas.com', '$2a$10$w4.g.y.T.xZ7K.rYc.QZ..42eENyYl1Rj8d2T/oE/O3e2jYh.MvI2', 'VENDEDOR');

-- Inserir Produtos
INSERT INTO produtos (nome, descricao, preco, qtd_estoque) VALUES
                                                               ('Smartphone Galaxy S25', 'O mais novo lançamento com câmera de 200MP e 16GB de RAM.', 7499.90, 50),
                                                               ('Notebook Pro Max', 'Notebook de alta performance para profissionais. Tela 4K e 32GB RAM.', 12500.00, 25),
                                                               ('Fone de Ouvido TWS Pro', 'Fone sem fio com cancelamento de ruído ativo e 24h de bateria.', 899.90, 150),
                                                               ('Smartwatch Fit Pro', 'Monitore sua saúde e atividades físicas com estilo.', 1200.00, 80),
                                                               ('Câmera Mirrorless Alpha', 'Para fotógrafos e videomakers exigentes. Sensor Full-Frame.', 9850.00, 15),
                                                               ('Teclado Mecânico Gamer K-900', 'Switches ópticos para resposta ultrarrápida e RGB customizável.', 550.00, 200),
                                                               ('Mouse Gamer M-500', 'Sensor de 20.000 DPI e design ergonômico para longas sessões.', 350.00, 300),
                                                               ('Monitor 4K 27 polegadas', 'Cores vibrantes e imagem nítida para trabalho e lazer.', 2800.00, 40),
                                                               ('Cadeira Gamer Ergonômica', 'Conforto e suporte para sua coluna durante o dia todo.', 1500.00, 60),
                                                               ('SSD NVMe 2TB Gen4', 'Velocidade de leitura de até 7000MB/s para seu PC ou PS5.', 950.00, 120);

-- Inserir Pedidos
-- Os IDs dos usuários são sequenciais a partir do 1 (admin), então os clientes são de 2 a 7.
INSERT INTO pedidos (cliente_id, status, valor_total, data_criacao) VALUES
                                                                        (2, 'FINALIZADO', 8399.80, NOW() - INTERVAL '10 days'), -- Pedido da Ana Silva
                                                                        (3, 'FINALIZADO', 12500.00, NOW() - INTERVAL '8 days'), -- Pedido do Bruno Costa
                                                                        (4, 'EM_ANDAMENTO', 1550.00, NOW() - INTERVAL '5 days'), -- Pedido da Carla Dias
                                                                        (5, 'EM_ANDAMENTO', 3150.00, NOW() - INTERVAL '2 days'), -- Pedido do Daniel Faria
                                                                        (2, 'CANCELADO', 9850.00, NOW() - INTERVAL '1 day'),  -- Outro pedido da Ana Silva
                                                                        (6, 'EM_ANDAMENTO', 550.00, NOW());              -- Pedido da Eduarda Lima

-- Inserir Itens de Pedido
-- Pedido 1 (Ana Silva)
INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
                                                                                 (1, 1, 1, 7499.90), -- Smartphone
                                                                                 (1, 3, 1, 899.90);  -- Fone TWS

-- Pedido 2 (Bruno Costa)
INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
    (2, 2, 1, 12500.00); -- Notebook

-- Pedido 3 (Carla Dias)
INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
                                                                                 (3, 4, 1, 1200.00), -- Smartwatch
                                                                                 (3, 7, 1, 350.00);  -- Mouse

-- Pedido 4 (Daniel Faria)
INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
                                                                                 (4, 8, 1, 2800.00), -- Monitor
                                                                                 (4, 7, 1, 350.00);  -- Mouse

-- Pedido 5 (Ana Silva, CANCELADO) - O estoque não deve ser alterado
INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
    (5, 5, 1, 9850.00); -- Câmera

-- Pedido 6 (Eduarda Lima, PENDENTE)
INSERT INTO pedido_itens (pedido_id, produto_id, quantidade, preco_unitario) VALUES
    (6, 6, 1, 550.00); -- Teclado

-- Atualizar o estoque dos produtos vendidos nos pedidos FINALIZADOS
UPDATE produtos SET qtd_estoque = qtd_estoque - 1 WHERE id = 1;
UPDATE produtos SET qtd_estoque = qtd_estoque - 1 WHERE id = 3;
UPDATE produtos SET qtd_estoque = qtd_estoque - 1 WHERE id = 2;
