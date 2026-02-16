/**
 * Main Application Logic for Semantic Search
 */

// API Configuration
const API_BASE_URL = 'http://localhost:5000/api';

// Global state
let currentResults = [];
let currentMode = 'flag';
let currentView = 'grid';

/**
 * Tab Switching
 */
function initializeTabs() {
    const tabs = document.querySelectorAll('.tab-btn');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const mode = tab.dataset.mode;
            switchMode(mode);
        });
    });
}

function switchMode(mode) {
    currentMode = mode;

    // Update tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.mode === mode);
    });

    // Update search modes
    document.querySelectorAll('.search-mode').forEach(modeDiv => {
        modeDiv.classList.add('hidden');
    });
    document.getElementById(`mode-${mode}`).classList.remove('hidden');

    // Clear results
    clearResults();

    // specific mode actions
    if (mode === 'admin') {
        loadDetailedStats();
        checkHealth();
    }
}

/**
 * View Switching (Grid vs Mind Map)
 */
function initializeViewToggle() {
    const viewBtns = document.querySelectorAll('.view-btn');
    viewBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const view = btn.dataset.view;
            switchView(view);
        });
    });
}

function switchView(view) {
    currentView = view;

    // Update buttons
    document.querySelectorAll('.view-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.view === view);
    });

    try {
        var FLAG_flagn = "FEATURE_auth_enable_email_verification";
        if (FLAG_flagn) {
            console.log("Test flag value: " + FLAG_flagn);
        }

        var FLAG_anotherflag = "FEATURE_auth_enable_mobile_verification";
        if (FLAG_anotherflag) {
            console.log("Test flag value: " + FLAG_anotherflag);
        }
    }
    catch (e) { }


    // Update views
    if (view === 'grid') {
        document.getElementById('grid-view').classList.remove('hidden');
        document.getElementById('mindmap-view').classList.add('hidden');
    } else {
        document.getElementById('grid-view').classList.add('hidden');
        document.getElementById('mindmap-view').classList.remove('hidden');
        renderMindMap(currentResults);
    }
}

/**
 * Search Functions
 */
async function searchByFlag() {
    const flagName = document.getElementById('flag-search').value.trim();

    if (!flagName) {
        showError('Please enter a flag name');
        return;
    }

    showLoading(true);
    clearError();

    try {
        const response = await fetch(`${API_BASE_URL}/search/flag`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ flagName })
        });

        const data = await response.json();

        if (data.success) {
            currentResults = data.results;
            displayResults(data.results);
        } else {
            showError(data.error || 'Search failed');
        }
    } catch (error) {
        showError('Failed to connect to API: ' + error.message);
    } finally {
        showLoading(false);
    }
}

async function searchBySimilarity() {
    const codeFragment = document.getElementById('code-search').value.trim();

    if (!codeFragment) {
        showError('Please enter a code fragment');
        return;
    }

    showLoading(true);
    clearError();

    try {
        const response = await fetch(`${API_BASE_URL}/search/similarity`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ codeFragment })
        });

        const data = await response.json();

        if (data.success) {
            currentResults = data.results;
            displayResults(data.results);
        } else {
            showError(data.error || 'Search failed');
        }
    } catch (error) {
        showError('Failed to connect to API: ' + error.message);
    } finally {
        showLoading(false);
    }
}

async function searchNatural() {
    const query = document.getElementById('natural-search').value.trim();

    if (!query) {
        showError('Please enter a search query');
        return;
    }

    showLoading(true);
    clearError();

    try {
        const response = await fetch(`${API_BASE_URL}/search/natural`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ query })
        });

        const data = await response.json();

        if (data.success) {
            // Combine flags and code results
            const allResults = [
                ...data.flags.map(f => ({ ...f, type: 'flag' })),
                ...data.code.map(c => ({ ...c, type: 'code' }))
            ];
            currentResults = allResults;
            displayResults(allResults);
        } else {
            showError(data.error || 'Search failed');
        }
    } catch (error) {
        showError('Failed to connect to API: ' + error.message);
    } finally {
        showLoading(false);
    }
}

/**
 * Display Results
 */
function displayResults(results) {
    if (!results || results.length === 0) {
        showError('No results found');
        document.getElementById('results-container').classList.add('hidden');
        document.getElementById('view-toggle').classList.add('hidden');
        return;
    }

    // Show results container and view toggle
    document.getElementById('results-container').classList.remove('hidden');
    document.getElementById('view-toggle').classList.remove('hidden');

    // Update count
    document.getElementById('result-count').textContent = results.length;

    // Populate table
    const tbody = document.getElementById('results-tbody');
    tbody.innerHTML = '';

    results.forEach(result => {
        const row = document.createElement('tr');

        // Service Name
        const serviceCell = document.createElement('td');
        serviceCell.textContent = result.service_name || '-';
        row.appendChild(serviceCell);

        // File Path
        const fileCell = document.createElement('td');
        const filePath = result.file_path || '-';
        fileCell.textContent = truncatePath(filePath);
        fileCell.title = filePath;
        fileCell.className = 'file-path';
        row.appendChild(fileCell);

        // Line Number
        const lineCell = document.createElement('td');
        lineCell.textContent = result.line_number || '-';
        lineCell.className = 'line-number';
        row.appendChild(lineCell);

        // Code Snippet
        const codeCell = document.createElement('td');
        const code = result.code || result.flag_name || '-';
        codeCell.innerHTML = `<code>${escapeHtml(truncateText(code, 100))}</code>`;
        codeCell.className = 'code-snippet';
        row.appendChild(codeCell);

        // Score
        const scoreCell = document.createElement('td');
        const score = result.similarity_score || result.score;
        if (score !== undefined) {
            const percentage = Math.round(score * 100);
            scoreCell.innerHTML = `<span class="score-badge score-${getScoreClass(score)}">${percentage}%</span>`;
        } else {
            scoreCell.textContent = '-';
        }
        row.appendChild(scoreCell);

        tbody.appendChild(row);
    });
}

/**
 * Load Statistics
 */
async function loadStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/stats`);
        const data = await response.json();

        if (data.success && data.stats) {
            document.getElementById('stat-vectors').textContent = data.stats.total_vectors || 0;
            document.getElementById('stat-snippets').textContent = data.stats.code_snippets || 0;
            document.getElementById('stat-flags').textContent = data.stats.feature_flags || 0;
            document.getElementById('stat-services').textContent = data.stats.services || 0;
        }
    } catch (error) {
        console.error('Failed to load stats:', error);
    }
}

/**
 * Admin Functions
 */
async function checkHealth() {
    const indicator = document.getElementById('health-indicator');
    const text = document.getElementById('health-text');

    indicator.className = 'health-badge checking';
    text.textContent = 'Checking...';

    try {
        const response = await fetch(`${API_BASE_URL}/health`);
        const data = await response.json();

        if (data.success && data.status === 'healthy') {
            indicator.className = 'health-badge healthy';
            text.textContent = 'Operational';
        } else {
            throw new Error('Unhealthy status');
        }
    } catch (error) {
        indicator.className = 'health-badge error';
        text.textContent = 'Offline / Error';
    }
}

async function loadDetailedStats() {
    try {
        const response = await fetch(`${API_BASE_URL}/stats`);
        const data = await response.json();

        if (data.success && data.stats) {
            // Update cards
            document.getElementById('admin-vectors').textContent = data.stats.total_vectors || 0;
            document.getElementById('admin-snippets').textContent = data.stats.code_snippets || 0;
            document.getElementById('admin-flags').textContent = data.stats.feature_flags || 0;
            document.getElementById('admin-services').textContent = data.stats.services || 0;

            // Update raw view
            document.getElementById('raw-stats').textContent = JSON.stringify(data.stats, null, 2);
        }
    } catch (error) {
        document.getElementById('raw-stats').textContent = 'Error loading stats: ' + error.message;
    }
}

async function confirmRebuild() {
    if (confirm('Are you sure you want to rebuild the index? This may take several minutes and search will be unavailable during this time.')) {
        rebuildIndex();
    }
}

async function rebuildIndex() {
    showLoading(true);
    const btn = document.querySelector('.warning-btn');
    const originalText = btn.textContent;
    btn.textContent = '⏳ Rebuilding...';
    btn.disabled = true;

    try {
        const response = await fetch(`${API_BASE_URL}/index/rebuild`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ confirm: true })
        });

        const data = await response.json();

        if (data.success) {
            alert('Index rebuilt successfully!');
            loadDetailedStats();
            loadStats(); // Update footer
        } else {
            alert('Rebuild failed: ' + (data.error || 'Unknown error'));
        }
    } catch (error) {
        alert('Rebuild failed: ' + error.message);
    } finally {
        showLoading(false);
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

/**
 * UI Helper Functions
 */
function showLoading(show) {
    document.getElementById('loading').classList.toggle('hidden', !show);
}

function showError(message) {
    const errorDiv = document.getElementById('error');
    errorDiv.textContent = message;
    errorDiv.classList.remove('hidden');
}

function clearError() {
    document.getElementById('error').classList.add('hidden');
}

function clearResults() {
    currentResults = [];
    document.getElementById('results-container').classList.add('hidden');
    document.getElementById('view-toggle').classList.add('hidden');
    document.getElementById('results-tbody').innerHTML = '';
}

function truncatePath(path, maxLength = 50) {
    if (path.length <= maxLength) return path;
    const parts = path.split('\\');
    if (parts.length > 2) {
        return '...' + parts.slice(-2).join('\\');
    }
    return path.substring(0, maxLength) + '...';
}

function truncateText(text, maxLength = 100) {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function getScoreClass(score) {
    if (score >= 0.8) return 'high';
    if (score >= 0.5) return 'medium';
    return 'low';
}

/**
 * Keyboard Shortcuts
 */
document.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
        const activeMode = document.querySelector('.search-mode:not(.hidden)');
        if (activeMode) {
            if (currentMode === 'flag') searchByFlag();
            else if (currentMode === 'code' && !e.target.matches('textarea')) searchBySimilarity();
            else if (currentMode === 'natural') searchNatural();
        }
    }
});

/**
 * Initialize on page load
 */
document.addEventListener('DOMContentLoaded', () => {
    initializeTabs();
    initializeViewToggle();
    console.log('Semantic Search App initialized');
});
