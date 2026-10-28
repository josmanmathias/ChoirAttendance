<?php
/**
 * SION CHOIR APP - Dashboard Web Admin
 * JKI Anugrah Ecclesia Cirebon
 * File: dashboard.php
 */
require_once 'config.php';
require_admin();

// Hitung metrik anggota
$totalAnggota = $pdo->query("SELECT COUNT(*) FROM chsn_users")->fetchColumn();
$sopranCount = $pdo->query("SELECT COUNT(*) FROM chsn_users WHERE posisi = 'Sopran'")->fetchColumn();
$altoCount = $pdo->query("SELECT COUNT(*) FROM chsn_users WHERE posisi = 'Alto'")->fetchColumn();
$tenorCount = $pdo->query("SELECT COUNT(*) FROM chsn_users WHERE posisi = 'Tenor'")->fetchColumn();
$basCount = $pdo->query("SELECT COUNT(*) FROM chsn_users WHERE posisi = 'Bas'")->fetchColumn();

// Hitung metrik konten
$totalPosters = $pdo->query("SELECT COUNT(*) FROM chsn_posters")->fetchColumn();
$totalKotbah = $pdo->query("SELECT COUNT(*) FROM chsn_kotbah")->fetchColumn();
$totalMateri = $pdo->query("SELECT COUNT(*) FROM chsn_materi")->fetchColumn();
$totalGaleri = $pdo->query("SELECT COUNT(*) FROM chsn_galeri")->fetchColumn();

// Hitung absensi hari ini
$today = date('Y-m-d');
$stmtAbsen = $pdo->prepare("
    SELECT COUNT(*) FROM chsn_absensi_latihan WHERE tanggal = ?
");
$stmtAbsen->execute([$today]);
$absenLatihanToday = $stmtAbsen->fetchColumn();

$stmtAbsenIbadah = $pdo->prepare("
    SELECT COUNT(*) FROM chsn_absensi_ibadah WHERE tanggal = ?
");
$stmtAbsenIbadah->execute([$today]);
$absenIbadahToday = $stmtAbsenIbadah->fetchColumn();

// Ambil 10 absensi terbaru gabungan
$recentAttendance = $pdo->query("
    (SELECT a.id, a.tanggal, a.waktu, a.latitude, a.longitude, a.foto, 'Latihan' as tipe, u.nama_lengkap, u.posisi
     FROM chsn_absensi_latihan a
     JOIN chsn_users u ON a.user_id = u.id)
    UNION
    (SELECT b.id, b.tanggal, b.waktu, b.latitude, b.longitude, b.foto, 'Ibadah' as tipe, u.nama_lengkap, u.posisi
     FROM chsn_absensi_ibadah b
     JOIN chsn_users u ON b.user_id = u.id)
    ORDER BY tanggal DESC, waktu DESC
    LIMIT 10
")->fetchAll();
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Admin - SION CHOIR APP | JKI Ecclesia</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        :root {
            --choir-navy: #0F1E36;
            --choir-navy-dark: #0A1324;
            --choir-gold: #D97706;
            --choir-gold-light: #FBBF24;
        }
        body {
            background-color: #F8FAFC;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        }
        .navbar-choir {
            background-color: var(--choir-navy);
            border-bottom: 3px solid var(--choir-gold);
        }
        .card-stat {
            border: none;
            border-radius: 14px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            transition: transform 0.2s;
        }
        .card-stat:hover {
            transform: translateY(-3px);
        }
        .badge-sopran { background-color: #EC4899; color: white; }
        .badge-alto { background-color: #8B5CF6; color: white; }
        .badge-tenor { background-color: #0284C7; color: white; }
        .badge-bas { background-color: #059669; color: white; }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark navbar-choir sticky-top py-2">
    <div class="container-fluid px-4">
        <a class="navbar-brand fw-bold d-flex align-items-center" href="dashboard.php" style="color: var(--choir-gold-light);">
            <i class="bi bi-music-note-beamed fs-4 me-2"></i>
            SION CHOIR ADMIN
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navMenu">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link active fw-semibold" href="dashboard.php"><i class="bi bi-speedometer2 me-1"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link" href="users.php"><i class="bi bi-people me-1"></i> Anggota Choir</a></li>
                <li class="nav-item"><a class="nav-link" href="absensi.php"><i class="bi bi-qr-code-scan me-1"></i> Data Presensi</a></li>
                <li class="nav-item"><a class="nav-link" href="posters.php"><i class="bi bi-images me-1"></i> Poster Slider</a></li>
                <li class="nav-item"><a class="nav-link" href="kotbah.php"><i class="bi bi-youtube me-1"></i> Kotbah Video</a></li>
                <li class="nav-item"><a class="nav-link" href="materi.php"><i class="bi bi-file-earmark-music me-1"></i> Materi SATB</a></li>
                <li class="nav-item"><a class="nav-link" href="jadwal.php"><i class="bi bi-calendar-event me-1"></i> Jadwal & Galeri</a></li>
            </ul>

            <div class="d-flex align-items-center text-white">
                <span class="badge bg-warning text-dark me-3"><i class="bi bi-shield-check me-1"></i> <?= htmlspecialchars($_SESSION['nama_lengkap']) ?> (Admin)</span>
                <a href="logout.php" class="btn btn-sm btn-outline-light"><i class="bi bi-box-arrow-right"></i> Keluar</a>
            </div>
        </div>
    </div>
</nav>

<!-- Main Container -->
<div class="container-fluid px-4 py-4">

    <!-- Header Greeting -->
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h3 class="fw-bold text-navy mb-0" style="color: var(--choir-navy);">Panel Utama Pengelolaan Sion Choir</h3>
            <p class="text-muted small mb-0">Gereja JKI Anugrah Ecclesia Cirebon • Database: <strong>terbang2_absensiqr</strong></p>
        </div>
        <div>
            <span class="text-secondary small me-2"><i class="bi bi-geo-alt-fill text-danger"></i> Geofence Radius: 100m</span>
            <span class="badge bg-primary px-3 py-2"><?= date('l, d F Y') ?></span>
        </div>
    </div>

    <!-- Quick Stats Row -->
    <div class="row g-3 mb-4">
        <!-- Anggota -->
        <div class="col-md-3">
            <div class="card card-stat bg-white p-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-muted small text-uppercase fw-bold">Total Anggota</span>
                        <h2 class="fw-bold mb-0 text-dark"><?= $totalAnggota ?></h2>
                        <small class="text-success"><i class="bi bi-check-circle"></i> Terdaftar di MySQL</small>
                    </div>
                    <div class="bg-primary bg-opacity-10 p-3 rounded-circle text-primary">
                        <i class="bi bi-people-fill fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Presensi Hari Ini -->
        <div class="col-md-3">
            <div class="card card-stat bg-white p-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-muted small text-uppercase fw-bold">Presensi Hari Ini</span>
                        <h2 class="fw-bold mb-0 text-success"><?= ($absenLatihanToday + $absenIbadahToday) ?></h2>
                        <small class="text-muted">Latihan: <?= $absenLatihanToday ?> | Ibadah: <?= $absenIbadahToday ?></small>
                    </div>
                    <div class="bg-success bg-opacity-10 p-3 rounded-circle text-success">
                        <i class="bi bi-person-check-fill fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Materi SATB -->
        <div class="col-md-3">
            <div class="card card-stat bg-white p-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-muted small text-uppercase fw-bold">Materi Latihan</span>
                        <h2 class="fw-bold mb-0 text-info"><?= $totalMateri ?></h2>
                        <small class="text-muted">Partitur PDF & Audio</small>
                    </div>
                    <div class="bg-info bg-opacity-10 p-3 rounded-circle text-info">
                        <i class="bi bi-music-note-list fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Kotbah & Pesan -->
        <div class="col-md-3">
            <div class="card card-stat bg-white p-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <span class="text-muted small text-uppercase fw-bold">Video Kotbah</span>
                        <h2 class="fw-bold mb-0 text-danger"><?= $totalKotbah ?></h2>
                        <small class="text-muted">YouTube Terintegrasi</small>
                    </div>
                    <div class="bg-danger bg-opacity-10 p-3 rounded-circle text-danger">
                        <i class="bi bi-youtube fs-3"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Voice Distribution -->
    <div class="card border-0 shadow-sm rounded-4 mb-4">
        <div class="card-body p-4">
            <h5 class="fw-bold mb-3"><i class="bi bi-pie-chart-fill me-2 text-warning"></i> Distribusi Suara Choir (SATB)</h5>
            <div class="row g-3 text-center">
                <div class="col-3">
                    <div class="p-3 border rounded-3">
                        <span class="badge badge-sopran mb-2 px-3 py-1">SOPRAN</span>
                        <h3 class="fw-bold mb-0"><?= $sopranCount ?></h3>
                        <small class="text-muted">Penyanyi</small>
                    </div>
                </div>
                <div class="col-3">
                    <div class="p-3 border rounded-3">
                        <span class="badge badge-alto mb-2 px-3 py-1">ALTO</span>
                        <h3 class="fw-bold mb-0"><?= $altoCount ?></h3>
                        <small class="text-muted">Penyanyi</small>
                    </div>
                </div>
                <div class="col-3">
                    <div class="p-3 border rounded-3">
                        <span class="badge badge-tenor mb-2 px-3 py-1">TENOR</span>
                        <h3 class="fw-bold mb-0"><?= $tenorCount ?></h3>
                        <small class="text-muted">Penyanyi</small>
                    </div>
                </div>
                <div class="col-3">
                    <div class="p-3 border rounded-3">
                        <span class="badge badge-bas mb-2 px-3 py-1">BAS</span>
                        <h3 class="fw-bold mb-0"><?= $basCount ?></h3>
                        <small class="text-muted">Penyanyi</small>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Attendance Logs Table -->
    <div class="card border-0 shadow-sm rounded-4">
        <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
            <h5 class="fw-bold mb-0"><i class="bi bi-clock-history me-2 text-primary"></i> 10 Presensi Terakhir Anggota</h5>
            <a href="absensi.php" class="btn btn-sm btn-outline-primary fw-semibold">Lihat Semua Data <i class="bi bi-arrow-right"></i></a>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="ps-4">Nama Anggota</th>
                            <th>Vokal</th>
                            <th>Jenis Absen</th>
                            <th>Tanggal & Waktu</th>
                            <th>Koordinat GPS</th>
                            <th>Status Geofence</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (empty($recentAttendance)): ?>
                            <tr>
                                <td colspan="6" class="text-center py-4 text-muted">Belum ada data absensi tercatat</td>
                            </tr>
                        <?php else: ?>
                            <?php foreach ($recentAttendance as $row): 
                                $dist = null;
                                if (!empty($row['latitude']) && !empty($row['longitude'])) {
                                    $dist = calculate_distance_meters($row['latitude'], $row['longitude'], CHURCH_LATITUDE, CHURCH_LONGITUDE);
                                }
                            ?>
                                <tr>
                                    <td class="ps-4 fw-bold text-dark">
                                        <?= htmlspecialchars($row['nama_lengkap']) ?>
                                    </td>
                                    <td>
                                        <span class="badge badge-<?= strtolower($row['posisi']) ?>">
                                            <?= htmlspecialchars($row['posisi']) ?>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge <?= $row['tipe'] == 'Latihan' ? 'bg-primary' : 'bg-warning text-dark' ?>">
                                            Absen <?= $row['tipe'] ?>
                                        </span>
                                    </td>
                                    <td>
                                        <div><?= htmlspecialchars($row['tanggal']) ?></div>
                                        <small class="text-muted fw-bold"><?= htmlspecialchars($row['waktu']) ?> WIB</small>
                                    </td>
                                    <td>
                                        <?php if (!empty($row['latitude'])): ?>
                                            <a href="https://maps.google.com/?q=<?= $row['latitude'] ?>,<?= $row['longitude'] ?>" target="_blank" class="text-decoration-none small">
                                                <i class="bi bi-geo-alt-fill text-danger"></i> <?= round($row['latitude'], 4) ?>, <?= round($row['longitude'], 4) ?>
                                            </a>
                                        <?php else: ?>
                                            <span class="text-muted small">-</span>
                                        <?php endif; ?>
                                    </td>
                                    <td>
                                        <?php if ($dist !== null): ?>
                                            <?php if ($dist <= CHURCH_ALLOWED_RADIUS_METERS): ?>
                                                <span class="badge bg-success-subtle text-success border border-success">
                                                    <i class="bi bi-check-circle-fill"></i> Valid (<?= round($dist) ?>m)
                                                </span>
                                            <?php else: ?>
                                                <span class="badge bg-danger-subtle text-danger border border-danger">
                                                    <i class="bi bi-x-circle-fill"></i> Di luar radius (<?= round($dist) ?>m)
                                                </span>
                                            <?php endif; ?>
                                        <?php else: ?>
                                            <span class="badge bg-secondary">Tersimpan</span>
                                        <?php endif; ?>
                                    </td>
                                </tr>
                            <?php endforeach; ?>
                        <?php endif; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
