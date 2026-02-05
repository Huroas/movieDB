const api = "/api/movies";

function authHeaders() {
    return window.authHeader ? { Authorization: window.authHeader } : {};
}

async function loadMovies() {
    const res = await fetch(api);
    const movies = await res.json();
    const list = document.getElementById("movie-list");
    list.innerHTML = "";
    document.getElementById("searchInput").value = "";

    movies.forEach(m => {
        const div = document.createElement("div");
        div.className = "movie";

        div.innerHTML = `
            <div>
                <span class="arrow" onclick="toggleDetails('d${m.movieId}')">&#9660;</span>
                <strong>${m.title}</strong> (${m.releaseYear || "N/A"})
            </div>

            <div id="d${m.movieId}" class="details">
                <label>Title: <input value="${m.title || ""}" id="title${m.movieId}"></label><br><br>
                <label>Genre: <input value="${m.genre || ""}" id="genre${m.movieId}"></label><br><br>
                <label>Release Year: <input type="number" value="${m.releaseYear || ""}" id="releaseYear${m.movieId}"></label><br><br>
                <label>Director: <input value="${m.director || ""}" id="director${m.movieId}"></label><br><br>
                <label>Rating (1-10): <input type="number" value="${m.rating || ""}" id="rating${m.movieId}"></label><br><br>

                <button onclick="updateMovie('${m.movieId}')">Update</button>
                <button onclick="deleteMovie('${m.movieId}')" class="btn-danger">Delete</button>
            </div>
        `;

        list.appendChild(div);
    });
}

function toggleDetails(id) {
    const el = document.getElementById(id);
    const style = window.getComputedStyle(el);
    if (style.display === "none") {
        el.style.display = "block";
    } else {
        el.style.display = "none";
    }
}

function showAddForm() {
    document.getElementById("addTitle").value = "";
    document.getElementById("addGenre").value = "";
    document.getElementById("addReleaseYear").value = "";
    document.getElementById("addDirector").value = "";
    document.getElementById("addRating").value = "";
    document.getElementById("addForm").style.display = "block";
}

function hideAddForm() {
    document.getElementById("addForm").style.display = "none";
}

async function login() {
    const username = prompt("Username:");
    const password = prompt("Password:");

    if (!username || !password) return alert("Login cancelled");

    window.authHeader = "Basic " + btoa(username + ":" + password);

    alert("Login successful!");
}

async function addMovie() {
    const movie = {
        title: document.getElementById("addTitle").value,
        genre: document.getElementById("addGenre").value,
        releaseYear: parseInt(document.getElementById("addReleaseYear").value) || null,
        director: document.getElementById("addDirector").value,
        rating: parseInt(document.getElementById("addRating").value) || null
    };

    const res = await fetch(api, {
        method: "POST",
        headers: { "Content-Type": "application/json", ...authHeaders() },
        body: JSON.stringify(movie)
    });

    if (!res.ok) {
        const errorData = await res.json();
        let message = "";
        for (let key in errorData) {
            message += `${key}: ${errorData[key]}\n`;
        }
        alert(message);
        return;
    }

    hideAddForm();
    loadMovies();
}

async function searchMovie() {
    const query = document.getElementById("searchInput").value.trim();
    if (!query) return loadMovies(); // jos tyhja, nayta kaikki

    const res = await fetch(`${api}/search?title=${encodeURIComponent(query)}`);
    const movies = await res.json();

    const list = document.getElementById("movie-list");
    list.innerHTML = "";

    movies.forEach(m => {
        const div = document.createElement("div");
        div.className = "movie";

        div.innerHTML = `
            <div>
                <span class="arrow" onclick="toggleDetails('d${m.movieId}')">&#9660;</span>
                <strong>${m.title}</strong> (${m.releaseYear || "N/A"})
            </div>

            <div id="d${m.movieId}" class="details">
                <label>Title: <input value="${m.title || ""}" id="title${m.movieId}"></label><br><br>
                <label>Genre: <input value="${m.genre || ""}" id="genre${m.movieId}"></label><br><br>
                <label>Release Year: <input type="number" value="${m.releaseYear || ""}" id="releaseYear${m.movieId}"></label><br><br>
                <label>Director: <input value="${m.director || ""}" id="director${m.movieId}"></label><br><br>
                <label>Rating (1-10): <input type="number" value="${m.rating || ""}" id="rating${m.movieId}"></label><br><br>

                <button onclick="updateMovie('${m.movieId}')">Update</button>
                <button onclick="deleteMovie('${m.movieId}')" class="btn-danger">Delete</button>
            </div>
        `;

        list.appendChild(div);
    });
}

async function updateMovie(id) {
    const movie = {
        title: document.getElementById("title" + id).value,
        genre: document.getElementById("genre" + id).value,
        releaseYear: parseInt(document.getElementById("releaseYear" + id).value) || null,
        director: document.getElementById("director" + id).value,
        rating: parseInt(document.getElementById("rating" + id).value) || null
    };

    const res = await fetch(`${api}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json", ...authHeaders() },
        body: JSON.stringify(movie)
    });

    if (!res.ok) {
        const errorData = await res.json();
        let message = "";
        for (let key in errorData) {
            message += `${key}: ${errorData[key]}\n`;
        }
        alert(message);
        return;
    }

    loadMovies();
}

async function deleteMovie(id) {
    await fetch(`${api}/${id}`, {
        method: "DELETE",
        headers: { ...authHeaders() }
    });
    loadMovies();
}

document.addEventListener("DOMContentLoaded", loadMovies);
