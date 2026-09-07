const API_BASE = 'http://localhost:8000/api';

let funnelChart = null;
let trendsChart = null;

document.addEventListener('DOMContentLoaded', () => {
    initFilters();
    loadFunnel();
    loadDailyTrends();
    loadParticipants();

    document.getElementById('btn-apply').addEventListener('click', () => {
        const st = document.getElementById('filter-stakeholder').value;
        const reg = document.getElementById('filter-region').value;
        const ch = document.getElementById('filter-channel').value;

        loadFunnel(st, reg, ch);
        loadDailyTrends(st, reg, ch);
    });
});

async function initFilters() {
    try {
        const res = await fetch(`${API_BASE}/participants?page=0&size=1000`);
        const data = await res.json();
        
        const stakeholders = new Set();
        const regions = new Set();
        const channels = new Set();

        data.items.forEach(p => {
            if (p.stakeholderType) stakeholders.add(p.stakeholderType);
            if (p.region) regions.add(p.region);
            if (p.channel) channels.add(p.channel);
        });

        populateSelect('filter-stakeholder', Array.from(stakeholders));
        populateSelect('filter-region', Array.from(regions));
        populateSelect('filter-channel', Array.from(channels));
    } catch (err) {
        console.error("Errore nel caricamento dei filtri:", err);
    }
}

function populateSelect(id, values) {
    const select = document.getElementById(id);
    values.sort().forEach(val => {
        const opt = document.createElement('option');
        opt.value = val;
        opt.textContent = val;
        select.appendChild(opt);
    });
}

async function loadFunnel(st = '', reg = '', ch = '') {
    const url = `${API_BASE}/analytics/funnel?stakeholder=${encodeURIComponent(st)}&region=${encodeURIComponent(reg)}&channel=${encodeURIComponent(ch)}`;
    const res = await fetch(url);
    const data = await res.json();

    const labels = data.map(item => item.stage);
    const counts = data.map(item => item.count);

    if (funnelChart) funnelChart.destroy();

    const ctx = document.getElementById('chart-funnel').getContext('2d');
    funnelChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Partecipanti',
                data: counts,
                backgroundColor: '#2563eb'
            }]
        },
        options: { 
            responsive: true,
            plugins: { legend: { display: false } }
        }
    });
}

async function loadDailyTrends(st = '', reg = '', ch = '') {
    const url = `${API_BASE}/analytics/daily-trends?stakeholder=${encodeURIComponent(st)}&region=${encodeURIComponent(reg)}&channel=${encodeURIComponent(ch)}`;
    const res = await fetch(url);
    const data = await res.json();

    const labels = data.map(item => item.date);
    const visitors = data.map(item => item.standVisitors);

    if (trendsChart) trendsChart.destroy();

    const ctx = document.getElementById('chart-trends').getContext('2d');
    trendsChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Visitatori Stand',
                data: visitors,
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                fill: true,
                tension: 0.3
            }]
        },
        options: { responsive: true }
    });
}

async function loadParticipants() {
    const res = await fetch(`${API_BASE}/participants?page=0&size=10`);
    const data = await res.json();

    const tbody = document.getElementById('table-participants');
    tbody.innerHTML = '';

    data.items.forEach(p => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${p.originalId}</td>
            <td>${p.fullName}</td>
            <td>${p.email}</td>
            <td>${p.stakeholderType}</td>
            <td>${p.region}</td>
            <td>${p.channel}</td>
        `;
        tbody.appendChild(row);
    });
}