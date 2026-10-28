<?php
/**
 * SION CHOIR APP - Kelola Jadwal & Galeri Foto
 * JKI Anugrah Ecclesia Cirebon
 * File: jadwal.php
 */
require_once 'config.php';
require_admin();

$msg = '';
$err = '';

// Ambil URL Jadwal tersimpan
$stmtGetJadwal = $pdo->prepare("SELECT setting_value FROM chsn_settings WHERE setting_key = 'jadwal_url'");
$stmtGetJadwal->execute();
$currentJadwal = $stmtGetJadwal->fetchColumn() ?: "https://terbangcirebon.sch.id/jki/ministry/choir/jadwal.png";

// Update URL Jadwal
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action_jadwal'])) {
    $new_url = trim($_POST['jadwal_url'] ?? '');
    if (!empty($new_url)) {
        $stmtSave = $pdo->prepare("REPLACE INTO chsn_settings (setting_key, setting_value) VALUES ('jadwal_url', ?)");
        $stmtSave->execute([$new_url]);
        $currentJadwal = $new_url;
        $msg = "Link file jadwal pelayanan berhasil diperbarui!";
    }
}

// Tambah Foto Galeri
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action_galeri'])) {
    $title = trim($_POST['title'] ?? '');
    $date_text = trim($_POST['date_text'] ?? '');
    $image_url = trim($_POST['image_url'] ?? '');
    $description = trim($_POST['description'] ?? '');

    if (!empty($title) && !empty($image_url)) {
        $stmtGal = $pdo->prepare("INSERT INTO chsn_galeri (title, date_text, image_url, description) VALUES (?, ?, ?, ?)");
        $stmtGal->execute([$title, $date_text, $image_url, $description]);
        $msg = "Foto kegiatan berhasil ditambahkan ke galeri!";
    } else {
        $err = "Judul dan link gambar wajib diisi.";
    }
}

// Hapus Foto Galeri
if (isset($_GET['delete_galeri'])) {
    $del_id = (int)$_GET['delete_galeri'];
    $stmtDel = $pdo->prepare("DELETE FROM chsn_galeri WHERE id = ?");
    $stmtDel->execute([$del_id]);
    $msg = "Foto galeri berhasil dihapus!";
}

$galeriList = $pdo->query("SELECT * FROM chsn_galeri ORDER BY id DESC")->fetchAll();
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kelola Jadwal & Galeri - SION CHOIR ADMIN</title>
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
                <li class="nav-item"><a class="nav-link" href="kotbah.php">Kotbah Video</a></li>
                <li class="nav-item"><a class="nav-link" href="materi.php">Materi SATB</a></li>
                <li class="nav-item"><a class="nav-link active fw-bold" href="jadwal.php">Jadwal & Galeri</a></li>
            </ul>
            <a href="logout.php" class="btn btn-sm btn-outline-light"><i class="bi bi-box-arrow-right"></i> Keluar</a>
        </div>
    </div>
</nav>

<div class="container-fluid px-4 py-4">

    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h3 class="fw-bold mb-0 text-dark">Kelola Jadwal & Galeri Foto</h3>
            <p class="text-muted small mb-0">Atur file gambar jadwal pelayanan dan arsip dokumentasi kegiatan</p>
        </div>
    </div>

    <?php if (!empty($msg)): ?><div class="alert alert-success alert-dismissible fade show"><?= htmlspecialchars($msg) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><?php endif; ?>
    <?php if (!empty($err)): ?><div class="alert alert-danger alert-dismissible fade show"><?= htmlspecialchars($err) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><?php endif; ?>

    <!-- Bagian 1: Jadwal Pelayanan PNG -->
    <div class="card border-0 shadow-sm rounded-4 mb-5">
        <div class="card-header bg-white py-3">
            <h5 class="fw-bold mb-0 text-dark"><i class="bi bi-calendar-check me-2 text-primary"></i> File Jadwal Pelayanan Choir (PNG)</h5>
        </div>
        <div class="card-body p-4">
            <div class="row align-items-center">
                <div class="col-md-7">
                    <form method="POST" action="">
                        <input type="hidden" name="action_jadwal" value="1">
                        <label class="form-label fw-bold small">URL File Gambar Jadwal Terkini:</label>
                        <div class="input-group mb-3">
                            <input type="url" name="jadwal_url" value="<?= htmlspecialchars($currentJadwal) ?>" class="form-control" required>
                            <button type="submit" class="btn btn-primary"><i class="bi bi-save me-1"></i> Perbarui Link</button>
                        </div>
                        <p class="text-muted small">
                            Default: <code>https://terbangcirebon.sch.id/jki/ministry/choir/jadwal.png</code><br>
                            Saat link ini diperbarui, seluruh anggota yang membuka menu <strong>Jadwal</strong> di aplikasi akan otomatis melihat gambar terbaru dengan fitur zoom dan download.
                        </p>
                    </form>
                </div>
                <div class="col-md-5 text-center">
                    <p class="small fw-bold text-muted mb-2">Pratinjau Gambar:</p>
                    <img src="<?= htmlspecialchars($currentJadwal) ?>" class="img-fluid rounded-3 border shadow-sm" style="max-height: 200px;" alt="Pratinjau Jadwal" onerror="this.src='https://placehold.co/500x300?text=Jadwal+Choir+PNG';">
                </div>
            </div>
        </div>
    </div>

    <!-- Bagian 2: Galeri Dokumentasi -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="fw-bold text-dark mb-0"><i class="bi bi-images me-2 text-warning"></i> Galeri Dokumentasi Kegiatan</h5>
        <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#modalTambahGaleri">
            <i class="bi bi-plus-circle me-1"></i> Tambah Foto Galeri
        </button>
    </div>

    <div class="row g-3">
        <?php if (empty($galeriList)): ?>
            <div class="col-12 text-center py-4 text-muted">Belum ada foto galeri.</div>
        <?php else: ?>
            <?php foreach ($galeriList as $g): ?>
                <div class="col-md-3">
                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden h-100">
                        <img src="<?= htmlspecialchars($g['image_url']) ?>" class="card-img-top" style="height: 160px; object-fit: cover;" alt="Galeri" onerror="this.src='https://placehold.co/400x300?text=Foto+Choir';">
                        <div class="card-body p-3">
                            <h6 class="fw-bold mb-1"><?= htmlspecialchars($g['title']) ?></h6>
                            <p class="small text-muted mb-2"><i class="bi bi-calendar"></i> <?= htmlspecialchars($g['date_text']) ?></p>
                            <p class="small text-secondary mb-3"><?= htmlspecialchars($g['description']) ?></p>
                            <div class="text-end border-top pt-2">
                                <a href="jadwal.php?delete_galeri=<?= $g['id'] ?>" class="btn btn-sm btn-outline-danger" onclick="return confirm('Hapus foto ini?');">
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

<!-- Modal Tambah Galeri -->
<div class="modal fade" id="modalTambahGaleri" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="POST" action="">
                <input type="hidden" name="action_galeri" value="1">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Tambah Foto Galeri Choir</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Judul Kegiatan *</label>
                        <input type="text" name="title" class="form-control" required placeholder="cth: Pelayanan Ibadah Raya Minggu">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Tanggal / Bulan</label>
                        <input type="text" name="date_text" class="form-control" placeholder="cth: 25 Oktober 2026">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">URL Gambar Foto *</label>
                        <input type="url" name="image_url" class="form-control" required placeholder="https://terbangcirebon.sch.id/.../foto.jpg">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Deskripsi Singkat</label>
                        <textarea name="description" class="form-control" rows="2" placeholder="Keterangan momen foto..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">Simpan Foto</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
