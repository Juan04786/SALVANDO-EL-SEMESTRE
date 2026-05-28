// ============================================================
// momento1_login.js
// Crocheterias Maho — Maryam — CESDE Bello 2025
//
// ENTREGABLE: Momento 1
// Ejecutar en la consola del navegador (F12 → Console)
// Simula un proceso de inicio de sesión con 3 intentos.
// ============================================================

// 1. CONFIGURACIÓN — credenciales hardcodeadas
const USUARIO_CORRECTO  = "maryam";
const PASSWORD_CORRECTA = "crochet123";

// 2. Función que encapsula toda la lógica (requisito de encapsulación)
const validarAcceso = () => {
    const MAX_INTENTOS = 3;
    let intentos = 0;
    let accesoConcedido = false;

    // 3. CICLO — controla los intentos (while como se recomienda)
    while (intentos < MAX_INTENTOS) {

        // 4. CAPTURA DE DATOS con prompt()
        const usuario    = prompt(`🧶 Crocheterias Maho\nIngresa tu usuario (intento ${intentos + 1} de ${MAX_INTENTOS}):`);
        const contrasena = prompt("Ingresa tu contraseña:");

        // Si el usuario cancela el prompt
        if (usuario === null || contrasena === null) {
            console.log("Inicio de sesión cancelado.");
            return false;
        }

        // 5. VALIDACIÓN con ===
        if (usuario === USUARIO_CORRECTO && contrasena === PASSWORD_CORRECTA) {
            // ÉXITO: ciclo termina, mensaje de bienvenida
            accesoConcedido = true;
            console.log(`✅ ¡Bienvenida al sistema, ${usuario}! 🧶`);
            console.log("Redirigiendo a Crocheterias Maho...");
            break; // termina el ciclo

        } else {
            // ERROR: incrementar intento e informar
            intentos++;

            if (intentos < MAX_INTENTOS) {
                console.log(`❌ Datos incorrectos. Intento ${intentos} de ${MAX_INTENTOS}.`);
            } else {
                // SE AGOTARON LOS INTENTOS
                console.log("🔒 Usuario bloqueado. Ha superado el número de intentos.");
            }
        }
    }

    return accesoConcedido;
};

// 6. LLAMADA A LA FUNCIÓN — el programa se ejecuta
validarAcceso();
