<?php
/**
 * SION CHOIR APP - Login Web Admin
 * JKI Anugrah Ecclesia Cirebon
 * File: index.php
 */
require_once 'config.php';

$error = '';

if (isset($_SESSION['user_id']) && $_SESSION['role'] === 'admin') {
    header("Location: dashboard.php");
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = trim($_POST['username'] ?? '');
    $password = trim($_POST['password'] ?? '');

    if (!empty($username) && !empty($password)) {
        $stmt = $pdo->prepare("SELECT * FROM chsn_users WHERE username = ? LIMIT 1");
        $stmt->execute([$username]);
        $user = $stmt->fetch();

        // Verifikasi password (password_verify hash atau plaintext fallback)
        if ($user && ($password === $user['password'] || password_verify($password, $user['password']))) {
            if ($user['role'] === 'admin') {
                if ($user['status'] === 'active') {
                    $_SESSION['user_id'] = $user['id'];
                    $_SESSION['username'] = $user['username'];
                    $_SESSION['nama_lengkap'] = $user['nama_lengkap'];
                    $_SESSION['role'] = $user['role'];
                    $_SESSION['posisi'] = $user['posisi'];

                    header("Location: dashboard.php");
                    exit;
                } else {
                    $error = "Akun ini sedang tidak aktif (inactive). Hubungi koordinator choir.";
                }
            } else {
                $error = "Akses ditolak: Akun Anda bukan berstatus Admin.";
            }
        } else {
            $error = "Username atau password salah.";
        }
    } else {
        $error = "Silakan masukkan username dan password.";
    }
}
?>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Admin - SION CHOIR APP | JKI Ecclesia</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #0A1324 0%, #0F1E36 50%, #1B2E52 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        }
        .login-card {
            background: #ffffff;
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.4);
            overflow: hidden;
            max-width: 440px;
            width: 100%;
        }
        .login-header {
            background: #0F1E36;
            color: #ffffff;
            padding: 30px 20px;
            text-align: center;
            border-bottom: 4px solid #D97706;
        }
        .login-body {
            padding: 35px 30px;
        }
        .btn-choir {
            background-color: #0F1E36;
            color: #FBBF24;
            font-weight: 700;
            border-radius: 10px;
            padding: 12px;
            transition: all 0.3s;
        }
        .btn-choir:hover {
            background-color: #1B2E52;
            color: #ffffff;
        }
        .logo-box {
            width: 85px;
            height: 85px;
            background: #ffffff;
            border-radius: 50%;
            margin: 0 auto 12px;
            padding: 6px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.3);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .logo-box img {
            max-width: 100%;
            border-radius: 50%;
        }
    </style>
</head>
<body>

<div class="container p-3">
    <div class="login-card mx-auto">
        <div class="login-header">
            <div class="logo-box">
                <i class="bi bi-music-note-beamed fs-1 text-primary"></i>
            </div>
            <h4 class="fw-bold mb-1" style="color: #FBBF24; letter-spacing: 1px;">SION CHOIR ADMIN</h4>
            <p class="mb-0 small text-white-50">JKI Anugrah Ecclesia Cirebon</p>
        </div>

        <div class="login-body">
            <?php if (!empty($error)): ?>
                <div class="alert alert-danger py-2 small" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-1"></i> <?= htmlspecialchars($error) ?>
                </div>
            <?php endif; ?>

            <form method="POST" action="">
                <div class="mb-3">
                    <label class="form-label fw-semibold text-secondary small">Username Admin</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-person text-muted"></i></span>
                        <input type="text" name="username" class="form-control border-start-0" placeholder="Masukkan username" required autofocus>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold text-secondary small">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light border-end-0"><i class="bi bi-lock text-muted"></i></span>
                        <input type="password" name="password" class="form-control border-start-0" placeholder="Masukkan password" required>
                    </div>
                </div>

                <div class="d-grid mt-4">
                    <button type="submit" class="btn btn-choir">
                        <i class="bi bi-box-arrow-in-right me-1"></i> MASUK PANEL ADMIN
                    </button>
                </div>
            </form>

            <div class="mt-4 pt-3 border-top text-center">
                <small class="text-muted d-block">Database: <strong>terbang2_absensiqr</strong></small>
                <small class="text-muted">Tabel: <strong>chsn_users</strong></small>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
