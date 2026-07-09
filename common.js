/**
 * ============================================================
 * COMMON.JS - Shared JavaScript for KKK Kar Wash
 * All pages import this file for consistent functionality
 * ============================================================
 */

// ====== USER MANAGEMENT ======

function getUser() {
    try {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    } catch (e) {
        return null;
    }
}

function setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
}

function isLoggedIn() {
    return getUser() !== null;
}

function isAdmin() {
    const user = getUser();
    return user && user.role === 'admin';
}

function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    updateNav();
    window.location.href = 'index.html';
}

// ====== NAVIGATION ======

function updateNav() {
    const user = getUser();
    const isLoggedIn = user !== null;
    const isAdminUser = isLoggedIn && user.role === 'admin';

    const navLogin = document.getElementById('navLogin');
    const navRegister = document.getElementById('navRegister');
    const navBookings = document.getElementById('navBookings');
    const navProfile = document.getElementById('navProfile');
    const navAdmin = document.getElementById('navAdmin');
    const navLogout = document.getElementById('navLogout');
    const userBadge = document.getElementById('userBadge');

    if (navLogin) navLogin.classList.toggle('d-none', isLoggedIn);
    if (navRegister) navRegister.classList.toggle('d-none', isLoggedIn);
    if (navBookings) navBookings.classList.toggle('d-none', !isLoggedIn || isAdminUser);
    if (navProfile) navProfile.classList.toggle('d-none', !isLoggedIn);
    if (navAdmin) navAdmin.classList.toggle('d-none', !isAdminUser);
    if (navLogout) navLogout.classList.toggle('d-none', !isLoggedIn);

    if (userBadge && isLoggedIn) {
        userBadge.innerHTML = `
            <span class="badge px-3 py-2" style="background:rgba(56,189,248,0.12);color:#7dd3fc;border-radius:40px;border:1px solid rgba(56,189,248,0.08);">
                <i class="bi bi-person-circle me-1"></i> ${user.name}
            </span>
        `;
        userBadge.classList.remove('d-none');
    } else if (userBadge) {
        userBadge.classList.add('d-none');
    }
}

// ====== TOAST NOTIFICATIONS ======

function showToast(message, type = 'success') {
    const toastContainer = document.createElement('div');
    toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    toastContainer.style.zIndex = '9999';

    const iconMap = {
        success: 'bi-check-circle-fill',
        danger: 'bi-exclamation-triangle-fill',
        info: 'bi-info-circle-fill',
        warning: 'bi-exclamation-circle-fill'
    };
    const colorMap = {
        success: '#34d399',
        danger: '#f87171',
        info: '#38bdf8',
        warning: '#fbbf24'
    };

    const icon = iconMap[type] || iconMap.info;
    const color = colorMap[type] || colorMap.info;

    toastContainer.innerHTML = `
        <div class="toast show align-items-center toast-glass" role="alert" style="border-left: 4px solid ${color};">
            <div class="d-flex p-3">
                <i class="bi ${icon}" style="color:${color};font-size:1.2rem;margin-right:12px;"></i>
                <div class="toast-body" style="color:rgba(255,255,255,0.9);">${message}</div>
                <button type="button" class="btn-close btn-close-white ms-3" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;

    document.body.appendChild(toastContainer);
    setTimeout(() => {
        if (toastContainer.parentNode) toastContainer.remove();
    }, 3500);
}

// ====== FORM HELPERS ======

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidPassword(password) {
    return password && password.length >= 6;
}

function getFormData(form) {
    const formData = new FormData(form);
    const data = {};
    for (let [key, value] of formData.entries()) {
        data[key] = value;
    }
    return data;
}

// ====== DATE HELPERS ======

function formatDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function getToday() {
    return new Date().toISOString().split('T')[0];
}

function getFutureDate(days = 1) {
    const date = new Date();
    date.setDate(date.getDate() + days);
    return date.toISOString().split('T')[0];
}

// ====== INITIALIZATION ======

function initCommon() {
    updateNav();

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            logout();
        });
    }

    const nav = document.getElementById('mainNav');
    if (nav) {
        window.addEventListener('scroll', function() {
            if (window.scrollY > 50) {
                nav.classList.add('scrolled');
            } else {
                nav.classList.remove('scrolled');
            }
        });
    }
}

// ====== EXPOSE GLOBALLY ======
window.getUser = getUser;
window.setUser = setUser;
window.isLoggedIn = isLoggedIn;
window.isAdmin = isAdmin;
window.logout = logout;
window.updateNav = updateNav;
window.showToast = showToast;
window.isValidEmail = isValidEmail;
window.isValidPassword = isValidPassword;
window.getFormData = getFormData;
window.formatDate = formatDate;
window.getToday = getToday;
window.getFutureDate = getFutureDate;
window.initCommon = initCommon;

// ====== AUTO-INIT ======
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCommon);
} else {
    initCommon();
}