// ===================================================
// auth.js - Lógica de autenticación
// Crocheterias Maho - Maryam - CESDE Bello
// Cumple: Momento 1 (lógica consola) + Momento 2 (DOM)
// ===================================================

// === MOMENTO 1: Credenciales hardcodeadas ===
const ADMIN_USUARIO = "admin";
const ADMIN_PASSWORD = "maho2025";
const USER_USUARIO = "maryam";
const USER_PASSWORD = "crochet123";

// Estado de sesión
let intentosRestantes = 3;
let sesionActual = null;

// ============================================
// MOMENTO 1: Función de validación (consola)
// ============================================
const validarAcceso = () => {
  const USUARIO_CORRECTO = USER_USUARIO;
  const PASSWORD_CORRECTA = USER_PASSWORD;
  let intentos = 0;
  const MAX_INTENTOS = 3;

  while (intentos < MAX_INTENTOS) {
    const usuario = prompt("Ingresa tu usuario:");
    const contrasena = prompt("Ingresa tu contraseña:");

    if (usuario === USUARIO_CORRECTO && contrasena === PASSWORD_CORRECTA) {
      console.log("¡Bienvenida al sistema, Maryam! 🧶");
      return true;
    } else {
      intentos++;
      if (intentos < MAX_INTENTOS) {
        console.log(`Datos incorrectos. Intento ${intentos} de ${MAX_INTENTOS}.`);
      } else {
        console.log("Usuario bloqueado. Ha superado el número de intentos.");
        return false;
      }
    }
  }
};

// ============================================
// MOMENTO 2: Login con DOM
// ============================================

function openLogin() {
  intentosRestantes = 3;
  actualizarDots();
  document.getElementById('login-modal').classList.add('open');
  document.getElementById('login-user').value = '';
  document.getElementById('login-pass').value = '';
  ocultarAlerta();
  setTimeout(() => document.getElementById('login-user').focus(), 300);
}

function closeLogin() {
  document.getElementById('login-modal').classList.remove('open');
}

function actualizarDots() {
  const usados = 3 - intentosRestantes;
  for (let i = 1; i <= 3; i++) {
    const dot = document.getElementById(`dot${i}`);
    if (dot) {
      dot.className = 'attempt-dot' + (i <= usados ? ' used' : '');
    }
  }
  const txt = document.getElementById('attempts-text');
  if (txt) {
    txt.textContent = intentosRestantes > 0
      ? `${intentosRestantes} intento${intentosRestantes !== 1 ? 's' : ''} disponible${intentosRestantes !== 1 ? 's' : ''}`
      : 'Cuenta bloqueada';
  }
}

function mostrarAlerta(mensaje, tipo = 'error') {
  const alert = document.getElementById('login-alert');
  if (!alert) return;
  alert.className = `alert show alert-${tipo}`;
  alert.textContent = mensaje;
}

function ocultarAlerta() {
  const alert = document.getElementById('login-alert');
  if (alert) alert.className = 'alert';
}

// Función principal de intento de login (DOM)
function intentarLogin() {
  if (intentosRestantes <= 0) {
    mostrarAlerta('Cuenta bloqueada. Recarga la página para intentar de nuevo.', 'error');
    return;
  }

  // Leer valores del DOM
  const usuarioInput = document.getElementById('login-user').value.trim();
  const passwordInput = document.getElementById('login-pass').value;

  if (!usuarioInput || !passwordInput) {
    mostrarAlerta('Por favor completa todos los campos.', 'error');
    return;
  }

  // Validar credenciales
  let rolUsuario = null;

  if (usuarioInput === ADMIN_USUARIO && passwordInput === ADMIN_PASSWORD) {
    rolUsuario = 'admin';
  } else if (usuarioInput === USER_USUARIO && passwordInput === USER_PASSWORD) {
    rolUsuario = 'user';
  } else {
    // Verificar contra MockAPI
    verificarContraAPI(usuarioInput, passwordInput);
    return;
  }

  // Login exitoso
  loginExitoso(usuarioInput, rolUsuario);
}

async function verificarContraAPI(usuario, password) {
  try {
    const response = await fetch(`https://67f14b2b94bde1c1252d31d2.mockapi.io/api/v1/usuarios`);
    const usuarios = await response.json();
    const encontrado = usuarios.find(u =>
      u.email === usuario && u.password === password && u.activo
    );
    if (encontrado) {
      loginExitoso(encontrado.nombre, encontrado.rol || 'user', encontrado);
    } else {
      loginFallido();
    }
  } catch(e) {
    loginFallido();
  }
}

function loginExitoso(nombre, rol, datosUsuario = null) {
  sesionActual = {
    nombre: nombre,
    rol: rol,
    datos: datosUsuario,
    timestamp: Date.now()
  };
  sessionStorage.setItem('sesion', JSON.stringify(sesionActual));

  mostrarAlerta(`¡Bienvenida, ${nombre}! 🧶`, 'success');
  console.log(`¡Bienvenida al sistema, ${nombre}!`);

  actualizarNavbar();
  showToast(`¡Hola ${nombre}! Bienvenida a Crocheterias Maho 🧶`, 'success');

  setTimeout(() => {
    closeLogin();
    if (rol === 'admin') {
      window.location.href = 'html/admin.html';
    }
  }, 1200);
}

function loginFallido() {
  intentosRestantes--;
  actualizarDots();

  if (intentosRestantes > 0) {
    mostrarAlerta(`Datos incorrectos. Intento ${3 - intentosRestantes} de 3.`, 'error');
    console.log(`Datos incorrectos. Intento ${3 - intentosRestantes} de 3.`);
    document.getElementById('login-pass').value = '';
    document.getElementById('login-pass').focus();
  } else {
    mostrarAlerta('Usuario bloqueado. Ha superado el número de intentos.', 'error');
    console.log('Usuario bloqueado. Ha superado el número de intentos.');
    document.getElementById('login-user').disabled = true;
    document.getElementById('login-pass').disabled = true;
  }
}

function cerrarSesion() {
  sesionActual = null;
  sessionStorage.removeItem('sesion');
  actualizarNavbar();
  showToast('Sesión cerrada. ¡Hasta pronto! 👋', 'success');
  if (window.location.pathname.includes('admin') || window.location.pathname.includes('dashboard')) {
    window.location.href = '../index.html';
  }
}

function actualizarNavbar() {
  const sesion = getSesionActual();
  const loginBtn = document.getElementById('nav-login-btn');
  const userBtn = document.getElementById('nav-user-btn');

  if (!loginBtn) return;

  if (sesion) {
    loginBtn.style.display = 'none';
    if (userBtn) {
      userBtn.style.display = 'inline-flex';
      userBtn.textContent = `👤 ${sesion.nombre}`;
      userBtn.href = sesion.rol === 'admin' ? 'html/admin.html' : 'html/dashboard.html';
    }
  } else {
    loginBtn.style.display = 'inline-flex';
    if (userBtn) userBtn.style.display = 'none';
  }
}

function getSesionActual() {
  if (sesionActual) return sesionActual;
  const stored = sessionStorage.getItem('sesion');
  if (stored) {
    sesionActual = JSON.parse(stored);
    return sesionActual;
  }
  return null;
}

function requireAuth(rol = null) {
  const sesion = getSesionActual();
  if (!sesion) {
    openLogin();
    return false;
  }
  if (rol && sesion.rol !== rol) {
    showToast('No tienes permisos para esta sección.', 'error');
    return false;
  }
  return true;
}

// Enter en inputs del modal
document.addEventListener('DOMContentLoaded', () => {
  ['login-user', 'login-pass'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.addEventListener('keydown', e => { if (e.key === 'Enter') intentarLogin(); });
  });

  // Cerrar modal al hacer click fuera
  const overlay = document.getElementById('login-modal');
  if (overlay) {
    overlay.addEventListener('click', e => { if (e.target === overlay) closeLogin(); });
  }
});

// ============================================
// Toast helper
// ============================================
function showToast(msg, tipo = 'success') {
  const toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = msg;
  toast.className = `toast show ${tipo}`;
  setTimeout(() => toast.className = 'toast', 3000);
}
