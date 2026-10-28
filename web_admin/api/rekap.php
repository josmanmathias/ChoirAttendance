<?php
/**
 * SION CHOIR APP - Rekap Absensi API
 * Endpoint: /web_admin/api/rekap.php?user_id=X&type=latihan|ibadah|all
 */

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once '../config.php';

$userId = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;
$type = isset($_GET['type']) ? strtolower(trim($_GET['type'])) : 'all';

if ($userId <= 0) {
    http_response_code(400);
    echo json_encode([
        'status' => 'error',
        'message' => 'Parameter user_id diperlukan'
    ]);
    exit;
}

try {
    $results = [];

    if ($type === 'latihan' || $type === 'all') {
        $stmt = $pdo->prepare("
            SELECT id, user_id, foto, tanggal, waktu, latitude, longitude, created_at, 'LATIHAN' as kategori
            FROM chsn_absensi_latihan
            WHERE user_id = :uid
            ORDER BY tanggal DESC, waktu DESC
            LIMIT 50
        ");
        $stmt->execute([':uid' => $userId]);
        $latihan = $stmt->fetchAll();
        $results = array_merge($results, $latihan);
    }

    if ($type === 'ibadah' || $type === 'all') {
        $stmt = $pdo->prepare("
            SELECT id, user_id, foto, tanggal, waktu, latitude, longitude, created_at, 'IBADAH' as kategori
            FROM chsn_absensi_ibadah
            WHERE user_id = :uid
            ORDER BY tanggal DESC, waktu DESC
            LIMIT 50
        ");
        $stmt->execute([':uid' => $userId]);
        $ibadah = $stmt->fetchAll();
        $results = array_merge($results, $ibadah);
    }

    // Sort combined by tanggal DESC, waktu DESC
    usort($results, function($a, $b) {
        $cmp = strcmp($b['tanggal'], $a['tanggal']);
        if ($cmp === 0) {
            return strcmp($b['waktu'], $a['waktu']);
        }
        return $cmp;
    });

    // Summary counts
    $stmtCountLatihan = $pdo->prepare("SELECT COUNT(*) FROM chsn_absensi_latihan WHERE user_id = :uid");
    $stmtCountLatihan->execute([':uid' => $userId]);
    $totalLatihan = (int)$stmtCountLatihan->fetchColumn();

    $stmtCountIbadah = $pdo->prepare("SELECT COUNT(*) FROM chsn_absensi_ibadah WHERE user_id = :uid");
    $stmtCountIbadah->execute([':uid' => $userId]);
    $totalIbadah = (int)$stmtCountIbadah->fetchColumn();

    echo json_encode([
        'status' => 'success',
        'summary' => [
            'total_latihan' => $totalLatihan,
            'total_ibadah' => $totalIbadah,
            'total_semua' => $totalLatihan + $totalIbadah
        ],
        'data' => $results
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Terjadi kesalahan server: ' . $e->getMessage()
    ]);
}
?>
