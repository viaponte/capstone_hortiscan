describe('Pruebas de Registro de Usuario - Ataques Simulados', () => {
  beforeEach(() => {
    cy.visit('http://localhost:4200/registrarse');
  });

  it('Debería manejar entradas maliciosas correctamente', () => {
    const maliciousInputs = [
      "' OR '1'='1",
      "<script>alert('XSS');</script>",
      "; rm -rf /",
      "<img src='x' onerror='alert(\"XSS\")'>",
      "'; DROP TABLE usuarios; --"
    ];

    maliciousInputs.forEach((input) => {
      // Manipula directamente el DOM para saltarse validadores
      cy.window().then((win) => {
        const usernameInput = win.document.querySelector('#username');
        const passwordInput = win.document.querySelector('#password');
        const confirmPasswordInput = win.document.querySelector('#confirmPassword');

        // Asigna valores directamente
        usernameInput.value = input;
        usernameInput.dispatchEvent(new Event('input', { bubbles: true }));

        passwordInput.value = 'Password123!';
        passwordInput.dispatchEvent(new Event('input', { bubbles: true }));

        confirmPasswordInput.value = 'Password123!';
        confirmPasswordInput.dispatchEvent(new Event('input', { bubbles: true }));
      });

      // Intenta someter el formulario
      cy.get('form').submit();

      // Verifica que el formulario no permita continuar
      cy.url().should('include', '/registrarse'); // Aún en la página de registro
      cy.get('.text-danger').should('exist'); // Confirma que hay mensajes de error visibles
    });
  });

  it('Debería registrar un usuario válido', () => {
    cy.window().then((win) => {
      const usernameInput = win.document.querySelector('#username');
      const passwordInput = win.document.querySelector('#password');
      const confirmPasswordInput = win.document.querySelector('#confirmPassword');

      // Inserta datos válidos directamente
      usernameInput.value = 'usuariovalido';
      usernameInput.dispatchEvent(new Event('input', { bubbles: true }));

      passwordInput.value = 'Password123!';
      passwordInput.dispatchEvent(new Event('input', { bubbles: true }));

      confirmPasswordInput.value = 'Password123!';
      confirmPasswordInput.dispatchEvent(new Event('input', { bubbles: true }));
    });

    // Envía el formulario
    cy.get('form').submit();

    // Verifica que se redirige al inicio de sesión después del registro
    cy.url().should('include', '/login');
  });
});
