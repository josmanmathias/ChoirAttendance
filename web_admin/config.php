<?php
/**
 * SION CHOIR APP - Web Admin & API Backend
 * JKI Anugrah Ecclesia Cirebon
 * File: config.php
 */

session_start();

// Database Configuration
define('DB_HOST', 'localhost');
define('DB_PORT', '3306');
define('DB_NAME', 'terbang2_absensiqr');
define('DB_USER', 'root'); // Sesuaikan dengan user database server Anda
define('DB_PASS', '');     // Sesuaikan dengan password database server Anda

// Geofence Gereja JKI Anugrah Ecclesia Cirebon
define('CHURCH_LATITUDE', -6.728251);
define('CHURCH_LONGITUDE', 108.553246);
define('CHURCH_ALLOWED_RADIUS_METERS', 100);

try {
    $pdo = new PDO(
        "mysql:host=" . DB_HOST . ";port=" . DB_PORT . ";dbname=" . DB_NAME . ";charset=utf8mb4",
        DB_USER,
        DB_PASS,
        [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ]
    );
} catch (PDOException $e) {
    // Jika koneksi gagal, hentikan eksekusi dengan pesan yang jelas
    die("Koneksi Database Gagal: " . $e->getMessage());
}

// Auto-create missing auxiliary tables if needed
$pdo->exec("
CREATE TABLE IF NOT EXISTS `chsn_posters` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `subtitle` VARCHAR(255) NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `action_text` VARCHAR(50) DEFAULT 'Lihat Detail',
  `is_active` TINYINT(1) DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `chsn_kotbah` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `preacher` VARCHAR(100) NOT NULL,
  `date_text` VARCHAR(50) NOT NULL,
  `duration` VARCHAR(20) DEFAULT '40:00',
  `youtube_id` VARCHAR(50) NOT NULL,
  `category` ENUM('IBADAH_RAYA', 'PW_PAGI', 'PESAN_GEMBALA') NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `chsn_materi` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `subtitle` VARCHAR(255) NULL,
  `voice_part` ENUM('Semua', 'Sopran', 'Alto', 'Tenor', 'Bas') DEFAULT 'Semua',
  `pdf_url` VARCHAR(500) NULL,
  `audio_url` VARCHAR(500) NULL,
  `lyrics` TEXT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `chsn_galeri` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `date_text` VARCHAR(50) NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `description` TEXT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `chsn_settings` (
  `setting_key` VARCHAR(50) PRIMARY KEY,
  `setting_value` TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
");

// Helper function: Check if user is logged in as admin
function require_admin() {
    if (!isset($_SESSION['user_id']) || $_SESSION['role'] !== 'admin') {
        header("Location: index.php?msg=Silakan+login+sebagai+admin+terlebih+dahulu");
        exit;
    }
}

// Helper function: Distance between coordinates (Haversine Formula)
function calculate_distance_meters($lat1, $lon1, $lat2, $lon2) {
    $earthRadius = 6371000; // Radius bumi dalam meter
    $dLat = deg2rad($lat2 - $lat1);
    $dLon = deg2rad($lon2 - $lon1);
    $a = sin($dLat/2) * sin($dLat/2) +
         cos(deg2rad($lat1)) * cos(deg2rad($lat2)) *
         sin($dLon/2) * sin($dLon/2);
    $c = 2 * atan2(sqrt($a), sqrt(1-$a));
    return $earthRadius * $c;
}
?>
