let loggedInUser = document.getElementById('loggedInUser').textContent;

function confirmDeletion() {
    const stackCheckboxes = document.querySelectorAll('input[name="selectedStacks"]:checked');
    const flashcardCheckboxes = document.querySelectorAll('input[name="selectedCards"]:checked');

    const stackIds = Array.from(stackCheckboxes).map(cb => cb.value);
    const flashcardIds = Array.from(flashcardCheckboxes).map(cb => cb.value);

    console.log('Selected stack IDs:', stackIds);
    console.log('Selected flashcard IDs:', flashcardIds);

    if (stackIds.length === 0 && flashcardIds.length === 0) {
        alert("Bitte wählen Sie mindestens einen Stapel oder eine Karte aus, um zu löschen.");
        return;
    }

    const userConfirmed = window.confirm("Bist du dir sicher, dass du die ausgewählten Elemente löschen möchtest?");
    if (!userConfirmed) {
        return;
    }

    let bodyContent = new URLSearchParams();
    if (stackIds.length > 0) bodyContent.append('selectedStacks', stackIds.join(','));
    if (flashcardIds.length > 0) bodyContent.append('selectedFlashcards', flashcardIds.join(','));

    fetch('/deleteSelectedItems', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: bodyContent
    }).then(response => {
        if (response.ok) {
            alert("Ausgewählte Elemente wurden erfolgreich gelöscht.");
            window.location.reload();
        } else {
            response.text().then(text => alert(text));
        }
    }).catch(error => console.error('Error:', error));
}



document.addEventListener('DOMContentLoaded', function() {
    fetchTagsAndPopulateDropdown();
    document.getElementById('saveEditedStackBtn').addEventListener('click', saveEditedStack);
});

function fetchTagsAndPopulateDropdown() {
    fetch('/api/flashcard-tags')
        .then(response => response.json())
        .then(tags => {
            const dropdownMenu = document.getElementById('tagDropdown');
            tags.forEach(tag => {
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.id = 'tag' + tag.id;
                checkbox.name = 'tags';
                checkbox.value = tag.id;
                checkbox.classList.add('form-check-input');

                const label = document.createElement('label');
                label.htmlFor = 'tag' + tag.id;
                label.classList.add('dropdown-item');
                label.appendChild(checkbox);
                label.append(tag.title);

                const listItem = document.createElement('li');
                listItem.appendChild(label);

                dropdownMenu.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error fetching tags:', error));
}
document.getElementById('flashcardForm').addEventListener('submit', updateSelectedTags);

function filterFlashcardsByTags() {
    const selectedTagIds = Array.from(document.querySelectorAll('#tagDropdown input[type="checkbox"]:checked'))
        .map(checkbox => checkbox.value)
        .join(',');
    if (selectedTagIds.length > 0) {
        window.location.href = `/filterFlashcards?tags=${selectedTagIds}`;
    } else {
        alert("Bitte wählen Sie mindestens ein Tag aus, um zu filtern.");
    }

}

document.getElementById('searchInput').addEventListener('input', function() {
    const input = this.value.trim();
    if (input.length >= 2) {
        searchStacks();
    } else {
        document.getElementById('searchResults').innerHTML = '';
    }
});


function openEditStackModal(stackId) {
    fetch(`/api/flashcards`)
        .then(response => response.json())
        .then(allFlashcards => {
            fetch(`/api/stack/${encodeURIComponent(stackId)}/flashcards`)
                .then(response => response.json())
                .then(stackFlashcards => {
                    const list = document.getElementById('editFlashcardList');
                    list.innerHTML = '';

                    allFlashcards.forEach(flashcard => {
                        const li = document.createElement('li');
                        li.className = 'list-group-item';
                        const isChecked = stackFlashcards.some(f => f.id === flashcard.id);
                        li.innerHTML = `<input type="checkbox" class="form-check-input" id="edit-flashcard-${flashcard.id}" ${isChecked ? 'checked' : ''} value="${flashcard.id}"> <label for="edit-flashcard-${flashcard.id}">${flashcard.title}</label>`;
                        list.appendChild(li);
                    });

                    var editStackModal = new bootstrap.Modal(document.getElementById('editStackModal'));
                    editStackModal.show();

                    document.getElementById('saveEditedStackBtn').setAttribute('data-stack-id', stackId);
                });
        });
}

function saveEditedStack() {
    const stackId = document.getElementById('saveEditedStackBtn').getAttribute('data-stack-id');
    const selectedItems = document.querySelectorAll('#editFlashcardList input[type="checkbox"]:checked');
    const flashcardIds = Array.from(selectedItems).map(item => item.value);

    if (flashcardIds.length === 0) {
        alert('Bitte wählen Sie mindestens eine Karteikarte aus.');
        return;
    }

    fetch(`/api/updateStack/${encodeURIComponent(stackId)}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ flashcardIds: flashcardIds })
    })
        .then(response => {
            if (response.ok) {
                alert('Stack wurde erfolgreich aktualisiert!');
                window.location.reload();
            } else {
                alert('Fehler beim Aktualisieren des Stacks');
                console.error('Fehler beim Aktualisieren des Stacks:', response);
            }
        })
        .catch(error => {
            console.error('Fehler beim Aktualisieren des Stacks:', error);
        });
}


function loadPublicStacks() {
    fetch('/publicStacks')
        .then(response => {
            if (!response.ok) {
                throw new Error('Server responded with status ' + response.status);
            }
            return response.json();
        })
        .then(publicStacks => {
            console.log('Public Stacks:', publicStacks);
            const container = document.getElementById('publicStacksContainer');
            container.innerHTML = ''; // Löscht die vorherigen Inhalte
            publicStacks.forEach(stack => {
                const stackElement = document.createElement('div');
                stackElement.className = "col-md-3";
                stackElement.style.margin = "10px";
                stackElement.innerHTML = `
                    <div class="card text-center position-relative">
                        <a href="/stack/${stack.id}" class="card-body btn btn-secondary">
                            <h5>${stack.title}</h5>
                        </a>
                    </div>
                `;
                container.appendChild(stackElement);
            });
        })
        .catch(error => {
            console.error('Error fetching public stacks:', error);
        });
}


window.onload = function() {
    loadPublicStacks();
}


function searchStacks() {
    const input = document.getElementById('searchInput').value.trim();
    const searchType = document.getElementById('searchType').value;
    const endpoint = searchType === 'public' ? '/searchPublicStacks' : '/searchPrivateStacks';

    document.getElementById('mainContent').style.display = 'none'; // Hauptinhalt verbergen
    document.querySelectorAll('.stack-container').forEach(container => container.style.display = 'none');
    document.getElementById('publicStacksTitle').style.display = 'none';

    if (input.length >= 2) {
        fetch(`${endpoint}?query=${encodeURIComponent(input)}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                displayResults(data, searchType);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Fehler bei der Verarbeitung Ihrer Suchanfrage.');
                document.getElementById('searchResults').innerHTML = '<p>Keine Ergebnisse gefunden.</p>';
            });
    } else {
        document.getElementById('searchResults').innerHTML = '<p>Bitte geben Sie mindestens zwei Zeichen ein.</p>';
    }
    return false; // Verhindert das Neuladen der Seite
}

function displayResults(results, searchType) {
    const searchContainer = document.getElementById('searchResults');
    searchContainer.innerHTML = ''; // Bereinigen der vorherigen Suchergebnisse

    // Nur bei Ergebnissen andere Inhalte verbergen
    if (results.length === 0) {
        searchContainer.innerHTML = '<p>Keine Ergebnisse gefunden.</p>';
        return;
    }

    // Suchergebnisse anzeigen
    results.forEach(result => {
        const resultElement = document.createElement('div');
        resultElement.className = 'col-md-3';
        resultElement.style.margin = '10px';

        let controls = '';
        if (searchType === 'private') {
            controls = `
                <input type="checkbox" name="selectedStacks" value="${result.id}" class="form-check-input position-absolute top-0 end-0 mt-2 me-2">
                <button type="button" class="btn btn-sm btn-primary position-absolute top-0 end-0 mt-2 me-4" onclick="openEditStackModal('${result.id}')">
                    <i class="bi bi-pencil-square"></i>
                </button>`;
        }

        resultElement.innerHTML = `
            <div class="card text-center position-relative">
                ${controls}
                <a href="/stack/${result.id}" class="card-body btn btn-secondary">
                    <h5>${result.title}</h5>
                </a>
            </div>`;
        searchContainer.appendChild(resultElement);
    });
}



function filterFlashcardsAndStacksByTags() {
    const selectedTagIds = Array.from(document.querySelectorAll('#tagDropdown input[type="checkbox"]:checked'))
        .map(checkbox => checkbox.value)
        .join(',');
    if (selectedTagIds.length > 0) {
        window.location.href = `/filterFlashcardsAndStacks?tags=${selectedTagIds}`;
    } else {
        alert("Bitte wählen Sie mindestens ein Tag aus, um zu filtern.");
    }
}

function filterByTags() {
    const selectedTagIds = Array.from(document.querySelectorAll('#tagDropdown input[type="checkbox"]:checked'))
        .map(checkbox => checkbox.value)
        .join(',');
    if (selectedTagIds.length > 0) {
        filterFlashcards(selectedTagIds);
        filterStacks(selectedTagIds);
    } else {
        alert("Bitte wählen Sie mindestens ein Tag aus, um zu filtern.");
    }
}

function filterFlashcards(tagIds) {
    window.location.href = `/filterFlashcards?tags=${tagIds}`;
}

function filterStacks(tagIds) {
    window.location.href = `/filterStacks?tags=${tagIds}`;
}








