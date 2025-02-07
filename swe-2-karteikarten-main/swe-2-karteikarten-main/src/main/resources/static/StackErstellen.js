document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('stackForm').addEventListener('submit', createStack);
});

function fetchFlashcards() {
    fetch('/api/flashcards')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(flashcards => {
            const list = document.getElementById('flashcardList');
            list.innerHTML = '';
            flashcards.forEach(flashcard => {
                const li = document.createElement('li');
                li.className = 'list-group-item';
                li.innerHTML = `<input type="checkbox" class="form-check-input" id="flashcard-${flashcard.id}" value="${flashcard.id}"> <label for="flashcard-${flashcard.id}">${flashcard.title}</label>`;
                list.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Error fetching flashcards:', error);
            alert('Fehler beim Laden der Flashcards. Bitte versuchen Sie es erneut.');
        });
}

function saveSelectedFlashcards() {
    const selectedItems = document.querySelectorAll('#flashcardList input[type="checkbox"]:checked');
    const selectedList = document.getElementById('selectedFlashcards');
    selectedList.innerHTML = '';
    selectedItems.forEach(item => {
        const li = document.createElement('li');
        li.className = 'list-group-item';
        li.textContent = item.nextElementSibling.textContent;
        li.dataset.id = item.value;
        selectedList.appendChild(li);
    });
    bootstrap.Modal.getInstance(document.getElementById('flashcardModal')).hide();
}

function createStack(event) {
    console.log('createStack function called'); // Debugging line
    event.preventDefault();
    const title = document.getElementById('stackTitle').value;
    const flashcardIds = Array.from(document.querySelectorAll('#selectedFlashcards .list-group-item')).map(li => li.dataset.id).join(',');

    let isPrivate = document.getElementById('privateRadio').checked;

    console.log(`title: ${title}, flashcardIds: ${flashcardIds}, isPrivate: ${isPrivate}`); // Debugging line

    fetch('/createStack', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `title=${encodeURIComponent(title)}&flashcardIdsString=${encodeURIComponent(flashcardIds)}&isPrivate=${isPrivate}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Server responded with status ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                alert('Stack erfolgreich erstellt!');
                window.location.href = '/startseite';
            } else {
                alert('Fehler beim Erstellen des Stacks');
            }
        })
        .catch(error => {
            console.error('Fehler beim Senden der Daten an das Backend:', error);
            alert('Ein Fehler ist aufgetreten. Bitte versuchen Sie es erneut.');
        });
}


document.getElementById('flashcardModal').addEventListener('show.bs.modal', function() {
    console.log("Modal wird angezeigt");
    fetchFlashcards();
});
