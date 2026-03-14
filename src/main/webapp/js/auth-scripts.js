document.addEventListener('DOMContentLoaded', function() {
    const passwordToggles = document.querySelectorAll('.password-toggle');

    passwordToggles.forEach(button => {
        button.addEventListener('click', function() {
            const input = this.previousElementSibling;
            const type = input.type === 'password' ? 'text' : 'password';
            input.type = type;
            this.textContent = type === 'password' ? '👁️' : '👁️‍🗨️';
        });
    });

    const inputs = document.querySelectorAll('input[required]');

    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });

        input.addEventListener('input', function() {
            if (this.classList.contains('error')) {
                validateField(this);
            }
        });
    });

    function validateField(field) {
        if (field.validity.valid && field.value.trim() !== '') {
            field.style.borderColor = '#48bb78';
            field.classList.remove('error');
        } else if (field.value.trim() !== '') {
            field.style.borderColor = '#fc8181';
            field.classList.add('error');
        } else {
            field.style.borderColor = '#e2e8f0';
            field.classList.remove('error');
        }
    }

    const authCard = document.querySelector('.auth-card');
    if (authCard) {
        authCard.style.opacity = '0';
        authCard.style.transform = 'translateY(20px)';

        setTimeout(() => {
            authCard.style.transition = 'all 0.5s ease-out';
            authCard.style.opacity = '1';
            authCard.style.transform = 'translateY(0)';
        }, 100);
    }
});