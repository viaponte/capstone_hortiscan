document.addEventListener('DOMContentLoaded', function () {
    var modal = document.getElementById("modalOpciones");
    var guardarBtn = document.getElementById("guardarBtn");
    var closeModal = document.getElementById("closeModal");

    var modalCorreo = document.getElementById("modalCorreo");
    var openEmailModal = document.getElementById("openEmailModal");
    var closeEmailModal = document.getElementById("closeEmailModal");

    var backToOptions = document.getElementById("backToOptions");

    // Mostrar el modal al hacer clic en "Guardar"
    guardarBtn.onclick = function() {
        modal.style.display = "block";
    }

    // Cerrar el modal al hacer clic en la "X"
    closeModal.onclick = function() {
        modal.style.display = "none";
    }

    // Mostrar el segundo modal (correo electrónico) al hacer clic en "Enviar por Correo"
    openEmailModal.onclick = function() {
        modalOpciones.style.display = "none";
        modalCorreo.style.display = "block";
    }

    // Cerrar el segundo modal al hacer clic en la "X"
    closeEmailModal.onclick = function() {
        modalCorreo.style.display = "none";
    }

    // Volver al primer modal al hacer clic en la flecha
    backToOptions.onclick = function() {
        modalCorreo.style.display = "none";
        modalOpciones.style.display = "block";
    }

});