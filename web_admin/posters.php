<?php
/**
 * SION CHOIR APP - Kelola Poster Slider Carousel
 * JKI Anugrah Ecclesia Cirebon
 * File: posters.php
 */
require_once 'config.php';
require_admin();

$msg = '';
$err = '';

// Tambah Poster
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action_add'])) {
    $title = trim($_POST['title'] ?? '');
    $subtitle = trim($_POST['subtitle'] ?? '');
    $image_url = trim($_POST['image_url'] ?? '');
    $action_text = trim($_POST['action_text'] ?? 'Lihat Detail');

    if (!empty($title) && !empty($image_url)) {
        $stmt = $pdo->prepare("INSERT INTO chsn_posters (title, subtitle, image_url, action_text) VALUES (?, ?, ?, ?)");
        $stmt->execute([$title, $subtitle, $image_url, $action_text]);
        $msg = "Poster carousel berhasil ditambahkan!";
    } else {
        $err = "Judul dan URL Gambar wajib diisi.";
    }
}

// Hapus Poster
if (isset($_GET['delete'])) {
    $del_id = (int)$_GET['delete'];
    $stmtDel = $pdo->prepare("DELETE FROM chsn_posters WHERE id = ?");
    $stmtDel->execute([$del_id]);
    $msg = "Poster carousel berhasil dihapus!";
}

$posters = $pdo->query("SELECT * FROM chsn_posters ORDER BY id DESC")->fetchAll();
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kelola Poster Slider - SION CHOIR ADMIN</title>
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
                <li class="nav-item"><a class="nav-link active fw-bold" href="posters.php">Poster Slider</a></li>
                <li class="nav-item"><a class="nav-link" href="kotbah.php">Kotbah Video</a></li>
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
            <h3 class="fw-bold mb-0 text-dark">Kelola Slider Carousel Beranda App</h3>
            <p class="text-muted small mb-0">Poster yang ditambahkan di sini otomatis muncul di carousel aplikasi mobile</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalTambah">
            <i class="bi bi-plus-circle me-1"></i> Tambah Poster Baru
        </button>
    </div>

    <?php if (!empty($msg)): ?>
        <div class="alert alert-success alert-dismissible fade show"><?= htmlspecialchars($msg) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <?php endif; ?>
    <?php if (!empty($err)): ?>
        <div class="alert alert-danger alert-dismissible fade show"><?= htmlspecialchars($err) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
    <?php endif; ?>

    <div class="row g-3">
        <?php if (empty($posters)): ?>
            <div class="col-12 text-center py-5 text-muted">
                <i class="bi bi-images fs-1 d-block mb-2 text-secondary"></i>
                Belum ada poster carousel. Silakan klik tombol "Tambah Poster Baru".
            </div>
        <?php else: ?>
            <?php foreach ($posters as $p): ?>
                <div class="col-md-4">
                    <div class="card h-100 border-0 shadow-sm rounded-4 overflow-hidden">
                        <img src="<?= htmlspecialchars($p['image_url']) ?>" class="card-img-top" style="height: 180px; object-fit: cover;" alt="<?= htmlspecialchars($p['title']) ?>" onerror="this.src='https://placehold.co/600x300?text=Poster+Choir';">
                        <div class="card-body">
                            <h5 class="fw-bold card-title mb-1"><?= htmlspecialchars($p['title']) ?></h5>
                            <p class="card-text text-muted small mb-3"><?= htmlspecialchars($p['subtitle']) ?></p>
                            <div class="d-flex justify-content-between align-items-center border-top pt-2">
                                <span class="badge bg-light text-secondary border"><?= htmlspecialchars($p['action_text']) ?></span>
                                <a href="posters.php?delete=<?= $p['id'] ?>" class="btn btn-sm btn-outline-danger" onclick="return confirm('Hapus poster ini?');">
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
                    <h5 class="modal-title fw-bold">Tambah Poster Carousel</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Judul Poster *</label>
                        <input type="text" name="title" class="form-control" required placeholder="cth: Jadwal Latihan Rutin Kamis">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Sub-judul / Waktu</label>
                        <input type="text" name="subtitle" class="form-control" placeholder="cth: Pukul 19.00 WIB di Ruang Sion">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">URL Gambar Poster (https://...) *</label>
                        <input type="url" name="image_url" class="form-control" required placeholder="https://terbangcirebon.sch.id/...">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Teks Tombol Aksi</label>
                        <input type="text" name="action_text" class="form-control" value="Lihat Detail">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">Simpan Poster</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
