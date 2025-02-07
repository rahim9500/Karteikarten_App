src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM"
crossorigin="anonymous"

document.getElementById('registerForm').addEventListener('submit', function(event) {
    var password = document.getElementById('password').value;
    var error = document.getElementById('passwordError');

    var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d-.,+*]{8,12}$/;

    if (!passwordRegex.test(password)) {
        error.style.display = 'block';
        event.preventDefault();
    } else {
        error.style.display = 'none';
    }
});

document.getElementById('togglePassword').addEventListener('click', function (e) {

    const password = document.getElementById('password');
    const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
    password.setAttribute('type', type);


    this.textContent = this.textContent === '\u{1F441}' ? '\u{1F576}' : '\u{1F441}';
});
