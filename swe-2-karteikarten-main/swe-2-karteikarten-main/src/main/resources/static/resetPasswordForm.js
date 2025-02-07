src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM"
crossorigin="anonymous"
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('newPasswordForm').addEventListener('submit', function(event) {
        var password = document.getElementById('newPassword').value;
        var confirmPassword = document.getElementById('confirmPassword').value;
        var error = document.getElementById('passwordError');

        var passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d-.,+*]{8,12}$/;

        if (!passwordRegex.test(password)) {
            error.style.display = 'block';
            event.preventDefault();
        } else {
            error.style.display = 'none';
            if (password !== confirmPassword) {
                alert("Die Passwörter stimmen nicht überein!");
                event.preventDefault();
            } else {
                alert("Passwort erfolgreich geändert!");
            }
        }
    });
});