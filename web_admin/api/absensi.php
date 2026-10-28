<?php
/**
 * SION CHOIR APP - API Submit Absensi
 * Endpoint: /api/absensi.php
 */
header('Content-Type: application/json; charset=utf-8');
require_once '../config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => false, "message" => "Method not allowed"]);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
if (!$input) {
    $input = $_POST;
}

$user_id = (int)($input['user_id'] ?? 0);
$tanggal = $input['tanggal'] ?? date('Y-m-d');
$waktu = $input['waktu'] ?? date('H:i:s');
$latitude = isset($input['latitude']) ? (float)$input['latitude'] : null;
$longitude = isset($input['longitude']) ? (float)$input['longitude'] : null;
$foto = $input['foto'] ?? null;
$type = strtoupper($input['type'] ?? 'LATIHAN');

if ($user_id <= 0) {
    echo json_encode(["status" => false, "message" => "User ID tidak valid"]);
    exit;
}

// Cek Geofence (Radius 100m dari gereja)
if ($latitude !== null && $longitude !== null) {
    $distance = calculate_distance_meters($latitude, $longitude, CHURCH_LATITUDE, CHURCH_LONGITUDE);
    if ($distance > CHURCH_ALLOWED_RADIUS_METERS) {
        echo json_encode([
            "status" => false,
            "message" => "Di luar radius gereja. Jarak Anda: " . round($distance) . "m (Maksimal: 100m)"
        ]);
        exit;
    }
}

// Tentukan tabel target
$tableName = ($type === 'IBADAH') ? 'chsn_absensi_ibadah' : 'chsn_absensi_latihan';

try {
    $stmt = $pdo->prepare("
        INSERT INTO $tableName (user_id, foto, tanggal, waktu, latitude, longitude)
        VALUES (?, ?, ?, ?, ?, ?)
    ");
    $stmt->execute([$user_id, $foto, $tanggal, $waktu, $latitude, $longitude]);
    $newId = (int)$pdo->lastInsertId();

    echo json_encode([
        "status" => true,
        "message" => "Presensi " . ucfirst(strtolower($type)) . " berhasil dicatat!",
        "id" => $newId
    ]);
} catch (PDOException $e) {
    echo json_encode(["status" => false, "message" => "Gagal menyimpan presensi: " . $e->getMessage()]);
}
?>
