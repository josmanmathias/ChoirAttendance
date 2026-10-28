<?php
/**
 * SION CHOIR APP - Kelola Video Kotbah
 * JKI Anugrah Ecclesia Cirebon
 * File: kotbah.php
 */
require_once 'config.php';
require_admin();

$msg = '';
$err = '';

function clean_yt_id($url) {
    $url = trim($url);
    if (strpos($url, 'v=') !== false) {
        $parts = explode('v=', $url);
        return explode('&', $parts[1])[0];
    }
    if (strpos($url, 'youtu.be/') !== false) {
        $parts = explode('youtu.be/', $url);
        return explode('?', $parts[1])[0];
    }
    return $url;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action_add'])) {
    $title = trim($_POST['title'] ?? '');
    $preacher = trim($_POST['preacher'] ?? '');
    $date_text = trim($_POST['date_text'] ?? '');
    $duration = trim($_POST['duration'] ?? '45:00');
    $yt_raw = trim($_POST['youtube_url'] ?? '');
    $category = $_POST['category'] ?? 'IBADAH_RAYA';

    $youtube_id = clean_yt_id($yt_raw);

    if (!empty($title) && !empty($youtube_id)) {
        $stmt = $pdo->prepare("INSERT INTO chsn_kotbah (title, preacher, date_text, duration, youtube_id, category) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->execute([$title, $preacher, $date_text, $duration, $youtube_id, $category]);
        $msg = "Video kotbah berhasil ditambahkan!";
    } else {
        $err = "Judul dan link YouTube wajib diisi.";
    }
}

if (isset($_GET['delete'])) {
    $del_id = (int)$_GET['delete'];
    $stmtDel = $pdo->prepare("DELETE FROM chsn_kotbah WHERE id = ?");
    $stmtDel->execute([$del_id]);
    $msg = "Kotbah berhasil dihapus!";
}

$filter_cat = $_GET['cat'] ?? '';
$sql = "SELECT * FROM chsn_kotbah WHERE 1=1";
$params = [];
if (!empty($filter_cat)) {
    $sql .= " AND category = ?";
    $params[] = $filter_cat;
}
$sql .= " ORDER BY id DESC";
$stmtKotbah = $pdo->prepare($sql);
$stmtKotbah->execute($params);
$kotbahList = $stmtKotbah->fetchAll();
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kelola Video Kotbah - SION CHOIR ADMIN</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #F8FAFC; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .navbar-choir { background-color: #0F1E36; border-bottom: 3px solid #D97706; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark navbar-choir sticky-top py-2">
    <div class="container-fluid px-4">
        <a class="navbar-brand fw-bold" href="dashboard.php" style="color: #FBBF24;">
            <i class="bi bi-music-note-beamed fs-4 me-2"></i> SION CHOIR ADMIN
        </a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="dashboard.php">Dashboard</a></li>
                <li class="nav-item"><a class="nav-link" href="users.php">Anggota Choir</a></li>
                <li class="nav-item"><a class="nav-link" href="absensi.php">Data Presensi</a></li>
                <li class="nav-item"><a class="nav-link" href="posters.php">Poster Slider</a></li>
                <li class="nav-item"><a class="nav-link active fw-bold" href="kotbah.php">Kotbah Video</a></li>
                <li class="nav-item"><a class="nav-link" href="materi.php">Materi SATB</a></li>
                <li class="nav-item"><a class="nav-link" href="jadwal.php">Jadwal & Galeri</a></li>
            </ul>
            <a href="logout.php" class="btn btn-sm btn-outline-light"><i class="bi bi-box-arrow-right"></i> Keluar</a>
        </div>
    </div>
</nav>

<div class="container-fluid px-4 py-4">

    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h3 class="fw-bold mb-0 text-dark">Kelola Video Kotbah & Pesan Gembala</h3>
            <p class="text-muted small mb-0">Video YouTube dapat langsung diputar di aplikasi Android Sion Choir</p>
        </div>
        <button class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#modalTambah">
            <i class="bi bi-youtube me-1"></i> Tambah Video Kotbah
        </button>
    </div>

    <?php if (!empty($msg)): ?><div class="alert alert-success alert-dismissible fade show"><?= htmlspecialchars($msg) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><?php endif; ?>
    <?php if (!empty($err)): ?><div class="alert alert-danger alert-dismissible fade show"><?= htmlspecialchars($err) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><?php endif; ?>

    <div class="card border-0 shadow-sm rounded-4 mb-4">
        <div class="card-body p-3">
            <div class="d-flex align-items-center gap-2">
                <span class="fw-semibold small text-muted">Kategori:</span>
                <a href="kotbah.php" class="btn btn-sm <?= empty($filter_cat) ? 'btn-dark' : 'btn-outline-secondary' ?>">Semua (<?= count($kotbahList) ?>)</a>
                <a href="kotbah.php?cat=IBADAH_RAYA" class="btn btn-sm <?= $filter_cat == 'IBADAH_RAYA' ? 'btn-dark' : 'btn-outline-secondary' ?>">Kotbah Ibadah Raya</a>
                <a href="kotbah.php?cat=PW_PAGI" class="btn btn-sm <?= $filter_cat == 'PW_PAGI' ? 'btn-dark' : 'btn-outline-secondary' ?>">Kotbah PW Pagi</a>
                <a href="kotbah.php?cat=PESAN_GEMBALA" class="btn btn-sm <?= $filter_cat == 'PESAN_GEMBALA' ? 'btn-dark' : 'btn-outline-secondary' ?>">Pesan Gembala</a>
            </div>
        </div>
    </div>

    <div class="row g-3">
        <?php if (empty($kotbahList)): ?>
            <div class="col-12 text-center py-5 text-muted">Belum ada video kotbah.</div>
        <?php else: ?>
            <?php foreach ($kotbahList as $k): ?>
                <div class="col-md-4">
                    <div class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden">
                        <div class="position-relative">
                            <img src="https://img.youtube.com/vi/<?= htmlspecialchars($k['youtube_id']) ?>/hqdefault.jpg" class="card-img-top" style="height: 180px; object-fit: cover;" alt="Thumbnail">
                            <a href="https://youtu.be/<?= htmlspecialchars($k['youtube_id']) ?>" target="_blank" class="position-absolute top-50 start-50 translate-middle btn btn-danger rounded-circle p-3">
                                <i class="bi bi-play-fill fs-3"></i>
                            </a>
                            <span class="position-absolute bottom-0 end-0 m-2 badge bg-dark bg-opacity-75"><?= htmlspecialchars($k['duration']) ?></span>
                        </div>
                        <div class="card-body">
                            <span class="badge bg-danger-subtle text-danger mb-2"><?= str_replace('_', ' ', $k['category']) ?></span>
                            <h6 class="fw-bold card-title mb-1"><?= htmlspecialchars($k['title']) ?></h6>
                            <p class="small text-muted mb-1"><i class="bi bi-person"></i> <?= htmlspecialchars($k['preacher']) ?></p>
                            <p class="small text-muted"><i class="bi bi-calendar"></i> <?= htmlspecialchars($k['date_text']) ?></p>
                            <div class="border-top pt-2 text-end">
                                <a href="kotbah.php?delete=<?= $k['id'] ?>" class="btn btn-sm btn-outline-danger" onclick="return confirm('Hapus video ini?');">
                                    <i class="bi bi-trash"></i> Hapus
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            <?php endforeach; ?>
        <?php endif; ?>
    </div>
</div>

<!-- Modal Tambah -->
<div class="modal fade" id="modalTambah" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="POST" action="">
                <input type="hidden" name="action_add" value="1">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Tambah Video Kotbah YouTube</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Kategori Menu *</label>
                        <select name="category" class="form-select">
                            <option value="IBADAH_RAYA">Kotbah Ibadah Raya</option>
                            <option value="PW_PAGI">Kotbah PW Pagi</option>
                            <option value="PESAN_GEMBALA">Pesan Gembala</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Judul Kotbah *</label>
                        <input type="text" name="title" class="form-control" required placeholder="cth: Menjadi Penyembah yang Benar">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Pengkhotbah / Pembicara</label>
                        <input type="text" name="preacher" class="form-control" placeholder="cth: Pdt. JKI Anugrah Ecclesia">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Link URL atau ID YouTube *</label>
                        <input type="text" name="youtube_url" class="form-control" required placeholder="https://www.youtube.com/watch?v=... atau ID video">
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label small fw-bold">Tanggal Ibadah</label>
                            <input type="text" name="date_text" class="form-control" placeholder="Minggu, 25 Okt 2026">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label small fw-bold">Durasi Video</label>
                            <input type="text" name="duration" class="form-control" value="45:00">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-danger">Simpan Video</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
