<?php
/**
 * SION CHOIR APP - API Login
 * Endpoint: /api/login.php
 */
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => false, "message" => "Method not allowed", "user" => null]);
    exit;
}

$username = trim($_POST['username'] ?? '');
$password = trim($_POST['password'] ?? '');

// Handle raw JSON input jika dipanggil via retrofit application/json
if (empty($username)) {
    $input = json_decode(file_get_contents('php://input'), true);
    if ($input) {
        $username = trim($input['username'] ?? '');
        $password = trim($input['password'] ?? '');
    }
}

if (empty($username) || empty($password)) {
    echo json_encode(["status" => false, "message" => "Username dan password tidak boleh kosong", "user" => null]);
    exit;
}

$stmt = $pdo->prepare("SELECT * FROM chsn_users WHERE username = ? LIMIT 1");
$stmt->execute([$username]);
$user = $stmt->fetch();

if ($user && ($password === $user['password'] || password_verify($password, $user['password']))) {
    if ($user['status'] !== 'active') {
        echo json_encode(["status" => false, "message" => "Akun Anda berstatus nonaktif", "user" => null]);
        exit;
    }

    echo json_encode([
        "status" => true,
        "message" => "Login berhasil",
        "user" => [
            "id" => (int)$user['id'],
            "username" => $user['username'],
            "nama_lengkap" => $user['nama_lengkap'],
            "email" => $user['email'],
            "no_hp" => $user['no_hp'],
            "posisi" => $user['posisi'],
            "role" => $user['role'],
            "foto" => $user['foto'],
            "status" => $user['status'],
            "created_at" => $user['created_at'],
            "updated_at" => $user['updated_at']
        ]
    ]);
} else {
    echo json_encode(["status" => false, "message" => "Username atau password salah", "user" => null]);
}
?>
