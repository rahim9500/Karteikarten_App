document.addEventListener('DOMContentLoaded', function() {
    fetchTagsAndPopulateDropdown();
});

function fetchTagsAndPopulateDropdown() {
    fetch('/api/tags')
        .then(response => response.json())
        .then(tags => {
            const dropdownMenu = document.getElementById('tagDropdown');
            tags.forEach((tag, index) => {
                const radioInput = document.createElement('input');
                radioInput.type = 'radio';
                radioInput.id = 'tag' + index;
                radioInput.name = 'tags';
                radioInput.value = tag.tagname;
                radioInput.classList.add('form-check-input');
                radioInput.addEventListener('click', updateDisplayForSelectedTag);

                const label = document.createElement('label');
                label.htmlFor = 'tag' + index;
                label.classList.add('dropdown-item');
                label.appendChild(radioInput);
                label.append(tag.tagname);

                const listItem = document.createElement('li');
                listItem.appendChild(label);

                dropdownMenu.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error fetching tags:', error));
}

document.getElementById('flashcardForm').addEventListener('submit', updateSelectedTags);



function updateDisplayForSelectedTag(event) {
    if (!validateTagSelection()) {
        event.preventDefault();
        return false;
    }
    const dropdownButton = document.querySelector('.btn.btn-tertiary.dropdown-toggle');
    dropdownButton.textContent = event.target.value;
}

function updateSelectedTags(event) {
    if (!validateTagSelection()) {
        event.preventDefault();
        return false;
    }
    const selectedTags = [];
    document.querySelectorAll('#tagDropdown input[type="radio"]:checked').forEach(radio => {
        selectedTags.push(radio.value);
    });
    document.getElementById('tagIdsString').value = selectedTags.join(',');
}

function validateTagSelection() {
    const selectedRadio = document.querySelector('#tagDropdown input[type="radio"]:checked');
    if (!selectedRadio) {
        alert("Bitte w채hlen Sie mindestens einen Tag aus.");
        return false;
    }
    return true;
}

function validateTitle() {
    var title = document.getElementById('title').value;
    var invalidChars = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
    if (invalidChars.test(title)) {
        alert('Der eingegebene Titel enth채lt ung체ltige Zeichen. Stellen Sie sicher, dass der Titel keine Sonderzeichen enth채lt.');
        return false;
    }
    return true;
}

document.getElementById('flashcardForm').addEventListener('submit', function(event) {
    if (!validateTitle()) {
        event.preventDefault();
    }
    updateSelectedTags(event);
});
