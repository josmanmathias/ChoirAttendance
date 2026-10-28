<?php
/**
 * SION CHOIR APP - Kelola Anggota Choir
 * JKI Anugrah Ecclesia Cirebon
 * File: users.php
 */
require_once 'config.php';
require_admin();

$msg = '';
$err = '';

// Handle Tambah Anggota
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action_add'])) {
    $username = trim($_POST['username'] ?? '');
    $password = trim($_POST['password'] ?? '');
    $nama_lengkap = trim($_POST['nama_lengkap'] ?? '');
    $email = trim($_POST['email'] ?? null);
    $no_hp = trim($_POST['no_hp'] ?? null);
    $posisi = $_POST['posisi'] ?? 'Sopran';
    $role = $_POST['role'] ?? 'member';

    if (!empty($username) && !empty($password) && !empty($nama_lengkap)) {
        // Cek username unik
        $stmtCek = $pdo->prepare("SELECT id FROM chsn_users WHERE username = ?");
        $stmtCek->execute([$username]);
        if ($stmtCek->fetch()) {
            $err = "Username '$username' sudah terdaftar. Silakan gunakan username lain.";
        } else {
            $stmtInsert = $pdo->prepare("
                INSERT INTO chsn_users (username, password, nama_lengkap, email, no_hp, posisi, role, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'active')
            ");
            $stmtInsert->execute([$username, $password, $nama_lengkap, $email, $no_hp, $posisi, $role]);
            $msg = "Anggota '$nama_lengkap' berhasil didaftarkan!";
        }
    } else {
        $err = "Semua field bertanda bintang (*) wajib diisi.";
    }
}

// Handle Update Vokal / Role / Status
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action_update'])) {
    $user_id = (int)($_POST['user_id'] ?? 0);
    $posisi = $_POST['posisi'] ?? 'Sopran';
    $role = $_POST['role'] ?? 'member';
    $status = $_POST['status'] ?? 'active';

    if ($user_id > 0) {
        $stmtUpdate = $pdo->prepare("UPDATE chsn_users SET posisi = ?, role = ?, status = ? WHERE id = ?");
        $stmtUpdate->execute([$posisi, $role, $status, $user_id]);
        $msg = "Data anggota berhasil diperbarui!";
    }
}

// Handle Hapus Anggota
if (isset($_GET['delete'])) {
    $del_id = (int)$_GET['delete'];
    if ($del_id != $_SESSION['user_id']) {
        $stmtDel = $pdo->prepare("DELETE FROM chsn_users WHERE id = ?");
        $stmtDel->execute([$del_id]);
        $msg = "Anggota berhasil dihapus dari database.";
    } else {
        $err = "Anda tidak dapat menghapus akun admin yang sedang login!";
    }
}

// Filter
$filter_posisi = $_GET['posisi'] ?? '';
$sql = "SELECT * FROM chsn_users WHERE 1=1";
$params = [];

if (!empty($filter_posisi)) {
    $sql .= " AND posisi = ?";
    $params[] = $filter_posisi;
}
$sql .= " ORDER BY id ASC";
$stmtUsers = $pdo->prepare($sql);
$stmtUsers->execute($params);
$users = $stmtUsers->fetchAll();
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kelola Anggota - SION CHOIR APP</title>
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

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark navbar-choir sticky-top py-2">
    <div class="container-fluid px-4">
        <a class="navbar-brand fw-bold" href="dashboard.php" style="color: #FBBF24;">
            <i class="bi bi-music-note-beamed fs-4 me-2"></i> SION CHOIR ADMIN
        </a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="dashboard.php">Dashboard</a></li>
                <li class="nav-item"><a class="nav-link active fw-bold" href="users.php">Anggota Choir</a></li>
                <li class="nav-item"><a class="nav-link" href="absensi.php">Data Presensi</a></li>
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
            <h3 class="fw-bold mb-0 text-dark">Data Anggota Choir (Tabel chsn_users)</h3>
            <p class="text-muted small mb-0">Kelola akun, penempatan vokal SATB, dan hak akses anggota</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalTambah">
            <i class="bi bi-person-plus-fill me-1"></i> Tambah Anggota Baru
        </button>
    </div>

    <?php if (!empty($msg)): ?>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill me-1"></i> <?= htmlspecialchars($msg) ?>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <?php endif; ?>

    <?php if (!empty($err)): ?>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-1"></i> <?= htmlspecialchars($err) ?>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <?php endif; ?>

    <!-- Filter Bar -->
    <div class="card border-0 shadow-sm rounded-4 mb-4">
        <div class="card-body p-3">
            <div class="d-flex align-items-center gap-2">
                <span class="fw-semibold small text-muted">Filter Bagian Vokal:</span>
                <a href="users.php" class="btn btn-sm <?= empty($filter_posisi) ? 'btn-dark' : 'btn-outline-secondary' ?>">Semua (<?= count($users) ?>)</a>
                <a href="users.php?posisi=Sopran" class="btn btn-sm <?= $filter_posisi == 'Sopran' ? 'btn-dark' : 'btn-outline-secondary' ?>">Sopran</a>
                <a href="users.php?posisi=Alto" class="btn btn-sm <?= $filter_posisi == 'Alto' ? 'btn-dark' : 'btn-outline-secondary' ?>">Alto</a>
                <a href="users.php?posisi=Tenor" class="btn btn-sm <?= $filter_posisi == 'Tenor' ? 'btn-dark' : 'btn-outline-secondary' ?>">Tenor</a>
                <a href="users.php?posisi=Bas" class="btn btn-sm <?= $filter_posisi == 'Bas' ? 'btn-dark' : 'btn-outline-secondary' ?>">Bas</a>
            </div>
        </div>
    </div>

    <!-- Users Table -->
    <div class="card border-0 shadow-sm rounded-4">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="ps-4">ID</th>
                            <th>Nama Lengkap</th>
                            <th>Username</th>
                            <th>Vokal (Posisi)</th>
                            <th>Role</th>
                            <th>Kontak</th>
                            <th>Status</th>
                            <th class="text-end pe-4">Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($users as $u): ?>
                            <tr>
                                <td class="ps-4 text-muted">#<?= $u['id'] ?></td>
                                <td class="fw-bold text-dark">
                                    <?= htmlspecialchars($u['nama_lengkap']) ?>
                                </td>
                                <td><code><?= htmlspecialchars($u['username']) ?></code></td>
                                <td>
                                    <span class="badge badge-<?= strtolower($u['posisi']) ?>">
                                        <?= htmlspecialchars($u['posisi']) ?>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge <?= $u['role'] == 'admin' ? 'bg-warning text-dark' : 'bg-light text-dark border' ?>">
                                        <?= strtoupper($u['role']) ?>
                                    </span>
                                </td>
                                <td>
                                    <small class="d-block text-muted"><?= htmlspecialchars($u['no_hp'] ?: '-') ?></small>
                                    <small class="text-muted"><?= htmlspecialchars($u['email'] ?: '-') ?></small>
                                </td>
                                <td>
                                    <span class="badge <?= $u['status'] == 'active' ? 'bg-success' : 'bg-secondary' ?>">
                                        <?= ucfirst($u['status']) ?>
                                    </span>
                                </td>
                                <td class="text-end pe-4">
                                    <button class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modalEdit<?= $u['id'] ?>">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </button>
                                    <?php if ($u['id'] != $_SESSION['user_id']): ?>
                                        <a href="users.php?delete=<?= $u['id'] ?>" class="btn btn-sm btn-outline-danger" onclick="return confirm('Yakin hapus anggota ini?');">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    <?php endif; ?>
                                </td>
                            </tr>

                            <!-- Modal Edit User -->
                            <div class="modal fade" id="modalEdit<?= $u['id'] ?>" tabindex="-1">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <form method="POST" action="">
                                            <input type="hidden" name="action_update" value="1">
                                            <input type="hidden" name="user_id" value="<?= $u['id'] ?>">
                                            <div class="modal-header">
                                                <h5 class="modal-title fw-bold">Edit Anggota: <?= htmlspecialchars($u['nama_lengkap']) ?></h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                            </div>
                                            <div class="modal-body">
                                                <div class="mb-3">
                                                    <label class="form-label small fw-bold">Bagian Vokal (Posisi)</label>
                                                    <select name="posisi" class="form-select">
                                                        <option value="Sopran" <?= $u['posisi'] == 'Sopran' ? 'selected' : '' ?>>Sopran</option>
                                                        <option value="Alto" <?= $u['posisi'] == 'Alto' ? 'selected' : '' ?>>Alto</option>
                                                        <option value="Tenor" <?= $u['posisi'] == 'Tenor' ? 'selected' : '' ?>>Tenor</option>
                                                        <option value="Bas" <?= $u['posisi'] == 'Bas' ? 'selected' : '' ?>>Bas</option>
                                                    </select>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label small fw-bold">Hak Akses (Role)</label>
                                                    <select name="role" class="form-select">
                                                        <option value="member" <?= $u['role'] == 'member' ? 'selected' : '' ?>>Member (Penyanyi)</option>
                                                        <option value="admin" <?= $u['role'] == 'admin' ? 'selected' : '' ?>>Admin (Pengelola)</option>
                                                    </select>
                                                </div>
                                                <div class="mb-3">
                                                    <label class="form-label small fw-bold">Status Akun</label>
                                                    <select name="status" class="form-select">
                                                        <option value="active" <?= $u['status'] == 'active' ? 'selected' : '' ?>>Active (Aktif)</option>
                                                        <option value="inactive" <?= $u['status'] == 'inactive' ? 'selected' : '' ?>>Inactive (Nonaktif)</option>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                                                <button type="submit" class="btn btn-primary">Simpan Perubahan</button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Modal Tambah Anggota -->
<div class="modal fade" id="modalTambah" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <form method="POST" action="">
                <input type="hidden" name="action_add" value="1">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">Pendaftaran Anggota Choir Baru</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Nama Lengkap *</label>
                        <input type="text" name="nama_lengkap" class="form-control" required placeholder="cth: Maria Grace">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Username Login *</label>
                        <input type="text" name="username" class="form-control" required placeholder="cth: mariagrace">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Password *</label>
                        <input type="text" name="password" class="form-control" required value="choir123">
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label small fw-bold">Bagian Vokal</label>
                            <select name="posisi" class="form-select">
                                <option value="Sopran">Sopran</option>
                                <option value="Alto">Alto</option>
                                <option value="Tenor">Tenor</option>
                                <option value="Bas">Bas</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label small fw-bold">Role Akses</label>
                            <select name="role" class="form-select">
                                <option value="member">Member</option>
                                <option value="admin">Admin</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">No. WhatsApp / HP</label>
                        <input type="text" name="no_hp" class="form-control" placeholder="0812xxxxxxxx">
                    </div>
                    <div class="mb-3">
                        <label class="form-label small fw-bold">Email</label>
                        <input type="email" name="email" class="form-control" placeholder="email@example.com">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-primary">Simpan Anggota</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
