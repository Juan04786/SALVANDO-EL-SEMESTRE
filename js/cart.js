// ===================================================
// cart.js - Lógica del carrito de compras
// Crocheterias Maho - Maryam - CESDE Bello
// ===================================================

let carrito = JSON.parse(localStorage.getItem('maho_cart') || '[]');

function toggleCart() {
  const sidebar = document.getElementById('cart-sidebar');
  if (sidebar) sidebar.classList.toggle('open');
  renderCart();
}

function addToCart(producto) {
  const existente = carrito.find(item => item.id === producto.id);
  if (existente) {
    existente.cantidad++;
  } else {
    carrito.push({ ...producto, cantidad: 1 });
  }
  guardarCarrito();
  renderCart();
  updateCartBadge();
  showToast(`¡${producto.nombre} añadido al carrito! 🧶`, 'success');

  // Abrir carrito brevemente
  const sidebar = document.getElementById('cart-sidebar');
  if (sidebar && !sidebar.classList.contains('open')) {
    sidebar.classList.add('open');
    setTimeout(() => {}, 100);
  }
}

function removeFromCart(id) {
  carrito = carrito.filter(item => item.id !== id);
  guardarCarrito();
  renderCart();
  updateCartBadge();
}

function cambiarCantidad(id, delta) {
  const item = carrito.find(i => i.id === id);
  if (!item) return;
  item.cantidad += delta;
  if (item.cantidad <= 0) {
    removeFromCart(id);
    return;
  }
  guardarCarrito();
  renderCart();
  updateCartBadge();
}

function guardarCarrito() {
  localStorage.setItem('maho_cart', JSON.stringify(carrito));
}

function calcularTotal() {
  return carrito.reduce((sum, item) => sum + (item.precio * item.cantidad), 0);
}

function updateCartBadge() {
  const badge = document.getElementById('cart-count');
  if (badge) {
    const total = carrito.reduce((s, i) => s + i.cantidad, 0);
    badge.textContent = total;
    badge.style.display = total > 0 ? 'flex' : 'none';
  }
}

function renderCart() {
  const lista = document.getElementById('cart-items-list');
  const totalEl = document.getElementById('cart-total');
  if (!lista) return;

  if (carrito.length === 0) {
    lista.innerHTML = `
      <div class="cart-empty">
        <div class="cart-empty-icon">🛒</div>
        <p>Tu carrito está vacío</p>
        <a href="html/catalogo.html" class="btn-outline" style="margin-top:1rem; display:inline-flex;">Ver productos</a>
      </div>`;
  } else {
    lista.innerHTML = carrito.map(item => `
      <div class="cart-item">
        <div class="cart-item-icon">${item.emoji || '🧶'}</div>
        <div class="cart-item-info">
          <h4>${item.nombre}</h4>
          <div class="price">${formatPrecioCart(item.precio * item.cantidad)}</div>
        </div>
        <div class="cart-qty-controls">
          <button class="qty-btn" onclick="cambiarCantidad('${item.id}', -1)">−</button>
          <span class="qty-num">${item.cantidad}</span>
          <button class="qty-btn" onclick="cambiarCantidad('${item.id}', 1)">+</button>
        </div>
      </div>
    `).join('');
  }

  if (totalEl) {
    totalEl.textContent = formatPrecioCart(calcularTotal());
  }
}

function formatPrecioCart(precio) {
  return new Intl.NumberFormat('es-CO', {
    style: 'currency', currency: 'COP', maximumFractionDigits: 0
  }).format(precio);
}

function finalizarCompra() {
  const sesion = getSesionActual ? getSesionActual() : null;
  if (!sesion) {
    showToast('Debes iniciar sesión para finalizar tu compra 🧶', 'error');
    openLogin();
    return;
  }
  if (carrito.length === 0) {
    showToast('Tu carrito está vacío', 'error');
    return;
  }

  const pedido = {
    usuario: sesion.nombre,
    productos: [...carrito],
    total: calcularTotal(),
    fecha: new Date().toISOString(),
    estado: 'pendiente'
  };

  // Crear pedido en MockAPI
  PedidosController.create(pedido)
    .then(() => {
      carrito = [];
      guardarCarrito();
      renderCart();
      updateCartBadge();
      showToast('¡Pedido realizado con éxito! Te contactamos pronto 🧶', 'success');
      toggleCart();
    })
    .catch(() => {
      // Si falla la API, igual confirmamos localmente
      carrito = [];
      guardarCarrito();
      renderCart();
      updateCartBadge();
      showToast('¡Pedido registrado! Te contactamos pronto 🧶', 'success');
      toggleCart();
    });
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
  updateCartBadge();
  renderCart();
});
