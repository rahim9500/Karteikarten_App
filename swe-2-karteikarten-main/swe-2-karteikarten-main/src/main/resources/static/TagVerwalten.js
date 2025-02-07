document.addEventListener('DOMContentLoaded', function() {
    setupDropdownEventListener();
    setupEditTagFormListener();
    setupDeleteTagFormListener();
    showTags();
});

document.getElementById('searchTerm').addEventListener('input', function() {
    if (this.value) {
        updateTagDropdown(this.value);
    } else {
        resetTagDropdown();
    }
});

function updateTagDropdown(searchTerm) {
    var userId = document.querySelector('input[name="userId"]').value;
    fetch('/api/filterTags?userId=' + userId + '&searchTerm=' + searchTerm)
        .then(response => response.json())
        .then(tags => {
            updateTagList(tags);
        })
        .catch(error => {
            console.error('Fehler beim Filtern der Tags:', error);
        });
}

function resetTagDropdown() {
    showTags();
}

function setupDropdownEventListener() {
    const dropdown = document.getElementById('tagDropdowns');
    dropdown.addEventListener('click', function(event) {
        if (event.target.matches('a')) {
            event.preventDefault();
            const tagName = event.target.getAttribute('data-tag-name');
            document.getElementById('tagName').value = tagName;
            updateDropdownButton(tagName);
        }
    });
}

function updateDropdownButton(tagName) {
    const dropdownToggle = document.querySelector('#deleteTagForm .dropdown-toggle');
    dropdownToggle.textContent = tagName;
}

function showTags() {
    var userId = document.querySelector('input[name="userId"]').value;
    fetch('/api/getTags?userId=' + userId)
        .then(response => response.json())
        .then(tags => {
            updateTagList(tags);
        })
        .catch(error => {
            console.error('Fehler beim Laden der Tags:', error);
        });
}

function updateTagList(tags) {
    var tagDropdown = document.getElementById('tagDropdowns');
    tagDropdown.innerHTML = '';
    tags.forEach(tag => {
        var a = document.createElement("a");
        a.className = 'dropdown-item';
        a.href = '#';
        a.dataset.tagName = tag.tagname;
        a.textContent = tag.tagname;
        tagDropdown.appendChild(a);
    });
}

function setupEditTagFormListener() {
    document.getElementById('editTagForm').addEventListener('submit', function(event) {
        if (!validateTagName('currentTagName') || !validateTagName('newTagName')) {
            event.preventDefault();
        }
    });
}

function setupDeleteTagFormListener() {
    document.querySelectorAll('#deleteTagForm #tagDropdowns a').forEach(item => {
        item.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();

            var tagName = this.getAttribute('data-tag-name');
            var dropdownToggle = document.querySelector('#deleteTagForm .dropdown-toggle');
            dropdownToggle.textContent = tagName;

            document.getElementById('tagName').value = tagName;
        });
    });
}

function validateTagName(tagId) {
    var tag = document.getElementById(tagId).value;
    var invalidChars = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/;
    if (invalidChars.test(tag)) {
        alert('Der eingegebene Tag-Name enthält ungültige Zeichen. Stellen Sie sicher, dass der Tag-Name keine Sonderzeichen enthält.');
        return false;
    }
    return true;
}

function toggleTagsList() {
    var tagsList = document.getElementById('tagsList');
    if (tagsList.style.display === 'block') {
        tagsList.style.display = 'none';
    } else {
        tagsList.style.display = 'block';
        displayTagsList();
    }
}

function displayTagsList() {
    var userId = document.querySelector('input[name="userId"]').value;
    fetch('/api/getTags?userId=' + userId)
        .then(response => response.json())
        .then(tags => {
            updateTagsListDisplay(tags);
        })
        .catch(error => {
            console.error('Fehler beim Laden der Tags:', error);
        });
}

function updateTagsListDisplay(tags) {
    var tagsList = document.getElementById('tagsList');
    tagsList.innerHTML = '';
    tags.forEach(tag => {
        var li = document.createElement("li");
        li.textContent = tag.tagname;
        tagsList.appendChild(li);
    });
}