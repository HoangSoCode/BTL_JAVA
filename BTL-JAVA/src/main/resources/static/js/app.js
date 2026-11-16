/* FoodNear - giao diện tìm kiếm nhà hàng lân cận */
(function () {
    'use strict';

    const body = document.body;
    const DEFAULT_LOC = { lat: parseFloat(body.dataset.lat), lng: parseFloat(body.dataset.lng) };
    const DEFAULT_RADIUS = parseFloat(body.dataset.radius) || 5;
    const PAGE_SIZE = 9;

    const $ = (id) => document.getElementById(id);
    const els = {
        form: $('searchForm'), keyword: $('keyword'), radius: $('radiusKm'), radiusText: $('radiusText'),
        noRadius: $('noRadius'), minPrice: $('minPrice'), maxPrice: $('maxPrice'), cuisine: $('cuisine'),
        minRating: $('minRating'), sort: $('sort'), chips: $('priceChips'), locate: $('btnLocate'),
        locLabel: $('locationLabel'), reset: $('btnReset'), list: $('resultList'), summary: $('resultSummary'),
        pager: $('pagination'), alert: $('alertBox')
    };

    const state = { loc: { ...DEFAULT_LOC }, usingGps: false, page: 0 };
    const money = new Intl.NumberFormat('vi-VN');
    //const cuisineEmoji = {
    //    'Món Việt': '🍜', 'Lẩu': '🍲', 'Hải sản': '🦐', 'Món Hàn': '🥩', 'Món Nhật': '🍣',
    //    'Món Âu': '🍕', 'Món chay': '🥗', 'Cafe & Bánh': '🍰', 'Món Hoa': '🥟'
    //};

    /* ---------- Bản đồ ---------- */
    const map = L.map('map').setView([state.loc.lat, state.loc.lng], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19, attribution: '&copy; OpenStreetMap'
    }).addTo(map);
    const markerLayer = L.layerGroup().addTo(map);
    let userMarker = null, radiusCircle = null;

    function esc(s) {
        const d = document.createElement('div');
        d.textContent = s == null ? '' : String(s);
        return d.innerHTML;
    }

    function formatPrice(min, max) {
        return money.format(min) + 'đ – ' + money.format(max) + 'đ';
    }

    function formatDistance(km) {
        if (km == null) return '';
        return km < 1 ? Math.round(km * 1000) + ' m' : km.toFixed(1) + ' km';
    }

    /* ---------- Xây query ---------- */
    function buildQuery() {
        const p = new URLSearchParams();
        const add = (k, v) => { if (v !== '' && v != null) p.set(k, v); };
        add('keyword', els.keyword.value.trim());
        add('cuisine', els.cuisine.value);
        add('minPrice', els.minPrice.value);
        add('maxPrice', els.maxPrice.value);
        add('minRating', els.minRating.value);
        add('sort', els.sort.value);
        if (!els.noRadius.checked) add('radiusKm', els.radius.value);
        p.set('lat', state.loc.lat);
        p.set('lng', state.loc.lng);
        p.set('page', state.page);
        p.set('size', PAGE_SIZE);
        return p.toString();
    }

    /* ---------- Gọi API ---------- */
    async function search() {
        showAlert(null);
        renderSkeleton();
        try {
            const res = await fetch('/api/restaurants/search?' + buildQuery());
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || 'Lỗi không xác định');
            renderResults(data);
        } catch (e) {
            els.list.innerHTML = '';
            els.pager.innerHTML = '';
            els.summary.textContent = 'Không thể tải dữ liệu';
            showAlert(e.message);
        }
    }

    async function loadCuisines() {
        try {
            const res = await fetch('/api/restaurants/cuisines');
            const list = await res.json();
            list.forEach((c) => {
                const o = document.createElement('option');
                o.value = c; o.textContent = c;
                els.cuisine.appendChild(o);
            });
        } catch (_) { /* bỏ qua: bộ lọc ẩm thực là tuỳ chọn */ }
    }

    /* ---------- Render ---------- */
    function renderSkeleton() {
        els.list.innerHTML = '<div class="skeleton"></div>'.repeat(3);
    }

    function renderResults(data) {
        const total = data.totalElements;
        els.summary.textContent = total === 0
            ? 'Không tìm thấy nhà hàng phù hợp'
            : 'Tìm thấy ' + total + ' nhà hàng';

        if (total === 0) {
            els.list.innerHTML = '<div class="empty"><div style="font-size:2.5rem">🍽️</div>' +
                '<p>Hãy thử tăng khoảng cách hoặc nới rộng mức giá.</p></div>';
        } else {
            els.list.innerHTML = data.content.map(cardHtml).join('');
        }
        renderPagination(data);
        renderMap(data.content);
    }

    function cardHtml(r) {
        // Nếu API chưa có ảnh hoặc lỗi, dùng một ảnh mặc định dự phòng
        const image = r.imageUrl ? r.imageUrl : '/images/default-restaurant.png';
        
        return '<article class="card rest-card">' +
            '<div class="rest-cover">' +
                '<img src="' + image + '" alt="' + esc(r.name) + '" class="rest-image">' +
            '</div>' +
            '<div class="rest-body">' +
            '<h3 class="rest-title">' + esc(r.name) + '</h3>' +
            '<div class="rest-meta"><span class="badge">' + esc(r.cuisine) + '</span>' +
            '<span class="rating">★ ' + esc(r.rating) + '</span>' +
            '<span class="distance">📍 ' + formatDistance(r.distanceKm) + '</span></div>' +
            '<p class="rest-addr">' + esc(r.address) + '</p>' +
            '<div class="rest-foot"><span class="price">' + formatPrice(r.priceMin, r.priceMax) + '</span>' +
            '<a href="/restaurants/' + r.id + '">Chi tiết →</a></div>' +
            '</div></article>';
    }

    function renderPagination(data) {
        els.pager.innerHTML = '';
        if (data.totalPages <= 1) return;
        const mk = (label, page, opts = {}) => {
            const b = document.createElement('button');
            b.type = 'button'; b.textContent = label;
            if (opts.active) b.classList.add('active');
            if (opts.disabled) b.disabled = true;
            b.addEventListener('click', () => { state.page = page; search(); window.scrollTo({ top: 0, behavior: 'smooth' }); });
            els.pager.appendChild(b);
        };
        mk('‹', data.page - 1, { disabled: data.page === 0 });
        for (let i = 0; i < data.totalPages; i++) mk(String(i + 1), i, { active: i === data.page });
        mk('›', data.page + 1, { disabled: data.page >= data.totalPages - 1 });
    }

    function renderMap(items) {
        sessionStorage.setItem('user_lat', state.loc.lat);
        sessionStorage.setItem('user_lng', state.loc.lng);
        markerLayer.clearLayers();
        if (userMarker) map.removeLayer(userMarker);
        if (radiusCircle) map.removeLayer(radiusCircle);

        userMarker = L.circleMarker([state.loc.lat, state.loc.lng], {
            radius: 8, color: '#2563eb', fillColor: '#3b82f6', fillOpacity: .9
        }).addTo(map).bindTooltip('Vị trí của bạn');

        const bounds = [[state.loc.lat, state.loc.lng]];
        items.forEach((r) => {
            L.marker([r.latitude, r.longitude]).addTo(markerLayer)
                .bindPopup('<strong>' + esc(r.name) + '</strong><br>' + formatPrice(r.priceMin, r.priceMax) +
                    '<br>' + formatDistance(r.distanceKm) + '<br><a href="/restaurants/' + r.id + '">Xem chi tiết</a>');
            bounds.push([r.latitude, r.longitude]);
        });

        if (!els.noRadius.checked) {
            radiusCircle = L.circle([state.loc.lat, state.loc.lng], {
                radius: parseFloat(els.radius.value) * 1000, color: '#e8590c', weight: 1, fillOpacity: .05
            }).addTo(map);
        }
        if (bounds.length > 1) map.fitBounds(bounds, { padding: [30, 30], maxZoom: 15 });
        else map.setView([state.loc.lat, state.loc.lng], 14);
    }

    function showAlert(msg) {
        els.alert.hidden = !msg;
        els.alert.textContent = msg || '';
    }

    /* ---------- Sự kiện ---------- */
    function submit() { state.page = 0; search(); }

    els.form.addEventListener('submit', (e) => { e.preventDefault(); submit(); });
    els.sort.addEventListener('change', submit);

    els.radius.addEventListener('input', () => { els.radiusText.textContent = els.radius.value + ' km'; });
    els.radius.addEventListener('change', submit);
    els.noRadius.addEventListener('change', () => {
        els.radius.disabled = els.noRadius.checked;
        els.radiusText.textContent = els.noRadius.checked ? 'Không giới hạn' : els.radius.value + ' km';
        submit();
    });

    els.chips.addEventListener('click', (e) => {
        const chip = e.target.closest('.chip');
        if (!chip) return;
        els.minPrice.value = chip.dataset.min;
        els.maxPrice.value = chip.dataset.max;
        markChip();
        submit();
    });
    function markChip() {
        els.chips.querySelectorAll('.chip').forEach((c) => {
            c.classList.toggle('active', c.dataset.min === els.minPrice.value && c.dataset.max === els.maxPrice.value);
        });
    }
    [els.minPrice, els.maxPrice].forEach((i) => i.addEventListener('input', markChip));
    els.cuisine.addEventListener('change', submit);
    els.minRating.addEventListener('change', submit);

    let timer;
    els.keyword.addEventListener('input', () => { clearTimeout(timer); timer = setTimeout(submit, 400); });

    els.locate.addEventListener('click', () => {
        if (!navigator.geolocation) { showAlert('Trình duyệt không hỗ trợ định vị.'); return; }
        els.locLabel.textContent = 'Đang xác định vị trí...';
        navigator.geolocation.getCurrentPosition((pos) => {
            state.loc = { lat: pos.coords.latitude, lng: pos.coords.longitude };
            state.usingGps = true;
            els.locLabel.textContent = 'Đang dùng vị trí hiện tại của bạn';
            submit();
        }, () => {
            els.locLabel.textContent = 'Vị trí mặc định';
            showAlert('Không lấy được vị trí. Hãy cho phép truy cập vị trí trong trình duyệt.');
        }, { enableHighAccuracy: true, timeout: 10000 });
    });

    els.reset.addEventListener('click', () => {
        els.form.reset();
        els.radius.value = DEFAULT_RADIUS;
        els.radius.disabled = false;
        els.radiusText.textContent = DEFAULT_RADIUS + ' km';
        els.sort.value = 'distance';
        state.loc = { ...DEFAULT_LOC };
        state.usingGps = false;
        els.locLabel.textContent = 'Vị trí mặc định';
        markChip();
        submit();
    });

    /* ---------- Khởi tạo ---------- */
    els.radius.value = DEFAULT_RADIUS;
    els.radiusText.textContent = DEFAULT_RADIUS + ' km';
    markChip();
    loadCuisines();
    search();
})();
