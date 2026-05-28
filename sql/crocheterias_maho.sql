-- ============================================================
-- BASE DE DATOS: Crocheterias Maho
-- Proyecto Integrador - CESDE Bello - 2025
-- Estudiante: Maryam
-- ============================================================
-- PROBLEMA: Una emprendedora de crochet necesita gestionar
--   su negocio en línea: clientes, productos, pedidos y reseñas,
--   garantizando trazabilidad, control de stock y reportes de ventas.
-- SOLUCIÓN: Sistema relacional normalizado con tablas de usuarios,
--   roles, categorías, productos, pedidos, detalles y reseñas.
-- ============================================================

CREATE DATABASE IF NOT EXISTS crocheterias_maho
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE crocheterias_maho;

-- ============================================================
-- TABLA: roles
-- ============================================================
CREATE TABLE roles (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(30) NOT NULL UNIQUE,  -- 'admin', 'user'
    descripcion VARCHAR(100)
);

INSERT INTO roles (nombre, descripcion) VALUES
  ('admin', 'Administrador con acceso total al sistema'),
  ('user',  'Usuario cliente con acceso a compras y perfil');

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100)        NOT NULL,
    email           VARCHAR(150)        NOT NULL UNIQUE,
    password_hash   VARCHAR(255)        NOT NULL,
    telefono        VARCHAR(20),
    ciudad          VARCHAR(80),
    id_rol          INT                 NOT NULL DEFAULT 2,
    activo          BOOLEAN             NOT NULL DEFAULT TRUE,
    fecha_registro  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id)
);

INSERT INTO usuarios (nombre, email, password_hash, telefono, ciudad, id_rol) VALUES
  ('Maryam Herrera',  'admin@maho.co',       '$2a$10$adminHash',  '3001234567', 'Bello',    1),
  ('Laura Martínez',  'laura@gmail.com',     '$2a$10$hash2',      '3112345678', 'Medellín', 2),
  ('Carolina Vargas', 'carolina@gmail.com',  '$2a$10$hash3',      '3209876543', 'Bogotá',   2),
  ('Daniela Ríos',    'daniela@gmail.com',   '$2a$10$hash4',      '3151234567', 'Bello',    2);

-- ============================================================
-- TABLA: categorias
-- ============================================================
CREATE TABLE categorias (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    emoji       VARCHAR(10)
);

INSERT INTO categorias (nombre, descripcion, emoji) VALUES
  ('amigurumis', 'Muñecos tejidos a crochet personalizados',          '🐻'),
  ('prendas',    'Ropa y accesorios tejidos a medida',                '👗'),
  ('llaveros',   'Llaveros personalizados tejidos a crochet',         '🗝️'),
  ('ramos',      'Ramos de flores tejidas que no se marchitan',       '🌹'),
  ('otros',      'Otros tejidos personalizados: bolsos, mantas, etc.','✨');

-- ============================================================
-- TABLA: productos
-- ============================================================
CREATE TABLE productos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150)       NOT NULL,
    descripcion     TEXT,
    precio          DECIMAL(10, 2)     NOT NULL,
    stock           INT                NOT NULL DEFAULT 0,
    emoji           VARCHAR(10),
    disponible      BOOLEAN            NOT NULL DEFAULT TRUE,
    id_categoria    INT                NOT NULL,
    fecha_creacion  TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id)
);

INSERT INTO productos (nombre, descripcion, precio, stock, emoji, id_categoria) VALUES
  ('Amigurumi Osito Personalizado',  'Osito tejido con colores a elección, relleno hipoalergénico.',    45000, 5,  '🐻', 1),
  ('Amigurumi Unicornio',            'Unicornio mágico tejido con hilo multicolor brillante.',          55000, 4,  '🦄', 1),
  ('Vestido Tejido Personalizado',   'Prenda a medida, colores y estilo a elección.',                  180000, 2, '👗', 2),
  ('Top de Crochet Boho',            'Top ligero estilo boho, perfecto para verano.',                   95000, 4,  '👚', 2),
  ('Llavero Personalizado',          'Llavero con inicial o diseño favorito, colores a elección.',      18000, 20, '🗝️', 3),
  ('Ramo de Rosas Tejidas',          'Ramo de 12 rosas tejidas que duran para siempre.',               75000, 3,  '🌹', 4),
  ('Ramo de Girasoles Tejidos',      'Girasoles eternos tejidos, nunca se marchitan.',                  65000, 6,  '🌻', 4),
  ('Bolso Tejido Artesanal',         'Bolso de crochet resistente, varios colores disponibles.',       120000, 3, '👜', 5);

-- ============================================================
-- TABLA: pedidos
-- ============================================================
CREATE TABLE pedidos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario      INT             NOT NULL,
    total           DECIMAL(10, 2)  NOT NULL,
    estado          ENUM('pendiente','en_proceso','enviado','entregado','cancelado')
                                    NOT NULL DEFAULT 'pendiente',
    direccion       VARCHAR(250),
    notas           TEXT,
    fecha_pedido    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega   TIMESTAMP,
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

INSERT INTO pedidos (id_usuario, total, estado, direccion) VALUES
  (2, 45000,  'entregado',   'Cra 50 #30-10, Medellín'),
  (3, 75000,  'en_proceso',  'Cll 100 #15-20, Bogotá'),
  (4, 54000,  'pendiente',   'Cra 48 #12-5, Bello'),
  (2, 180000, 'enviado',     'Cra 50 #30-10, Medellín');

-- ============================================================
-- TABLA: detalle_pedidos
-- ============================================================
CREATE TABLE detalle_pedidos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido       INT             NOT NULL,
    id_producto     INT             NOT NULL,
    cantidad        INT             NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10, 2)  NOT NULL,
    CONSTRAINT fk_detalle_pedido   FOREIGN KEY (id_pedido)   REFERENCES pedidos(id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
);

INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_unitario) VALUES
  (1, 1, 1, 45000),
  (2, 6, 1, 75000),
  (3, 5, 3, 18000),
  (4, 3, 1, 180000);

-- ============================================================
-- TABLA: resenas
-- ============================================================
CREATE TABLE resenas (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario  INT     NOT NULL,
    id_producto INT     NOT NULL,
    estrellas   TINYINT NOT NULL CHECK (estrellas BETWEEN 1 AND 5),
    comentario  TEXT,
    fecha       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resena_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuarios(id),
    CONSTRAINT fk_resena_producto FOREIGN KEY (id_producto) REFERENCES productos(id)
);

INSERT INTO resenas (id_usuario, id_producto, estrellas, comentario) VALUES
  (2, 1, 5, '¡Mi amigurumi llegó perfectísimo, mejor de lo que esperaba!'),
  (3, 6, 5, 'El ramo de rosas tejidas es una maravilla, mi mamá lloró de emoción.'),
  (4, 5, 4, 'Los llaveros son adorables. Ya le compré a toda mi familia.');

-- ============================================================
-- ===   5 PROCEDIMIENTOS ALMACENADOS   ===
-- ============================================================

DELIMITER $$

-- ────────────────────────────────────────────────────────────
-- PA 1: Registrar un nuevo pedido con su detalle
--       y actualizar el stock automáticamente
-- ────────────────────────────────────────────────────────────
CREATE PROCEDURE sp_crear_pedido(
    IN p_id_usuario  INT,
    IN p_id_producto INT,
    IN p_cantidad    INT,
    IN p_direccion   VARCHAR(250)
)
BEGIN
    DECLARE v_precio    DECIMAL(10,2);
    DECLARE v_stock     INT;
    DECLARE v_total     DECIMAL(10,2);
    DECLARE v_id_pedido INT;

    -- Verificar stock disponible
    SELECT precio, stock INTO v_precio, v_stock
    FROM productos WHERE id = p_id_producto;

    IF v_stock < p_cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para este producto.';
    END IF;

    SET v_total = v_precio * p_cantidad;

    -- Crear cabecera del pedido
    INSERT INTO pedidos (id_usuario, total, estado, direccion)
    VALUES (p_id_usuario, v_total, 'pendiente', p_direccion);

    SET v_id_pedido = LAST_INSERT_ID();

    -- Insertar detalle
    INSERT INTO detalle_pedidos (id_pedido, id_producto, cantidad, precio_unitario)
    VALUES (v_id_pedido, p_id_producto, p_cantidad, v_precio);

    -- Descontar stock
    UPDATE productos SET stock = stock - p_cantidad WHERE id = p_id_producto;

    SELECT v_id_pedido AS id_pedido_creado, v_total AS total_pedido;
END$$

-- ────────────────────────────────────────────────────────────
-- PA 2: Actualizar el estado de un pedido
-- ────────────────────────────────────────────────────────────
CREATE PROCEDURE sp_actualizar_estado_pedido(
    IN p_id_pedido INT,
    IN p_nuevo_estado ENUM('pendiente','en_proceso','enviado','entregado','cancelado')
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pedidos WHERE id = p_id_pedido) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El pedido especificado no existe.';
    END IF;

    UPDATE pedidos SET estado = p_nuevo_estado WHERE id = p_id_pedido;

    -- Si se cancela, devolver stock
    IF p_nuevo_estado = 'cancelado' THEN
        UPDATE productos p
        JOIN detalle_pedidos dp ON dp.id_producto = p.id
        SET p.stock = p.stock + dp.cantidad
        WHERE dp.id_pedido = p_id_pedido;
    END IF;

    -- Si es entregado, registrar fecha
    IF p_nuevo_estado = 'entregado' THEN
        UPDATE pedidos SET fecha_entrega = NOW() WHERE id = p_id_pedido;
    END IF;

    SELECT CONCAT('Pedido #', p_id_pedido, ' actualizado a: ', p_nuevo_estado) AS resultado;
END$$

-- ────────────────────────────────────────────────────────────
-- PA 3: Reporte de ventas por categoría en un rango de fechas
-- ────────────────────────────────────────────────────────────
CREATE PROCEDURE sp_reporte_ventas_categoria(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin    DATE
)
BEGIN
    SELECT
        c.nombre                        AS categoria,
        COUNT(DISTINCT pe.id)           AS total_pedidos,
        SUM(dp.cantidad)                AS unidades_vendidas,
        SUM(dp.cantidad * dp.precio_unitario) AS ingresos_totales
    FROM detalle_pedidos dp
    JOIN productos   pr ON pr.id = dp.id_producto
    JOIN categorias  c  ON c.id  = pr.id_categoria
    JOIN pedidos     pe ON pe.id = dp.id_pedido
    WHERE pe.estado != 'cancelado'
      AND DATE(pe.fecha_pedido) BETWEEN p_fecha_inicio AND p_fecha_fin
    GROUP BY c.nombre
    ORDER BY ingresos_totales DESC;
END$$

-- ────────────────────────────────────────────────────────────
-- PA 4: Registrar una reseña (un usuario solo puede reseñar
--       un producto que haya comprado)
-- ────────────────────────────────────────────────────────────
CREATE PROCEDURE sp_agregar_resena(
    IN p_id_usuario  INT,
    IN p_id_producto INT,
    IN p_estrellas   TINYINT,
    IN p_comentario  TEXT
)
BEGIN
    -- Verificar que el usuario haya comprado el producto
    IF NOT EXISTS (
        SELECT 1 FROM detalle_pedidos dp
        JOIN pedidos pe ON pe.id = dp.id_pedido
        WHERE pe.id_usuario = p_id_usuario
          AND dp.id_producto = p_id_producto
          AND pe.estado IN ('enviado','entregado')
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Solo puedes reseñar productos que hayas comprado y recibido.';
    END IF;

    -- Verificar que no haya reseñado ya este producto
    IF EXISTS (SELECT 1 FROM resenas WHERE id_usuario = p_id_usuario AND id_producto = p_id_producto) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ya dejaste una reseña para este producto.';
    END IF;

    INSERT INTO resenas (id_usuario, id_producto, estrellas, comentario)
    VALUES (p_id_usuario, p_id_producto, p_estrellas, p_comentario);

    SELECT 'Reseña registrada exitosamente.' AS resultado;
END$$

-- ────────────────────────────────────────────────────────────
-- PA 5: Obtener el historial de pedidos de un usuario
--       con detalle de productos
-- ────────────────────────────────────────────────────────────
CREATE PROCEDURE sp_historial_usuario(
    IN p_id_usuario INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_id_usuario) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuario no encontrado.';
    END IF;

    SELECT
        pe.id                 AS id_pedido,
        pe.fecha_pedido,
        pe.estado,
        pe.total,
        pr.nombre             AS producto,
        pr.emoji,
        dp.cantidad,
        dp.precio_unitario,
        c.nombre              AS categoria
    FROM pedidos pe
    JOIN detalle_pedidos dp ON dp.id_pedido   = pe.id
    JOIN productos       pr ON pr.id          = dp.id_producto
    JOIN categorias      c  ON c.id           = pr.id_categoria
    WHERE pe.id_usuario = p_id_usuario
    ORDER BY pe.fecha_pedido DESC;
END$$

DELIMITER ;

-- ============================================================
-- ===   10 PREGUNTAS QUE LA BASE DE DATOS RESUELVE   ===
-- ============================================================

-- P1. ¿Cuáles son todos los productos disponibles con su categoría?
SELECT p.nombre, p.precio, p.stock, c.nombre AS categoria, p.emoji
FROM productos p
JOIN categorias c ON c.id = p.id_categoria
WHERE p.disponible = TRUE
ORDER BY c.nombre, p.precio;

-- P2. ¿Cuántos pedidos tiene cada usuario y cuánto ha gastado en total?
SELECT u.nombre, u.email,
       COUNT(pe.id)   AS total_pedidos,
       SUM(pe.total)  AS gasto_total
FROM usuarios u
LEFT JOIN pedidos pe ON pe.id_usuario = u.id
WHERE u.id_rol = 2
GROUP BY u.id
ORDER BY gasto_total DESC;

-- P3. ¿Cuál es el producto más vendido por cantidad de unidades?
SELECT pr.nombre, pr.emoji,
       SUM(dp.cantidad) AS unidades_vendidas,
       SUM(dp.cantidad * dp.precio_unitario) AS ingresos
FROM detalle_pedidos dp
JOIN productos pr ON pr.id = dp.id_producto
JOIN pedidos   pe ON pe.id = dp.id_pedido
WHERE pe.estado != 'cancelado'
GROUP BY pr.id
ORDER BY unidades_vendidas DESC
LIMIT 5;

-- P4. ¿Qué pedidos están aún pendientes o en proceso?
SELECT pe.id, u.nombre AS cliente, pe.total, pe.estado, pe.fecha_pedido, pe.direccion
FROM pedidos pe
JOIN usuarios u ON u.id = pe.id_usuario
WHERE pe.estado IN ('pendiente','en_proceso')
ORDER BY pe.fecha_pedido ASC;

-- P5. ¿Cuál es el promedio de calificación (estrellas) por producto?
SELECT pr.nombre, pr.emoji,
       ROUND(AVG(r.estrellas), 1) AS promedio_estrellas,
       COUNT(r.id)                AS cantidad_resenas
FROM productos pr
LEFT JOIN resenas r ON r.id_producto = pr.id
GROUP BY pr.id
ORDER BY promedio_estrellas DESC;

-- P6. ¿Cuánto ha vendido cada categoría en total?
SELECT c.nombre AS categoria, c.emoji,
       COUNT(DISTINCT pe.id)              AS pedidos,
       SUM(dp.cantidad)                   AS unidades,
       SUM(dp.cantidad * dp.precio_unitario) AS ingresos_totales
FROM categorias c
LEFT JOIN productos      pr ON pr.id_categoria = c.id
LEFT JOIN detalle_pedidos dp ON dp.id_producto  = pr.id
LEFT JOIN pedidos         pe ON pe.id           = dp.id_pedido
                             AND pe.estado != 'cancelado'
GROUP BY c.id
ORDER BY ingresos_totales DESC;

-- P7. ¿Qué productos tienen stock bajo (menos de 3 unidades)?
SELECT nombre, emoji, stock, precio
FROM productos
WHERE stock < 3 AND disponible = TRUE
ORDER BY stock ASC;

-- P8. ¿Cuántos usuarios se registraron cada mes del año actual?
SELECT
    MONTH(fecha_registro)  AS mes,
    MONTHNAME(fecha_registro) AS nombre_mes,
    COUNT(id)              AS nuevos_usuarios
FROM usuarios
WHERE YEAR(fecha_registro) = YEAR(CURDATE())
GROUP BY MONTH(fecha_registro)
ORDER BY mes;

-- P9. ¿Cuál es el cliente que más ha gastado y qué productos compró?
SELECT u.nombre, u.email, SUM(pe.total) AS gasto_total
FROM usuarios u
JOIN pedidos pe ON pe.id_usuario = u.id
WHERE pe.estado != 'cancelado'
GROUP BY u.id
ORDER BY gasto_total DESC
LIMIT 1;

-- P10. ¿Cuál es el ingreso total del negocio y el ticket promedio por pedido?
SELECT
    COUNT(id)                                        AS total_pedidos,
    SUM(total)                                       AS ingreso_total,
    ROUND(AVG(total), 0)                             AS ticket_promedio,
    MAX(total)                                       AS pedido_mas_alto,
    MIN(total)                                       AS pedido_mas_bajo
FROM pedidos
WHERE estado != 'cancelado';
