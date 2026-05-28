# 🧶 Crocheterias Maho — Proyecto Integrador CESDE Bello 2025

**Estudiante:** Maryam Herrera  
**Institución:** CESDE Bello — Segundo Semestre Programación  
**Negocio:** Tienda online de crochet personalizado

---

## 📁 Estructura del Proyecto

```
crocheterias-maho/
│
├── 📄 index.html                  # Página principal (inicio)
│
├── css/
│   └── styles.css                 # Estilos globales (paleta terra + rosa palo)
│
├── html/
│   ├── catalogo.html              # Catálogo con carrito y filtros
│   ├── nosotros.html              # Historia desde 2020
│   ├── curiosidades.html          # 10 curiosidades del crochet
│   ├── registro.html              # Registro de nuevos usuarios
│   ├── dashboard.html             # Panel del usuario (pedidos)
│   └── admin.html                 # Panel administrador (CRUD productos/usuarios)
│
├── js/
│   ├── auth.js                    # Lógica de autenticación (Momento 1 + 2)
│   ├── cart.js                    # Carrito de compras
│   ├── api.js                     # Consumo MockAPI con fetch (Momento 3)
│   └── momento1_login.js          # Entregable puro Momento 1 (consola)
│
├── sql/
│   └── crocheterias_maho.sql      # Base de datos completa:
│                                  # • 6 tablas relacionadas
│                                  # • 5 procedimientos almacenados
│                                  # • 10 preguntas resueltas
│
└── java/
    ├── pom.xml                    # Maven: Hibernate + PostgreSQL + iText PDF
    └── src/main/java/com/maho/crochet/
        ├── entity/
        │   ├── Rol.java
        │   ├── Usuario.java
        │   └── Entidades.java     # Categoria, Producto, Pedido, DetallePedido, Resena
        ├── repository/
        │   ├── Repository.java            # Interfaz genérica T,ID
        │   ├── GenericRepositoryImpl.java # Implementación genérica CRUD
        │   └── Repositorios.java          # ProductoRepo, UsuarioRepo, PedidoRepo
        ├── service/
        │   └── PdfService.java    # Exportación a PDF con iText (dep. adicional)
        ├── util/
        │   └── JpaUtil.java       # Singleton EntityManagerFactory
        └── ui/
            └── MenuPrincipal.java # Consola interactiva CRUD completa
```

---

## 🌐 JavaScript — Momentos del Proyecto

### Momento 1 — Lógica pura en consola
**Archivo:** `js/momento1_login.js`

- Credenciales hardcodeadas con `const`
- Captura de datos con `prompt()`
- Ciclo `while` para controlar 3 intentos
- Validación con `===`
- Mensajes de éxito o bloqueo en `console.log`
- Toda la lógica encapsulada en `const validarAcceso = () => {}`

**Cómo ejecutar:** Abrir DevTools (F12) → Console → pegar y ejecutar el archivo.

---

### Momento 2 — Login con DOM
**Archivo:** `js/auth.js`

- Reutiliza la lógica del Momento 1
- Selección de elementos con `getElementById`
- `addEventListener` en el botón de login
- Lee `.value` de los inputs
- Actualiza el DOM con `.textContent` e `.innerHTML`
- Barra visual de intentos que se actualiza con `createElement`

---

### Momento 3 — Consumo de API con fetch
**Archivo:** `js/api.js`

- Función `async/await` que consume MockAPI
- Muestra "Cargando..." al iniciar el `fetch`
- Bloque `try...catch` para manejo de errores
- Mensaje de error amigable en el DOM si falla
- Recorre los datos con `forEach` y crea elementos con `createElement + appendChild`
- **Controladores completos** para: Productos, Usuarios, Pedidos, Reseñas, Categorías

**MockAPI URL:** `https://67f14b2b94bde1c1252d31d2.mockapi.io/api/v1`

> Si MockAPI no está activo, el sistema usa datos mock locales automáticamente.

---

## 🗄️ Base de Datos — PostgreSQL

### Tablas
| Tabla | Descripción |
|-------|-------------|
| `roles` | Roles del sistema (admin, user) |
| `usuarios` | Clientes y administradores |
| `categorias` | Categorías de productos |
| `productos` | Catálogo de productos |
| `pedidos` | Órdenes de compra |
| `detalle_pedidos` | Líneas de cada pedido |
| `resenas` | Reseñas de productos |

### Problema → Solución
**Problema:** La emprendedora gestiona todo manualmente (WhatsApp, cuadernos, memoria). No tiene trazabilidad de pedidos, control de stock, ni historial de clientes.

**Solución:** Sistema relacional normalizado que permite: registro de clientes con roles, catálogo de productos por categoría, pedidos con detalles y estados, reseñas verificadas (solo quien compró puede opinar), y reportes de ventas.

### Procedimientos Almacenados
1. `sp_crear_pedido` — Crea pedido y descuenta stock automáticamente
2. `sp_actualizar_estado_pedido` — Cambia estado y devuelve stock si se cancela
3. `sp_reporte_ventas_categoria` — Ingresos por categoría en rango de fechas
4. `sp_agregar_resena` — Solo permite reseñar productos comprados y recibidos
5. `sp_historial_usuario` — Historial completo de pedidos de un cliente

---

## ☕ Java — Backend con Hibernate + PostgreSQL + iText

### Dependencias Maven
1. `hibernate-core:6.4.4.Final` — ORM para mapeo objeto-relacional
2. `postgresql:42.7.3` — Driver JDBC para PostgreSQL
3. `itextpdf:5.5.13.3` *(adicional)* — Generación de reportes PDF con datos reales

### Arquitectura
```
Repository<T, ID>          ← Interfaz genérica (abstracción)
    └── GenericRepositoryImpl<T, ID>  ← Implementación genérica (reutilizable)
            └── ProductoRepository   ← Repositorio específico con queries propias
            └── UsuarioRepository    ← findByEmail, findAllActivos
            └── PedidoRepository     ← findByEstado, calcularTotalVentas
```

### Ejecución
```bash
cd java
mvn clean package
java -jar target/crocheterias-maho-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 🎨 Diseño

- **Tipografías:** Chewy (display/headings) + Quicksand (cuerpo)
- **Paleta:** Tonos tierra (#8B5E3C, #C4895A) + Rosa palo (#C2748A, #D99BAD)
- **Usuarios:** Admin (`admin` / `maho2025`) | Cliente (`maryam` / `crochet123`)
- **Responsive:** Mobile-first, adaptable a todos los tamaños

---

*Proyecto académico — CESDE Bello 2025 · Hecho con 🧶 y mucho amor por Maryam*
