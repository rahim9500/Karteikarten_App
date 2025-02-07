document.addEventListener('DOMContentLoaded', function () {
    const card = document.querySelector('.card');
    card.addEventListener('click', () => {
        card.classList.toggle('flipped');
    });
});