// Meridian ERP — shared front-end helpers
(function () {
    'use strict';

    // Confirm destructive actions (forms / links with data-confirm)
    document.addEventListener('submit', function (e) {
        const msg = e.target.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) {
            e.preventDefault();
        }
    });

    document.addEventListener('click', function (e) {
        const el = e.target.closest('[data-confirm-link]');
        if (el && !window.confirm(el.getAttribute('data-confirm-link'))) {
            e.preventDefault();
        }
    });

    // Show a loading spinner on submit buttons
    document.addEventListener('submit', function (e) {
        const btn = e.target.querySelector('button[type="submit"]');
        if (btn && !btn.disabled && !e.defaultPrevented) {
            btn.dataset.label = btn.innerHTML;
            btn.disabled = true;
            btn.innerHTML = '<span class="inline-block w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin align-middle"></span>';
            // Re-enable shortly in case validation blocks navigation
            setTimeout(function () {
                if (btn.dataset.label) { btn.innerHTML = btn.dataset.label; btn.disabled = false; }
            }, 4000);
        }
    });
})();
