// Selecciona los elementos del DOM
const openModalButton = document.getElementById('openModalButton');
const closeModalButton = document.getElementById('closeModalButton');
const modal = document.getElementById('folderModal');

// Función para abrir el modal
openModalButton.addEventListener('click', function() {
    modal.style.display = 'block';
});

// Función para cerrar el modal
closeModalButton.addEventListener('click', function() {
    modal.style.display = 'none';
});