// ============== CORE FUNCTIONS ==============
function toggleReplyForm(commentId) {
    var form = document.getElementById('reply-form-' + commentId);
    if (form) {
        form.classList.toggle('show');
        if (form.classList.contains('show')) {
            var textarea = form.querySelector('textarea');
            if (textarea) textarea.focus();
        }
    }
}

function previewImages(input, previewId) {
    var preview = document.getElementById(previewId);
    if (!preview) return;
    preview.innerHTML = '';
    if (input.files) {
        for (var i = 0; i < input.files.length; i++) {
            var file = input.files[i];
            if (file.type.startsWith('image/')) {
                (function(f) {
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        var div = document.createElement('div');
                        div.className = 'position-relative';
                        div.innerHTML = '<img src="' + e.target.result + '" class="rounded" style="width:60px;height:60px;object-fit:cover;">';
                        preview.appendChild(div);
                    };
                    reader.readAsDataURL(f);
                })(file);
            }
        }
    }
}

function showToast(type, title, message, duration) {
    duration = duration || 5000;
    var container = document.getElementById('toastContainer');
    if (!container) return;
    var toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    var icons = { success: 'bi-check-circle-fill', error: 'bi-x-circle-fill', warning: 'bi-exclamation-triangle-fill', info: 'bi-info-circle-fill' };
    toast.innerHTML = '<div class="toast-icon"><i class="bi ' + (icons[type] || 'bi-info-circle-fill') + '"></i></div><div class="toast-content"><div class="toast-title">' + title + '</div><div class="toast-message">' + message + '</div></div><button class="toast-close" onclick="closeToast(this)"><i class="bi bi-x"></i></button>';
    container.appendChild(toast);
    setTimeout(function() { toast.classList.add('toast-exit'); setTimeout(function() { toast.remove(); }, 300); }, duration);
}

function closeToast(btn) { 
    var toast = btn.closest('.toast'); 
    if (toast) { toast.classList.add('toast-exit'); setTimeout(function() { toast.remove(); }, 300); }
}

function formatTimeAgo(d) {
    var s = Math.floor((new Date() - new Date(d)) / 1000);
    if (s < 60) return 'Vừa xong';
    if (s < 3600) return Math.floor(s / 60) + ' phút trước';
    if (s < 86400) return Math.floor(s / 3600) + ' giờ trước';
    if (s < 604800) return Math.floor(s / 86400) + ' ngày trước';
    return new Date(d).toLocaleDateString('vi-VN');
}

// ============== NOTIFICATION SYSTEM ==============
var allNotifications = [];
var currentFilter = 'all';

function loadNotificationCount() {
    fetch('/api/thong-bao/dem').then(function(r) { return r.json(); }).then(function(d) {
        var b = document.querySelector('.notification-count');
        if (b) { if (d.count > 0) { b.textContent = d.count > 99 ? '99+' : d.count; b.style.display = 'inline'; } else { b.style.display = 'none'; } }
    }).catch(function() {});
}

function loadNotifications() {
    fetch('/api/thong-bao/danh-sach').then(function(r) { return r.json(); }).then(function(d) { allNotifications = d; renderNotifications(); }).catch(function() {});
}

function renderNotifications() {
    var c = document.getElementById('notification-list');
    if (!c) return;
    var d = currentFilter === 'unread' ? allNotifications.filter(function(t) { return !(t.daDoc || t.dadoc); }) : allNotifications;
    if (d.length === 0) { c.innerHTML = '<div class="empty-notification"><i class="bi bi-inbox"></i><p>' + (currentFilter === 'unread' ? 'Không có thông báo chưa đọc' : 'Chưa có thông báo nào') + '</p></div>'; return; }
    c.innerHTML = d.map(function(t) {
        var title = t.tieuDe || 'Thông báo mới', content = t.noiDung || '', link = t.link || t.duongDan || '#', isUnread = !(t.daDoc || t.dadoc);
        var senderName = t.tennguoigui || t.tenNguoiGui || 'Người dùng';
        var av = t.avatarnguoigui || t.avatarNguoiGui || 'https://ui-avatars.com/api/?name=' + encodeURIComponent(senderName) + '&background=2563eb&color=fff';
        var time = (t.ngaytao || t.ngayTao) ? formatTimeAgo(t.ngaytao || t.ngayTao) : '';
        return '<div class="notification-item ' + (isUnread ? 'unread' : '') + '" data-id="' + t.id + '" data-link="' + link + '"><img src="' + av + '" class="notification-avatar" style="width:40px;height:40px;border-radius:50%;" alt=""><div class="notification-content"><p class="notification-text"><strong>' + title + '</strong></p>' + (content ? '<p class="notification-desc small text-muted">' + content + '</p>' : '') + (time ? '<span class="notification-time"><i class="bi bi-clock me-1"></i>' + time + '</span>' : '') + '</div>' + (isUnread ? '<span class="notification-badge">Mới</span>' : '') + '</div>';
    }).join('');
}

function filterNotifications(f, b) {
    currentFilter = f;
    document.querySelectorAll('.tab-btn').forEach(function(x) { x.classList.remove('btn-primary'); x.classList.add('btn-secondary'); });
    b.classList.remove('btn-secondary'); b.classList.add('btn-primary');
    renderNotifications();
}

function markAllAsRead(e) {
    e.preventDefault();
    fetch('/api/thong-bao/doc-tat-ca', { method: 'POST' }).then(function() { loadNotifications(); loadNotificationCount(); });
}

// ============== REPORT FUNCTIONS ==============
function openReportModal(type) {
    document.getElementById('reportType').value = type;
    document.getElementById('reportReason').value = '';
    document.getElementById('reportDescription').value = '';
    new bootstrap.Modal(document.getElementById('reportModal')).show();
}

function showResultModal(success, title, message) {
    document.getElementById('resultIcon').innerHTML = success ? '<i class="bi bi-check-circle-fill text-success"></i>' : '<i class="bi bi-x-circle-fill text-danger"></i>';
    document.getElementById('resultTitle').textContent = title;
    document.getElementById('resultMessage').textContent = message;
    new bootstrap.Modal(document.getElementById('resultModal')).show();
}

function submitReport() {
    var loai = document.getElementById('reportType').value;
    var maDoiTuong = document.getElementById('reportTargetId').value;
    var lyDo = document.getElementById('reportReason').value;
    var moTa = document.getElementById('reportDescription').value;
    if (!lyDo) { showResultModal(false, 'Thiếu thông tin', 'Vui lòng chọn lý do báo cáo'); return; }
    fetch('/api/bao-cao/gui', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ loai: loai, maDoiTuong: maDoiTuong, lyDo: lyDo, moTa: moTa }) })
    .then(function(r) { return r.json(); })
    .then(function(data) { bootstrap.Modal.getInstance(document.getElementById('reportModal')).hide(); if (data.success) { showResultModal(true, 'Gửi báo cáo thành công!', 'Admin sẽ xem xét báo cáo của bạn.'); } else { showResultModal(false, 'Không thể gửi báo cáo', data.message || 'Đã có lỗi xảy ra'); } })
    .catch(function() { showResultModal(false, 'Lỗi kết nối', 'Có lỗi xảy ra khi gửi báo cáo.'); });
}

// ============== INIT ==============
document.addEventListener('DOMContentLoaded', function() {
    loadNotifications(); loadNotificationCount(); setInterval(loadNotificationCount, 30000);
    var nl = document.getElementById('notification-list');
    if (nl) {
        nl.addEventListener('click', function(e) {
            var item = e.target.closest('.notification-item');
            if (!item) return;
            e.preventDefault();
            var id = item.getAttribute('data-id'), link = item.getAttribute('data-link');
            if (item.classList.contains('unread')) {
                item.classList.remove('unread');
                var badge = item.querySelector('.notification-badge'); if (badge) badge.style.display = 'none';
                var cb = document.querySelector('.notification-count');
                if (cb) { var c = Math.max(0, (parseInt(cb.textContent) || 0) - 1); cb.textContent = c > 0 ? c : ''; if (c === 0) cb.style.display = 'none'; }
            }
            fetch('/api/thong-bao/doc/' + id, { method: 'POST' });
            if (link && link !== '#' && link !== 'null') { setTimeout(function() { window.location.href = link; }, 200); }
        });
    }
    var nd = document.getElementById('notificationDropdown'); if (nd) nd.addEventListener('show.bs.dropdown', loadNotifications);
});
