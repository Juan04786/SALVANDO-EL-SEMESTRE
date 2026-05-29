// =============================================
// api.js - Momento 3: Consumo de API con fetch
// Crocheterias Maho - CESDE Bello 2025
// =============================================

const API_URL = "https://6a1908ef489e4715751963bc.mockapi.io/productos";

// 1. Función asíncrona que consume la API
async function obtenerProductos() {
  const response = await fetch(API_URL);
  const data = await response.json();
  return data;
}

// 2. Función que actualiza el DOM
async function cargarProductos() {
  const contenedor = document.getElementById("productos-container");

  // Manejo de carga
  contenedor.innerHTML = `
    <div class="cargando">
      <span>🧶</span> Cargando productos...
    </div>
  `;

  try {
    const productos = await obtenerProductos();

    contenedor.innerHTML = ""; // Limpia el "Cargando..."

    productos.forEach((producto) => {
      const card = document.createElement("div");
      card.className = "producto-card";

      card.innerHTML = `
        <div class="producto-categoria">${producto.categoria}</div>
        <h3 class="producto-nombre">${producto.nombre}</h3>
        <p class="producto-precio">$${Number(producto.precio).toLocaleString("es-CO")}</p>
        <button class="btn-agregar" onclick="agregarAlCarrito('${producto.nombre}', ${producto.precio})">
          Agregar al carrito
        </button>
      `;

      contenedor.appendChild(card);
    });

  } catch (error) {
    // Manejo de errores
    contenedor.innerHTML = `
      <div class="error-msg">
        ⚠️ No se pudieron cargar los productos. Intenta más tarde.
      </div>
    `;
  }
}

// 3. Función carrito básico
function agregarAlCarrito(nombre, precio) {
  alert(`✅ "${nombre}" agregado al carrito por $${Number(precio).toLocaleString("es-CO")}`);
}

// 4. Cargar productos al iniciar la página
document.addEventListener("DOMContentLoaded", cargarProductos);