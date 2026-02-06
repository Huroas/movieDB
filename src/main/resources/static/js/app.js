const api = "/api/movies";
const pageState = {
    page: 0,
    size: 10,
    totalPages: 1,
    mode: "all",
    criteria: {}
};
const reviewPageSize = 5;
const reviewState = {};

function authHeaders() {
    return window.authHeader ? { Authorization: window.authHeader } : {};
}

function setValue(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.value = value;
    }
}

function normalize(value) {
    const trimmed = (value || "").toString().trim();
    return trimmed.length ? trimmed : null;
}

function toInt(value) {
    const parsed = parseInt(value, 10);
    return Number.isNaN(parsed) ? null : parsed;
}

function buildQuery(params) {
    const entries = Object.entries(params)
        .filter(([, value]) => value !== null && value !== undefined && value !== "");
    if (entries.length === 0) {
        return "";
    }

    return "?" + entries
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        .join("&");
}

async function fetchPage(endpoint, params) {
    const url = `${endpoint}${buildQuery(params)}`;
    const res = await fetch(url, { headers: { ...authHeaders() } });
    if (res.ok) {
        return res.json();
    }
    await showResponseError(res);
    return null;
}

async function showResponseError(res) {
    const text = await res.text();
    if (!text) {
        alert(`Request failed (${res.status})`);
        return;
    }

    try {
        const data = JSON.parse(text);
        if (data && typeof data === "object") {
            let message = "";
            for (let key in data) {
                message += `${key}: ${data[key]}\n`;
            }
            alert(message || `Request failed (${res.status})`);
            return;
        }
    } catch (err) {
        // fall through
    }

    alert(text);
}

function getReviewState(movieId) {
    if (!reviewState[movieId]) {
        reviewState[movieId] = { page: 0, totalPages: 1, open: false };
    }
    return reviewState[movieId];
}

function setReviewsOpen(movieId, open) {
    const body = document.getElementById(`reviews-${movieId}`);
    const arrow = document.getElementById(`reviewsArrow-${movieId}`);
    if (!body) {
        return;
    }

    if (open) {
        body.classList.remove("hidden");
        if (arrow) {
            arrow.innerHTML = "&#9660;";
        }
    } else {
        body.classList.add("hidden");
        if (arrow) {
            arrow.innerHTML = "&#9654;";
        }
    }
}

function renderMovies(movies) {
    if (!Array.isArray(movies)) {
        alert("Unexpected response from server.");
        return;
    }

    const list = document.getElementById("movie-list");
    list.innerHTML = "";

    if (movies.length === 0) {
        list.innerHTML = "<div class=\"empty\">No movies found.</div>";
        return;
    }

    movies.forEach(m => {
        const div = document.createElement("div");
        div.className = "movie";

        div.innerHTML = `
            <div>
                <span class="arrow" onclick="toggleDetails('d${m.movieId}')">&#9660;</span>
                <strong>${m.title}</strong> (${m.releaseYear || "N/A"})
            </div>

            <div id="d${m.movieId}" class="details">
                <div class="details-grid">
                    <div class="details-left">
                        <div class="field-row">
                            <label for="title${m.movieId}">Title</label>
                            <input value="${m.title || ""}" id="title${m.movieId}">
                        </div>
                        <div class="field-row">
                            <label for="genre${m.movieId}">Genre</label>
                            <input value="${m.genre || ""}" id="genre${m.movieId}">
                        </div>
                        <div class="field-row">
                            <label for="releaseYear${m.movieId}">Release Year</label>
                            <input type="number" value="${m.releaseYear || ""}" id="releaseYear${m.movieId}">
                        </div>
                        <div class="field-row">
                            <label for="director${m.movieId}">Director</label>
                            <input value="${m.director || ""}" id="director${m.movieId}">
                        </div>
                        <div class="field-row">
                            <label for="rating${m.movieId}">Rating (1-10)</label>
                            <input type="number" value="${m.rating || ""}" id="rating${m.movieId}">
                        </div>
                        <div class="details-actions">
                            <button onclick="updateMovie('${m.movieId}')">Update</button>
                            <button onclick="deleteMovie('${m.movieId}')" class="btn-danger">Delete</button>
                        </div>
                    </div>
                    <div class="details-right">
                        <label>Your review:<br>
                            <textarea id="review${m.movieId}" rows="6" placeholder="Write your review"></textarea>
                        </label><br>
                        <button onclick="saveReview('${m.movieId}')">Save Review</button>
                        <div class="reviews-section">
                            <div class="reviews-header" onclick="toggleReviews('${m.movieId}')">
                                <span id="reviewsArrow-${m.movieId}" class="arrow">&#9654;</span>
                                <strong>Reviews</strong>
                            </div>
                            <div id="reviews-${m.movieId}" class="reviews-body hidden">
                                <div id="reviewsList-${m.movieId}"></div>
                                <div class="reviews-pagination">
                                    <button id="reviewsPrev-${m.movieId}" onclick="prevReviewPage('${m.movieId}')">Prev</button>
                                    <span id="reviewsPageLabel-${m.movieId}">1 / 1</span>
                                    <button id="reviewsNext-${m.movieId}" onclick="nextReviewPage('${m.movieId}')">Next</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        list.appendChild(div);
    });
}

async function loadReviews(movieId, page) {
    const data = await fetchPage(`${api}/${movieId}/reviews`, {
        page,
        size: reviewPageSize
    });
    if (!data) {
        return;
    }

    const state = getReviewState(movieId);
    state.page = data.page;
    state.totalPages = data.totalPages;
    renderReviews(movieId, data);
}

function renderReviews(movieId, pageData) {
    const list = document.getElementById(`reviewsList-${movieId}`);
    if (!list) {
        return;
    }

    list.innerHTML = "";

    if (!pageData.items || pageData.items.length === 0) {
        list.innerHTML = "<div class=\"empty\">No reviews yet.</div>";
        updateReviewPagination(movieId, pageData);
        return;
    }

    pageData.items.forEach(review => {
        const item = document.createElement("div");
        item.className = "review-item";

        const meta = document.createElement("div");
        meta.className = "review-meta";
        meta.textContent = review.username;

        const text = document.createElement("div");
        text.className = "review-text";
        text.textContent = review.review;

        item.append(meta, text);
        list.appendChild(item);
    });

    updateReviewPagination(movieId, pageData);
}

function updateReviewPagination(movieId, pageData) {
    const prevBtn = document.getElementById(`reviewsPrev-${movieId}`);
    const nextBtn = document.getElementById(`reviewsNext-${movieId}`);
    const label = document.getElementById(`reviewsPageLabel-${movieId}`);

    if (prevBtn) {
        prevBtn.disabled = pageData.page <= 0;
    }
    if (nextBtn) {
        nextBtn.disabled = pageData.totalPages === 0 || pageData.page >= pageData.totalPages - 1;
    }
    if (label) {
        const total = Math.max(pageData.totalPages, 1);
        const current = Math.min(pageData.page + 1, total);
        label.textContent = `${current} / ${total}`;
    }
}

function toggleReviews(movieId) {
    const state = getReviewState(movieId);
    state.open = !state.open;
    setReviewsOpen(movieId, state.open);
    if (state.open) {
        loadReviews(movieId, state.page);
    }
}

function prevReviewPage(movieId) {
    const state = getReviewState(movieId);
    if (state.page > 0) {
        loadReviews(movieId, state.page - 1);
    }
}

function nextReviewPage(movieId) {
    const state = getReviewState(movieId);
    if (state.page < state.totalPages - 1) {
        loadReviews(movieId, state.page + 1);
    }
}

function renderPage(pageData) {
    if (!pageData || !Array.isArray(pageData.items)) {
        alert("Unexpected response from server.");
        return;
    }

    pageState.page = pageData.page;
    pageState.size = pageData.size;
    pageState.totalPages = pageData.totalPages;

    renderMovies(pageData.items);
    updatePagination();
}

function updatePagination() {
    const prevBtn = document.getElementById("prevPageBtn");
    const nextBtn = document.getElementById("nextPageBtn");
    const label = document.getElementById("pageLabel");

    if (prevBtn) {
        prevBtn.disabled = pageState.page <= 0;
    }
    if (nextBtn) {
        nextBtn.disabled = pageState.totalPages === 0 || pageState.page >= pageState.totalPages - 1;
    }
    if (label) {
        const total = Math.max(pageState.totalPages, 1);
        const current = Math.min(pageState.page + 1, total);
        label.textContent = `${current} / ${total}`;
    }
}

async function loadAllPage(page) {
    const data = await fetchPage(api, { page, size: pageState.size });
    if (data) {
        pageState.mode = "all";
        renderPage(data);
    }
}

async function loadMovies() {
    pageState.mode = "all";
    pageState.criteria = {};
    setValue("searchId", "");
    setValue("searchTitle", "");
    setValue("searchGenre", "");
    setValue("searchYear", "");
    setValue("searchRating", "");
    await loadAllPage(0);
}

async function loadSearchPage(page) {
    const params = {
        ...pageState.criteria,
        page,
        size: pageState.size
    };

    const data = await fetchPage(`${api}/search`, params);
    if (data) {
        pageState.mode = "search";
        renderPage(data);
    }
}

async function performSearch() {
    const criteria = {
        id: normalize(document.getElementById("searchId")?.value),
        title: normalize(document.getElementById("searchTitle")?.value),
        genre: normalize(document.getElementById("searchGenre")?.value),
        releaseYear: toInt(document.getElementById("searchYear")?.value),
        rating: toInt(document.getElementById("searchRating")?.value)
    };

    if (!criteria.id && !criteria.title && !criteria.genre && criteria.releaseYear === null && criteria.rating === null) {
        return loadMovies();
    }

    pageState.criteria = criteria;
    await loadSearchPage(0);
}

function clearSearch() {
    setValue("searchId", "");
    setValue("searchTitle", "");
    setValue("searchGenre", "");
    setValue("searchYear", "");
    setValue("searchRating", "");
    loadMovies();
}

function prevPage() {
    goToPage(pageState.page - 1);
}

function nextPage() {
    goToPage(pageState.page + 1);
}

async function goToPage(page) {
    if (page < 0) {
        return;
    }
    if (pageState.totalPages !== 0 && page >= pageState.totalPages) {
        return;
    }

    if (pageState.mode === "search") {
        await loadSearchPage(page);
    } else {
        await loadAllPage(page);
    }
}

async function refreshPage() {
    if (pageState.mode === "search") {
        await loadSearchPage(pageState.page);
    } else {
        await loadAllPage(pageState.page);
    }
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
        await showResponseError(res);
        return;
    }

    hideAddForm();
    await refreshPage();
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
        await showResponseError(res);
        return;
    }

    await refreshPage();
}

async function deleteMovie(id) {
    const res = await fetch(`${api}/${id}`, {
        method: "DELETE",
        headers: { ...authHeaders() }
    });

    if (!res.ok) {
        await showResponseError(res);
        return;
    }

    await refreshPage();
}

async function saveReview(id) {
    const input = document.getElementById("review" + id);
    if (!input) {
        return;
    }

    const review = input.value.trim();
    if (!review) {
        alert("Review is missing");
        return;
    }

    const res = await fetch(`${api}/${id}/reviews`, {
        method: "POST",
        headers: { "Content-Type": "application/json", ...authHeaders() },
        body: JSON.stringify({ review })
    });

    if (!res.ok) {
        await showResponseError(res);
        return;
    }

    const data = await res.json();
    if (data && data.review) {
        input.value = data.review;
    }
    const state = getReviewState(id);
    state.page = 0;
    state.open = true;
    setReviewsOpen(id, true);
    await loadReviews(id, 0);
    alert("Review saved.");
}

document.addEventListener("DOMContentLoaded", loadMovies);
