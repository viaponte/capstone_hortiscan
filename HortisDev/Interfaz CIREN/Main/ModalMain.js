// Obtener elementos
var modal = document.getElementById("folderModal");
var openModalButton = document.getElementById("openModalButton");
var closeModalButton = document.getElementById("closeModalButton");

// Abrir el modal
openModalButton.onclick = function() {
    modal.style.display = "block";
}

// Cerrar el modal al hacer clic en el botón "X"
closeModalButton.onclick = function() {
    modal.style.display = "none";
}
