<?php
/**
 * SION CHOIR APP - Rekap & Monitoring Absensi
 * JKI Anugrah Ecclesia Cirebon
 * File: absensi.php
 */
require_once 'config.php';
require_admin();

$filter_tipe = $_GET['tipe'] ?? 'semua';
$filter_tanggal = $_GET['tanggal'] ?? '';

// Handle CSV Export
if (isset($_GET['export']) && $_GET['export'] == 'csv') {
    header('Content-Type: text/csv; charset=utf-8');
    header('Content-Disposition: attachment; filename=rekap_absensi_sion_choir_' . date('Ymd_His') . '.csv');
    $output = fopen('php://output', 'w');
    fputcsv($output, ['ID', 'Nama Anggota', 'Vokal', 'Kegiatan', 'Tanggal', 'Waktu (WIB)', 'Latitude', 'Longitude', 'Jarak Gereja (m)']);

    $q = "
        (SELECT a.id, a.tanggal, a.waktu, a.latitude, a.longitude, 'Latihan' as tipe, u.nama_lengkap, u.posisi
         FROM chsn_absensi_latihan a JOIN chsn_users u ON a.user_id = u.id)
        UNION
        (SELECT b.id, b.tanggal, b.waktu, b.latitude, b.longitude, 'Ibadah' as tipe, u.nama_lengkap, u.posisi
         FROM chsn_absensi_ibadah b JOIN chsn_users u ON b.user_id = u.id)
        ORDER BY tanggal DESC, waktu DESC
    ";
    $rows = $pdo->query($q)->fetchAll();
    foreach ($rows as $r) {
        $dist = '';
        if (!empty($r['latitude']) && !empty($r['longitude'])) {
            $dist = round(calculate_distance_meters($r['latitude'], $r['longitude'], CHURCH_LATITUDE, CHURCH_LONGITUDE));
        }
        fputcsv($output, [$r['id'], $r['nama_lengkap'], $r['posisi'], 'Absen ' . $r['tipe'], $r['tanggal'], $r['waktu'], $r['latitude'], $r['longitude'], $dist]);
    }
    fclose($output);
    exit;
}

// Query data
$sqlParts = [];
if ($filter_tipe == 'semua' || $filter_tipe == 'latihan') {
    $where1 = "1=1";
    if (!empty($filter_tanggal)) $where1 .= " AND a.tanggal = " . $pdo->quote($filter_tanggal);
    $sqlParts[] = "SELECT a.id, a.tanggal, a.waktu, a.latitude, a.longitude, a.foto, 'Latihan' as tipe, u.nama_lengkap, u.posisi
                   FROM chsn_absensi_latihan a JOIN chsn_users u ON a.user_id = u.id WHERE $where1";
}

if ($filter_tipe == 'semua' || $filter_tipe == 'ibadah') {
    $where2 = "1=1";
    if (!empty($filter_tanggal)) $where2 .= " AND b.tanggal = " . $pdo->quote($filter_tanggal);
    $sqlParts[] = "SELECT b.id, b.tanggal, b.waktu, b.latitude, b.longitude, b.foto, 'Ibadah' as tipe, u.nama_lengkap, u.posisi
                   FROM chsn_absensi_ibadah b JOIN chsn_users u ON b.user_id = u.id WHERE $where2";
}

$fullSql = implode(" UNION ", $sqlParts) . " ORDER BY tanggal DESC, waktu DESC";
$records = $pdo->query($fullSql)->fetchAll();
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rekap Presensi - SION CHOIR ADMIN</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #F8FAFC; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .navbar-choir { background-color: #0F1E36; border-bottom: 3px solid #D97706; }
        .badge-sopran { background-color: #EC4899; color: white; }
        .badge-alto { background-color: #8B5CF6; color: white; }
        .badge-tenor { background-color: #0284C7; color: white; }
        .badge-bas { background-color: #059669; color: white; }
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
                <li class="nav-item"><a class="nav-link active fw-bold" href="absensi.php">Data Presensi</a></li>
                <li class="nav-item"><a class="nav-link" href="posters.php">Poster Slider</a></li>
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
            <h3 class="fw-bold mb-0 text-dark">Data Presensi Choir (Latihan & Ibadah)</h3>
            <p class="text-muted small mb-0">Tabel: <strong>chsn_absensi_latihan</strong> & <strong>chsn_absensi_ibadah</strong></p>
        </div>
        <a href="absensi.php?export=csv" class="btn btn-success">
            <i class="bi bi-file-earmark-spreadsheet me-1"></i> Export ke Excel / CSV
        </a>
    </div>

    <!-- Filter Form -->
    <div class="card border-0 shadow-sm rounded-4 mb-4">
        <div class="card-body p-3">
            <form method="GET" class="row g-2 align-items-center">
                <div class="col-auto">
                    <label class="small fw-bold text-muted">Tipe:</label>
                </div>
                <div class="col-auto">
                    <select name="tipe" class="form-select form-select-sm">
                        <option value="semua" <?= $filter_tipe == 'semua' ? 'selected' : '' ?>>Semua Kegiatan</option>
                        <option value="latihan" <?= $filter_tipe == 'latihan' ? 'selected' : '' ?>>Absen Latihan</option>
                        <option value="ibadah" <?= $filter_tipe == 'ibadah' ? 'selected' : '' ?>>Absen Ibadah</option>
                    </select>
                </div>
                <div class="col-auto ms-2">
                    <label class="small fw-bold text-muted">Tanggal:</label>
                </div>
                <div class="col-auto">
                    <input type="date" name="tanggal" value="<?= htmlspecialchars($filter_tanggal) ?>" class="form-control form-control-sm">
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-sm btn-primary"><i class="bi bi-filter"></i> Terapkan Filter</button>
                    <a href="absensi.php" class="btn btn-sm btn-outline-secondary">Reset</a>
                </div>
            </form>
        </div>
    </div>

    <!-- Table -->
    <div class="card border-0 shadow-sm rounded-4">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="ps-4">No</th>
                            <th>Nama Anggota</th>
                            <th>Vokal</th>
                            <th>Kegiatan</th>
                            <th>Tanggal</th>
                            <th>Waktu (WIB)</th>
                            <th>Lokasi GPS</th>
                            <th>Radius Gereja</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php if (empty($records)): ?>
                            <tr><td colspan="8" class="text-center py-4 text-muted">Tidak ada data presensi yang sesuai</td></tr>
                        <?php else: ?>
                            <?php $no = 1; foreach ($records as $r): 
                                $dist = null;
                                if (!empty($r['latitude']) && !empty($r['longitude'])) {
                                    $dist = calculate_distance_meters($r['latitude'], $r['longitude'], CHURCH_LATITUDE, CHURCH_LONGITUDE);
                                }
                            ?>
                                <tr>
                                    <td class="ps-4 text-muted"><?= $no++ ?></td>
                                    <td class="fw-bold"><?= htmlspecialchars($r['nama_lengkap']) ?></td>
                                    <td><span class="badge badge-<?= strtolower($r['posisi']) ?>"><?= htmlspecialchars($r['posisi']) ?></span></td>
                                    <td>
                                        <span class="badge <?= $r['tipe'] == 'Latihan' ? 'bg-primary' : 'bg-warning text-dark' ?>">
                                            Absen <?= $r['tipe'] ?>
                                        </span>
                                    </td>
                                    <td><?= htmlspecialchars($r['tanggal']) ?></td>
                                    <td class="fw-semibold text-primary"><?= htmlspecialchars($r['waktu']) ?></td>
                                    <td>
                                        <?php if (!empty($r['latitude'])): ?>
                                            <a href="https://maps.google.com/?q=<?= $r['latitude'] ?>,<?= $r['longitude'] ?>" target="_blank" class="small text-decoration-none">
                                                <i class="bi bi-geo-alt-fill text-danger"></i> <?= round($r['latitude'], 4) ?>, <?= round($r['longitude'], 4) ?>
                                            </a>
                                        <?php else: ?>
                                            <span class="text-muted small">-</span>
                                        <?php endif; ?>
                                    </td>
                                    <td>
                                        <?php if ($dist !== null): ?>
                                            <?php if ($dist <= CHURCH_ALLOWED_RADIUS_METERS): ?>
                                                <span class="badge bg-success-subtle text-success border border-success">
                                                    <i class="bi bi-check-circle"></i> <?= round($dist) ?>m (Valid)
                                                </span>
                                            <?php else: ?>
                                                <span class="badge bg-danger-subtle text-danger border border-danger">
                                                    <i class="bi bi-x-circle"></i> <?= round($dist) ?>m (Di Luar 100m)
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
