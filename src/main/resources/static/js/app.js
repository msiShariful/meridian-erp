// Meridian ERP — shared front-end behaviour
(function () {
    'use strict';

    /* ---------- Lucide icons ---------- */
    function renderIcons() {
        if (window.lucide && typeof window.lucide.createIcons === 'function') {
            window.lucide.createIcons();
        }
    }
    document.addEventListener('DOMContentLoaded', renderIcons);

    /* ---------- Confirmation modal (replaces window.confirm) ---------- */
    const modal = document.getElementById('confirm-modal');
    let pendingAction = null; // function to run when confirmed

    function iconMarkup(name) {
        return '<i data-lucide="' + name + '" class="w-5 h-5"></i>';
    }

    function openConfirm(opts) {
        if (!modal) { // graceful fallback
            if (window.confirm(opts.message || 'Are you sure?')) opts.onConfirm();
            return;
        }
        const titleEl = document.getElementById('confirm-title');
        const msgEl = document.getElementById('confirm-message');
        const okBtn = document.getElementById('confirm-ok');
        const iconWrap = document.getElementById('confirm-icon');

        titleEl.textContent = opts.title || 'Are you sure?';
        msgEl.textContent = opts.message || 'This action cannot be undone.';

        const danger = opts.variant !== 'default';
        iconWrap.className = 'w-11 h-11 rounded-full flex items-center justify-center shrink-0 '
            + (danger ? 'bg-red-100 text-danger' : 'bg-indigo-100 text-brand');
        iconWrap.innerHTML = iconMarkup(danger ? 'alert-triangle' : 'help-circle');
        okBtn.textContent = opts.confirmLabel || (danger ? 'Delete' : 'Confirm');
        okBtn.className = 'px-4 py-2.5 rounded-xl text-white text-sm font-semibold transition shadow-sm '
            + (danger ? 'bg-danger hover:brightness-95' : 'bg-brand hover:bg-brand-dark');

        pendingAction = opts.onConfirm;
        modal.classList.remove('hidden');
        modal.classList.add('flex');
        renderIcons();
        okBtn.focus();
    }

    function closeConfirm() {
        if (!modal) return;
        modal.classList.add('hidden');
        modal.classList.remove('flex');
        pendingAction = null;
    }

    if (modal) {
        modal.addEventListener('click', function (e) {
            if (e.target.closest('[data-confirm-cancel]')) closeConfirm();
        });
        document.getElementById('confirm-ok').addEventListener('click', function () {
            const action = pendingAction;
            closeConfirm();
            if (typeof action === 'function') action();
        });
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && !modal.classList.contains('hidden')) closeConfirm();
        });
    }

    // Intercept form submissions that opt in via data-confirm
    document.addEventListener('submit', function (e) {
        const form = e.target;
        const msg = form.getAttribute('data-confirm');
        if (!msg || form.dataset.confirmed === 'true') return;
        e.preventDefault();
        openConfirm({
            title: form.getAttribute('data-confirm-title') || 'Please confirm',
            message: msg,
            confirmLabel: form.getAttribute('data-confirm-label'),
            variant: form.getAttribute('data-confirm-variant') || 'danger',
            onConfirm: function () {
                form.dataset.confirmed = 'true';
                form.submit(); // native submit bypasses this listener
            }
        });
    }, true);

    // Intercept links that opt in via data-confirm-link
    document.addEventListener('click', function (e) {
        const el = e.target.closest('[data-confirm-link]');
        if (!el) return;
        e.preventDefault();
        openConfirm({
            title: el.getAttribute('data-confirm-title') || 'Please confirm',
            message: el.getAttribute('data-confirm-link'),
            confirmLabel: el.getAttribute('data-confirm-label'),
            variant: el.getAttribute('data-confirm-variant') || 'danger',
            onConfirm: function () { window.location.href = el.href; }
        });
    });

    /* ---------- Submit button loading state ---------- */
    document.addEventListener('submit', function (e) {
        const form = e.target;
        if (e.defaultPrevented) return;
        if (form.getAttribute('data-confirm') && form.dataset.confirmed !== 'true') return;
        const btn = form.querySelector('button[type="submit"]');
        if (btn && !btn.disabled) {
            btn.dataset.label = btn.innerHTML;
            btn.disabled = true;
            btn.innerHTML = '<span class="inline-block w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin align-middle"></span>';
            setTimeout(function () {
                if (btn.dataset.label) { btn.innerHTML = btn.dataset.label; btn.disabled = false; }
            }, 5000);
        }
    });

    /* ---------- Auto-dismiss toasts ---------- */
    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('[data-toast]').forEach(function (t) {
            setTimeout(function () {
                t.style.transition = 'opacity .4s, transform .4s';
                t.style.opacity = '0';
                t.style.transform = 'translateX(20px)';
                setTimeout(function () { t.remove(); }, 400);
            }, 5000);
        });
    });
})();
