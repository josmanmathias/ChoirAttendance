<?php
/**
 * SION CHOIR APP - Kelola Materi Pelayanan (SATB)
 * JKI Anugrah Ecclesia Cirebon
 * File: materi.php
 */
require_once 'config.php';
require_admin();

$msg = '';
$err = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action_add'])) {
    $title = trim($_POST['title'] ?? '');
    $subtitle = trim($_POST['subtitle'] ?? '');
    $voice_part = $_POST['voice_part'] ?? 'Semua';
    $pdf_url = trim($_POST['pdf_url'] ?? '');
    $audio_url = trim($_POST['audio_url'] ?? '');
    $lyrics = trim($_POST['lyrics'] ?? '');

    if (!empty($title)) {
        $stmt = $pdo->prepare("INSERT INTO chsn_materi (title, subtitle, voice_part, pdf_url, audio_url, lyrics) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->execute([$title, $subtitle, $voice_part, $pdf_url ?: null, $audio_url ?: null, $lyrics ?: null]);
        $msg = "Materi latihan berhasil ditambahkan!";
    } else {
        $err = "Judul lagu wajib diisi.";
    }
}

if (isset($_GET['delete'])) {
    $del_id = (int)$_GET['delete'];
    $stmtDel = $pdo->prepare("DELETE FROM chsn_materi WHERE id = ?");
    $stmtDel->execute([$del_id]);
    $msg = "Materi latihan berhasil dihapus!";
}

$materiList = $pdo->query("SELECT * FROM chsn_materi ORDER BY id DESC")->fetchAll();
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kelola Materi SATB - SION CHOIR ADMIN</title>
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
                <li class="nav-item"><a class="nav-link active fw-bold" href="materi.php">Materi SATB</a></li>
                <li class="nav-item"><a class="nav-link" href="jadwal.php">Jadwal & Galeri</a></li>
            </ul>
            <a href="logout.php" class="btn btn-sm btn-outline-light"><i class="bi bi-box-arrow-right"></i> Keluar</a>
        </div>
    </div>
</nav>

<div class="container-fluid px-4 py-4">

    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h3 class="fw-bold mb-0 text-dark">Kelola Materi Pelayanan (Partitur & Audio SATB)</h3>
            <p class="text-muted small mb-0">Anggota choir dapat membaca PDF partitur dan memutar audio rekaman di HP</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalTambah">
            <i class="bi bi-file-earmark-music me-1"></i> Tambah Materi Lagu
        </button>
    </div>

    <?php if (!empty($msg)): ?><div class="alert alert-success alert-dismissible fade show"><?= htmlspecialchars($msg) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><?php endif; ?>
    <?php if (!empty($err)): ?><div class="alert alert-danger alert-dismissible fade show"><?= htmlspecialchars($err) ?><button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><?php endif; ?>

    <div class="row g-3">
        <?php if (empty($materiList)): ?>
            <div class="col-12 text-center py-5 text-muted">Belum ada materi lagu tersimpan.</div>
        <?php else: ?>
            <?php foreach ($materiList as $m): ?>
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm rounded-4 h-100">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="badge bg-primary"><?= htmlspecialchars($m['voice_part']) ?></span>
                                <a href="materi.php?delete=<?= $m['id'] ?>" class="text-danger" onclick="return confirm('Hapus materi ini?');">
                                    <i class="bi bi-trash"></i>
                                </a>
                            </div>
                            <h5 class="fw-bold mb-1"><?= htmlspecialchars($m['title']) ?></h5>
                            <p class="text-muted small mb-3"><?= htmlspecialchars($m['subtitle']) ?></p>

                            <?php if (!empty($m['lyrics'])): ?>
                                <div class="p-2 bg-light rounded-3 small text-secondary mb-3 font-monospace">
                                    <?= nl2br(htmlspecialchars($m['lyrics'])) ?>
                                </div>
                            <?php endif; ?>

                            <div class="d-flex gap-2">
                                <?php if (!empty($m['pdf_url'])): ?>
                                    <a href="<?= htmlspecialchars($m['pdf_url']) ?>" target="_blank" class="btn btn-sm btn-outline-danger">
                                        <i class="bi bi-file-earmark-pdf"></i> Lihat Partitur PDF
                                    </a>
                                <?php endif; ?>

                                <?php if (!empty($m['audio_url'])): ?>
                                    <a href="<?= htmlspecialchars($m['audio_url']) ?>" target="_blank" class="btn btn-sm btn-outline-success">
                                        <i class="bi bi-play-circle"></i> Putar Audio
                                    </a>
                                <?php endif; ?>
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
                    <h5 class="modal-title fw-bold">Tambah Materi Lagu Latihan</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Judul Lagu *</label>
                        <input type="text" name="title" class="form-control" required placeholder="cth: Kudus, Kuduslah Tuhan (Sanctus)">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Sub-judul / Keterangan Aransemen</label>
                        <input type="text" name="subtitle" class="form-control" placeholder="cth: Partitur 4 Suara (SATB) - Ibadah Raya">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Bagian Suara (Vokal)</label>
                        <select name="voice_part" class="form-select">
                            <option value="Semua">Semua (SATB)</option>
                            <option value="Sopran">Sopran</option>
                            <option value="Alto">Alto</option>
                            <option value="Tenor">Tenor</option>
                            <option value="Bas">Bas</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Link URL File PDF Partitur</label>
                        <input type="url" name="pdf_url" class="form-control" placeholder="https://terbangcirebon.sch.id/.../partitur.pdf">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Link URL File Audio Rekaman (MP3)</label>
                        <input type="url" name="audio_url" class="form-control" placeholder="https://terbangcirebon.sch.id/.../latihan.mp3">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Catatan Lirik / Melodi</label>
                        <textarea name="lyrics" class="form-control" rows="3" placeholder="Tuliskan lirik atau pembagian ketukan..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">Simpan Materi</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
