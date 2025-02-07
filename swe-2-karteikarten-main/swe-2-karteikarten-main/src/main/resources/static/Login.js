src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM"
crossOrigin="anonymous">




    document.getElementById('togglePassword').addEventListener('click', function (e) {

    const password = document.getElementById('password');
    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    password.setAttribute('type', type);
    this.textContent = this.textContent === '\u{1F441}' ? '\u{1F576}' : '\u{1F441}';
});


    setTimeout(function () {
    var successMessage = document.getElementById('successMessage');
    if (successMessage) {
    successMessage.style.display = 'none';
}
}, 3000);


