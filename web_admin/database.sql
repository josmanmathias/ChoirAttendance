-- ========================================================
-- SION CHOIR APP - SQL Schema & Initial Data
-- Gereja JKI Anugrah Ecclesia Cirebon
-- Database: terbang2_absensiqr
-- ========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- --------------------------------------------------------
-- Tabel: chsn_users (Data Pengguna / Anggota Choir)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `chsn_users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nama_lengkap` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `no_hp` VARCHAR(20) DEFAULT NULL,
  `posisi` ENUM('Sopran','Alto','Tenor','Bas') DEFAULT NULL,
  `role` ENUM('member','admin') DEFAULT 'member',
  `foto` VARCHAR(255) DEFAULT NULL,
  `status` ENUM('active','inactive') DEFAULT 'active',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_chsn_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Akun default untuk login awal:
-- Admin: admin / admin123
-- Member: hendra / 123456 (Tenor), maria / 123456 (Sopran), david / 123456 (Bas), ester / 123456 (Alto)
INSERT INTO `chsn_users` (`id`, `username`, `password`, `nama_lengkap`, `email`, `no_hp`, `posisi`, `role`, `status`) VALUES
(1, 'admin', 'admin123', 'Administrator Choir', 'admin@jkiecclesia.org', '081234567890', 'Tenor', 'admin', 'active'),
(2, 'hendra', '123456', 'Hendra Wijaya', 'hendra@jkiecclesia.org', '081298765432', 'Tenor', 'member', 'active'),
(3, 'maria', '123456', 'Maria Santoso', 'maria@jkiecclesia.org', '081345678901', 'Sopran', 'member', 'active'),
(4, 'david', '123456', 'David Kristianto', 'david@jkiecclesia.org', '081567890123', 'Bas', 'member', 'active'),
(5, 'ester', '123456', 'Ester Natalia', 'ester@jkiecclesia.org', '081678901234', 'Alto', 'member', 'active')
ON DUPLICATE KEY UPDATE `nama_lengkap`=VALUES(`nama_lengkap`);

-- --------------------------------------------------------
-- Tabel: chsn_absensi_latihan (Absensi Latihan Choir)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `chsn_absensi_latihan` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `foto` VARCHAR(255) DEFAULT NULL,
  `tanggal` DATE NOT NULL,
  `waktu` TIME NOT NULL,
  `latitude` DECIMAL(10,8) DEFAULT NULL,
  `longitude` DECIMAL(11,8) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_latihan_user` (`user_id`),
  KEY `idx_latihan_tanggal` (`tanggal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Tabel: chsn_absensi_ibadah (Absensi Pelayanan Ibadah)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `chsn_absensi_ibadah` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `foto` VARCHAR(255) DEFAULT NULL,
  `tanggal` DATE NOT NULL,
  `waktu` TIME NOT NULL,
  `latitude` DECIMAL(10,8) DEFAULT NULL,
  `longitude` DECIMAL(11,8) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ibadah_user` (`user_id`),
  KEY `idx_ibadah_tanggal` (`tanggal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------
-- Tabel: chsn_posters (Banner Slider Dashboard)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `chsn_posters` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `subtitle` VARCHAR(255) NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `action_text` VARCHAR(50) DEFAULT 'Lihat Detail',
  `is_active` TINYINT(1) DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `chsn_posters` (`id`, `title`, `subtitle`, `image_url`, `action_text`, `is_active`) VALUES
(1, 'Latihan Gabungan Sion Choir', 'Setiap Sabtu pukul 18.00 WIB di Main Hall JKI Anugrah', 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&q=80', 'Lihat Jadwal', 1),
(2, 'Pelayanan Ibadah Raya Minggu', 'Glory to God in the Highest - Mempersiapkan Hati Melayani', 'https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=800&q=80', 'Pelajari Materi', 1),
(3, 'Doa & Puasa Pelayan Altar', 'Memperdalam keintiman dengan Tuhan sebelum melayani', 'https://images.unsplash.com/photo-1445445290350-18a3b86e0b5b?w=800&q=80', 'Detail Acara', 1)
ON DUPLICATE KEY UPDATE `title`=VALUES(`title`);

-- --------------------------------------------------------
-- Tabel: chsn_kotbah (Video YouTube Kotbah & Pesan)
-- --------------------------------------------------------
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

INSERT INTO `chsn_kotbah` (`title`, `preacher`, `date_text`, `duration`, `youtube_id`, `category`) VALUES
('Penyembahan yang Menggetarkan Surga', 'Ps. Timotius Arifin', 'Minggu, 19 Okt 2026', '45:20', 'kXYiU_JCYtU', 'IBADAH_RAYA'),
('Kuasa Firman yang Menghidupkan', 'Ps. Daniel Yahya', 'Minggu, 12 Okt 2026', '52:10', '9bZkp7q19f0', 'IBADAH_RAYA'),
('Fajar Menyingsing, Berkat Melimpah', 'Ev. Samuel Santoso', 'Selasa, 21 Okt 2026', '32:15', 'kJQP7kiw5Fk', 'PW_PAGI'),
('Menjaga Hati Sebagai Pelayan Altar', 'Ps. Timotius Arifin', 'Senin, 20 Okt 2026', '18:40', '3tmd-ClpJxA', 'PESAN_GEMBALA');

-- --------------------------------------------------------
-- Tabel: chsn_materi (Partitur PDF & Audio SATB)
-- --------------------------------------------------------
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

INSERT INTO `chsn_materi` (`title`, `subtitle`, `voice_part`, `pdf_url`, `audio_url`, `lyrics`) VALUES
('How Great Thou Art (Arr. Dan Forrest)', 'Lagu Pembuka Ibadah Raya', 'Semua', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 'O Lord my God, when I in awesome wonder, consider all the worlds Thy hands have made...'),
('How Great Thou Art - Part Sopran', 'Latihan Suara 1 Sopran', 'Sopran', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3', 'Bagian melodi utama dan nada tinggi...'),
('How Great Thou Art - Part Alto', 'Latihan Suara 2 Alto', 'Alto', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3', 'Harmonisasi suara dua lembut dan agung...'),
('How Great Thou Art - Part Tenor', 'Latihan Suara 3 Tenor', 'Tenor', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3', 'Suara tiga melengkapi harmoni choir...'),
('How Great Thou Art - Part Bas', 'Latihan Suara 4 Bas', 'Bas', 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3', 'Pondasi bass yang kokoh dan mantap...');

-- --------------------------------------------------------
-- Tabel: chsn_galeri (Foto Kegiatan Choir)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `chsn_galeri` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `date_text` VARCHAR(50) NOT NULL,
  `image_url` VARCHAR(500) NOT NULL,
  `description` TEXT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `chsn_galeri` (`title`, `date_text`, `image_url`, `description`) VALUES
('Pelayanan Paskah Akbar 2026', 'April 2026', 'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&q=80', 'Pelayanan paduan suara di Ibadah Raya Paskah JKI Anugrah Ecclesia.'),
('Latihan Intensif Natal 2025', 'Desember 2025', 'https://images.unsplash.com/photo-1526478806334-5fd488fcaabc?w=800&q=80', 'Kebersamaan dan gladi resik menjelang konser malam Natal.'),
('Retreat & Fellowship Choir', 'Agustus 2025', 'https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=800&q=80', 'Membangun kebersamaan dan kekeluargaan antar anggota Sion Choir.');

-- --------------------------------------------------------
-- Tabel: chsn_settings (Konfigurasi Global)
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `chsn_settings` (
  `setting_key` VARCHAR(50) PRIMARY KEY,
  `setting_value` TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `chsn_settings` (`setting_key`, `setting_value`) VALUES
('jadwal_image_url', 'https://terbangcirebon.sch.id/jki/ministry/choir/jadwal.png')
ON DUPLICATE KEY UPDATE `setting_value`=VALUES(`setting_value`);

SET FOREIGN_KEY_CHECKS = 1;
