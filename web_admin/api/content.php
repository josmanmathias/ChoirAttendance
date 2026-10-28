<?php
/**
 * SION CHOIR APP - Dynamic Content API
 * Endpoint: /web_admin/api/content.php?type=posters|kotbah|materi|galeri|jadwal|all
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

$type = isset($_GET['type']) ? strtolower(trim($_GET['type'])) : 'all';

try {
    $response = ['status' => 'success'];

    if ($type === 'posters' || $type === 'all') {
        $stmt = $pdo->query("SELECT * FROM chsn_posters WHERE is_active = 1 ORDER BY id DESC");
        $response['posters'] = $stmt->fetchAll();
    }

    if ($type === 'kotbah' || $type === 'all') {
        $category = isset($_GET['category']) ? strtoupper(trim($_GET['category'])) : null;
        if ($category && in_array($category, ['IBADAH_RAYA', 'PW_PAGI', 'PESAN_GEMBALA'])) {
            $stmt = $pdo->prepare("SELECT * FROM chsn_kotbah WHERE category = :cat ORDER BY id DESC");
            $stmt->execute([':cat' => $category]);
        } else {
            $stmt = $pdo->query("SELECT * FROM chsn_kotbah ORDER BY id DESC");
        }
        $response['kotbah'] = $stmt->fetchAll();
    }

    if ($type === 'materi' || $type === 'all') {
        $part = isset($_GET['voice_part']) ? trim($_GET['voice_part']) : null;
        if ($part && in_array($part, ['Semua', 'Sopran', 'Alto', 'Tenor', 'Bas'])) {
            $stmt = $pdo->prepare("SELECT * FROM chsn_materi WHERE voice_part = :part OR voice_part = 'Semua' ORDER BY id DESC");
            $stmt->execute([':part' => $part]);
        } else {
            $stmt = $pdo->query("SELECT * FROM chsn_materi ORDER BY id DESC");
        }
        $response['materi'] = $stmt->fetchAll();
    }

    if ($type === 'galeri' || $type === 'all') {
        $stmt = $pdo->query("SELECT * FROM chsn_galeri ORDER BY id DESC");
        $response['galeri'] = $stmt->fetchAll();
    }

    if ($type === 'jadwal' || $type === 'all') {
        $stmt = $pdo->prepare("SELECT setting_value FROM chsn_settings WHERE setting_key = 'jadwal_image_url'");
        $stmt->execute();
        $val = $stmt->fetchColumn();
        $response['jadwal_url'] = $val ?: 'https://terbangcirebon.sch.id/jki/ministry/choir/jadwal.png';
    }

    echo json_encode($response);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Terjadi kesalahan server: ' . $e->getMessage()
    ]);
}
?>
