// ===================================================
// api.js - Consumo de MockAPI con fetch (Momento 3)
// Crocheterias Maho - Maryam - CESDE Bello
// ===================================================

const BASE_URL = 'https://67f14b2b94bde1c1252d31d2.mockapi.io/api/v1';

// ====================================================
// PRODUCTOS - Controlador completo (CRUD)
// ====================================================
const ProductosController = {
  async getAll() {
    const res = await fetch(`${BASE_URL}/productos`);
    if (!res.ok) throw new Error('Error al obtener productos');
    return res.json();
  },

  async getById(id) {
    const res = await fetch(`${BASE_URL}/productos/${id}`);
    if (!res.ok) throw new Error('Producto no encontrado');
    return res.json();
  },

  async create(producto) {
    const res = await fetch(`${BASE_URL}/productos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(producto)
    });
    if (!res.ok) throw new Error('Error al crear producto');
    return res.json();
  },

  async update(id, producto) {
    const res = await fetch(`${BASE_URL}/productos/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(producto)
    });
    if (!res.ok) throw new Error('Error al actualizar producto');
    return res.json();
  },

  async delete(id) {
    const res = await fetch(`${BASE_URL}/productos/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Error al eliminar producto');
    return res.json();
  }
};

// ====================================================
// USUARIOS - Controlador completo (CRUD)
// ====================================================
const UsuariosController = {
  async getAll() {
    const res = await fetch(`${BASE_URL}/usuarios`);
    if (!res.ok) throw new Error('Error al obtener usuarios');
    return res.json();
  },

  async getById(id) {
    const res = await fetch(`${BASE_URL}/usuarios/${id}`);
    if (!res.ok) throw new Error('Usuario no encontrado');
    return res.json();
  },

  async create(usuario) {
    const res = await fetch(`${BASE_URL}/usuarios`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(usuario)
    });
    if (!res.ok) throw new Error('Error al crear usuario');
    return res.json();
  },

  async update(id, datos) {
    const res = await fetch(`${BASE_URL}/usuarios/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos)
    });
    if (!res.ok) throw new Error('Error al actualizar usuario');
    return res.json();
  },

  async delete(id) {
    const res = await fetch(`${BASE_URL}/usuarios/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Error al eliminar usuario');
    return res.json();
  }
};

// ====================================================
// PEDIDOS - Controlador completo (CRUD)
// ====================================================
const PedidosController = {
  async getAll() {
    const res = await fetch(`${BASE_URL}/pedidos`);
    if (!res.ok) throw new Error('Error al obtener pedidos');
    return res.json();
  },

  async getById(id) {
    const res = await fetch(`${BASE_URL}/pedidos/${id}`);
    if (!res.ok) throw new Error('Pedido no encontrado');
    return res.json();
  },

  async create(pedido) {
    const res = await fetch(`${BASE_URL}/pedidos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(pedido)
    });
    if (!res.ok) throw new Error('Error al crear pedido');
    return res.json();
  },

  async update(id, datos) {
    const res = await fetch(`${BASE_URL}/pedidos/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos)
    });
    if (!res.ok) throw new Error('Error al actualizar pedido');
    return res.json();
  },

  async delete(id) {
    const res = await fetch(`${BASE_URL}/pedidos/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Error al eliminar pedido');
    return res.json();
  }
};

// ====================================================
// RESEÑAS - Controlador completo (CRUD)
// ====================================================
const ResenasController = {
  async getAll() {
    const res = await fetch(`${BASE_URL}/resenas`);
    if (!res.ok) throw new Error('Error al obtener reseñas');
    return res.json();
  },

  async create(resena) {
    const res = await fetch(`${BASE_URL}/resenas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(resena)
    });
    if (!res.ok) throw new Error('Error al crear reseña');
    return res.json();
  }
};

// ====================================================
// CATEGORÍAS - Controlador completo (CRUD)
// ====================================================
const CategoriasController = {
  async getAll() {
    const res = await fetch(`${BASE_URL}/categorias`);
    if (!res.ok) throw new Error('Error al obtener categorías');
    return res.json();
  }
};

// ====================================================
// FUNCIONES DE ALTO NIVEL (usadas en las páginas)
// ====================================================

// Momento 3: función async con manejo de carga y errores
async function obtenerProductos() {
  mostrarCargando('featured-products');
  try {
    const productos = await ProductosController.getAll();
    return productos.length > 0 ? productos : getProductosMock();
  } catch(e) {
    console.warn('MockAPI no disponible, usando datos locales:', e.message);
    return getProductosMock();
  }
}

async function obtenerUsuarios() {
  try {
    return await UsuariosController.getAll();
  } catch(e) {
    console.warn('Error al cargar usuarios:', e.message);
    return [];
  }
}

async function obtenerPedidos() {
  try {
    return await PedidosController.getAll();
  } catch(e) {
    console.warn('Error al cargar pedidos:', e.message);
    return [];
  }
}

// ====================================================
// DATOS MOCK (fallback cuando MockAPI no está activo)
// ====================================================
function getProductosMock() {
  return [
    {
      id: '1',
      nombre: 'Amigurumi Osito Personalizado',
      descripcion: 'Pequeño osito tejido a crochet con colores y detalles a tu gusto. Perfecto para regalar.',
      precio: 45000,
      categoria: 'amigurumis',
      emoji: '🐻',
      disponible: true,
      stock: 5
    },
    {
      id: '2',
      nombre: 'Ramo de Rosas Tejidas',
      descripcion: 'Hermoso ramo de flores tejidas que duran para siempre. Ideal para bodas y aniversarios.',
      precio: 75000,
      categoria: 'ramos',
      emoji: '🌹',
      disponible: true,
      stock: 3
    },
    {
      id: '3',
      nombre: 'Llavero Personalizado',
      descripcion: 'Llavero tejido con tu inicial o diseño favorito. Colores a elección.',
      precio: 18000,
      categoria: 'llaveros',
      emoji: '🗝️',
      disponible: true,
      stock: 20
    },
    {
      id: '4',
      nombre: 'Vestido Tejido Personalizado',
      descripcion: 'Prenda tejida a medida con los colores y estilo que prefieras. Talla a elección.',
      precio: 180000,
      categoria: 'prendas',
      emoji: '👗',
      disponible: true,
      stock: 2
    },
    {
      id: '5',
      nombre: 'Amigurumi Unicornio',
      descripcion: 'Unicornio mágico tejido con hilo multicolor y detalles brillantes. ¡Único en su clase!',
      precio: 55000,
      categoria: 'amigurumis',
      emoji: '🦄',
      disponible: true,
      stock: 4
    },
    {
      id: '6',
      nombre: 'Bolso Tejido Artesanal',
      descripcion: 'Bolso de crochet hecho con hilo resistente. Varios colores disponibles.',
      precio: 120000,
      categoria: 'otros',
      emoji: '👜',
      disponible: true,
      stock: 3
    },
    {
      id: '7',
      nombre: 'Ramo de Girasoles Tejidos',
      descripcion: 'Girasoles eternos tejidos a crochet. Nunca se marchitan y siempre alegran.',
      precio: 65000,
      categoria: 'ramos',
      emoji: '🌻',
      disponible: true,
      stock: 6
    },
    {
      id: '8',
      nombre: 'Top de Crochet',
      descripcion: 'Top tejido estilo boho, perfecto para el verano. Personalizable en color y talla.',
      precio: 95000,
      categoria: 'prendas',
      emoji: '👚',
      disponible: true,
      stock: 4
    }
  ];
}

function getUsuariosMock() {
  return [
    { id: '1', nombre: 'Maryam H.', email: 'maryam@maho.co', rol: 'admin', activo: true, fechaRegistro: '2024-01-15' },
    { id: '2', nombre: 'Laura M.', email: 'laura@gmail.com', rol: 'user', activo: true, fechaRegistro: '2024-03-20' },
    { id: '3', nombre: 'Carolina V.', email: 'carolina@gmail.com', rol: 'user', activo: true, fechaRegistro: '2024-05-10' },
    { id: '4', nombre: 'Daniela R.', email: 'daniela@gmail.com', rol: 'user', activo: false, fechaRegistro: '2024-07-01' },
  ];
}

function getPedidosMock() {
  return [
    { id: 'PED-001', usuario: 'Laura M.', producto: 'Amigurumi Osito', total: 45000, estado: 'entregado', fecha: '2025-06-10' },
    { id: 'PED-002', usuario: 'Carolina V.', producto: 'Ramo de Rosas', total: 75000, estado: 'en_proceso', fecha: '2025-06-15' },
    { id: 'PED-003', usuario: 'Daniela R.', producto: 'Llavero x3', total: 54000, estado: 'pendiente', fecha: '2025-06-18' },
  ];
}

// ====================================================
// HELPERS DE UI
// ====================================================
function mostrarCargando(contenedorId) {
  const el = document.getElementById(contenedorId);
  if (el) el.innerHTML = '<div class="loading"><div class="spinner"></div> Cargando...</div>';
}

function formatPrecio(precio) {
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(precio);
}

function renderProductCard(p) {
  return `
    <div class="product-card" data-id="${p.id}">
      <div class="product-img">${p.emoji || '🧶'}</div>
      <div class="product-body">
        <span class="product-tag">${p.categoria}</span>
        <h3 class="product-name">${p.nombre}</h3>
        <p class="product-desc">${p.descripcion}</p>
        <div class="product-footer">
          <span class="product-price">${formatPrecio(p.precio)}</span>
          <button class="btn-add-cart" onclick="addToCart(${JSON.stringify(p).replace(/"/g, '&quot;')})">+ Agregar</button>
        </div>
      </div>
    </div>`;
}
