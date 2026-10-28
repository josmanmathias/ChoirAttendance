# PANDUAN LENGKAP WEB ADMIN & BACKEND API - SION CHOIR APP
**Gereja JKI Anugrah Ecclesia Cirebon**  
*Database MySQL: `terbang2_absensiqr`*

---

## 📁 Struktur File `web_admin`

```
web_admin/
├── config.php          # Konfigurasi Database PDO, Geofence GPS (-6.728251, 108.553246), Auto-create table
├── index.php           # Halaman Login Admin PC (Bootstrap 5 modern)
├── dashboard.php       # Dashboard Statistik Suara (Sopran, Alto, Tenor, Bas), Grafik & Absensi Terbaru
├── users.php           # Kelola Data Anggota Choir (Tambah, Edit, Hapus, Filter Suara)
├── absensi.php         # Monitoring & Rekap Absensi Latihan & Ibadah + Filter Tanggal & Export CSV
├── posters.php         # Kelola Carousel Poster Slider Beranda Mobile
├── kotbah.php          # Kelola Video YouTube (Kotbah Ibadah Raya, PW Pagi, Pesan Gembala)
├── materi.php          # Kelola Materi Partitur PDF & Audio Player SATB
├── jadwal.php          # Kelola URL Jadwal Kegiatan & Galeri Foto Choir
├── logout.php          # Keluar sesi Admin
├── database.sql        # Skrip SQL untuk Import ke phpMyAdmin
├── README.md           # Panduan ini
└── api/                # REST API untuk Aplikasi Android
    ├── login.php       # Endpoint Otentikasi User (Session & Token)
    ├── absensi.php     # Endpoint Submit Absen (Selfie/QR + Validasi Geofence 100m)
    ├── rekap.php       # Endpoint Riwayat Absen Anggota
    └── content.php     # Endpoint Dynamic Content (Poster, Kotbah, Materi, Galeri)
```

---

## 🚀 Langkah Instalasi ke Server (`https://terbangcirebon.sch.id`)

### 1. Upload Folder `web_admin` ke Hosting / Server
- Buka **cPanel** atau hubungkan via **FTP** (FileZilla) ke hosting Anda.
- Masuk ke folder `public_html/`.
- Anda bisa meletakkan folder ini langsung di `public_html/web_admin/` atau di `public_html/jki/ministry/choir/admin/`.
- Upload seluruh isi folder `web_admin` ke direktori tersebut.

### 2. Konfigurasi Koneksi Database di `config.php`
Buka file `config.php` melalui File Manager cPanel / teks editor:
```php
define('DB_HOST', 'localhost');
define('DB_PORT', '3306');
define('DB_NAME', 'terbang2_absensiqr');
define('DB_USER', 'username_cpanel_anda'); // Masukkan user database MySQL Anda
define('DB_PASS', 'password_cpanel_anda'); // Masukkan password database MySQL Anda
```

### 3. Import Schema & Data Awal ke phpMyAdmin (Opsional/Jika Diperlukan)
- Buka **phpMyAdmin** di cPanel Anda.
- Pilih database `terbang2_absensiqr`.
- Klik tab **Import**, lalu pilih file `database.sql` dan klik **Go / Kirim**.
- *Catatan:* File `config.php` juga telah dilengkapi auto-create table (`CREATE TABLE IF NOT EXISTS`), sehingga tabel baru (`chsn_posters`, `chsn_kotbah`, `chsn_materi`, `chsn_galeri`, `chsn_settings`) otomatis terbentuk saat halaman web pertama kali dibuka.

---

## 🔑 Akun Login Admin & Member Default

| Username | Password | Role | Suara / Posisi | Keterangan |
| :--- | :--- | :--- | :--- | :--- |
| **admin** | **admin123** | `admin` | Tenor | Akun Administrator Web |
| **hendra** | **123456** | `member` | Tenor | Anggota Choir |
| **maria** | **123456** | `member` | Sopran | Anggota Choir |
| **david** | **123456** | `member` | Bas | Anggota Choir |
| **ester** | **123456** | `member` | Alto | Anggota Choir |

> *Catatan Keamanan:* Setelah login pertama kali, disarankan untuk mengubah password admin di menu **Data Anggota**.

---

## 🖥️ Fitur-Fitur Web Admin PC

1. **Dashboard Utama:**
   - Kartu statistik total anggota & rincian per kategori suara (**Sopran**, **Alto**, **Tenor**, **Bas**).
   - Rangkuman absensi hari ini (Latihan vs Ibadah).
   - Tabel 10 aktivitas absensi terakhir lengkap dengan status geofence jarak GPS dan tautan langsung ke Google Maps.

2. **Manajemen Anggota (`users.php`):**
   - Tambah anggota baru dengan posisi vokal (`Sopran`, `Alto`, `Tenor`, `Bas`) dan peran (`member` / `admin`).
   - Ubah username, password, email, dan nomor HP anggota.
   - Filter cepat berdasarkan bagian suara.

3. **Rekap Absensi (`absensi.php`):**
   - Monitoring riwayat absensi latihan dan ibadah.
   - Filter berdasarkan rentang tanggal.
   - Tombol **Export ke CSV / Excel** untuk arsip kehadiran mingguan dan bulanan.

4. **Carousel Slider (`posters.php`):**
   - Tambah/edit poster promo atau pengumuman yang muncul di carousel slider aplikasi mobile.
   - Atur status aktif/non-aktif setiap poster.

5. **Kotbah & Pesan Gembala (`kotbah.php`):**
   - Input ID Video YouTube atau link lengkap untuk:
     - *Kotbah Ibadah Raya*
     - *Kotbah PW Pagi*
     - *Pesan Gembala*
   - Video dapat diputar langsung di aplikasi Android maupun web.

6. **Materi Pelayanan SATB (`materi.php`):**
   - Input link partitur PDF dan file Audio Latihan per bagian vokal (Sopran, Alto, Tenor, Bas, atau Semua).
   - Dilengkapi lirik lagu untuk memudahkan anggota menghafal.

7. **Jadwal & Galeri Kegiatan (`jadwal.php`):**
   - Perbarui tautan gambar Jadwal Pelayanan Choir.
   - Tambah dokumentasi foto kegiatan paduan suara ke Galeri.

---

## 📡 Integrasi REST API untuk Aplikasi Android

Aplikasi Android terhubung ke server melalui endpoint JSON berikut:

1. **Login Anggota:**
   - `POST /web_admin/api/login.php`
   - Body JSON: `{"username": "...", "password": "..."}`
   - Mengembalikan data session pengguna, role, dan posisi vokal.

2. **Submit Absensi (Latihan / Ibadah):**
   - `POST /web_admin/api/absensi.php`
   - Body JSON:
     ```json
     {
       "user_id": 1,
       "type": "latihan",        // "latihan" atau "ibadah"
       "method": "selfie",       // "selfie" atau "qrcode"
       "foto": "base64_or_url",
       "latitude": -6.728251,
       "longitude": 108.553246
     }
     ```
   - Server memvalidasi geofence GPS Gereja JKI Anugrah Ecclesia Cirebon (radius maksimal 100 meter).

3. **Riwayat Absensi Pengguna:**
   - `GET /web_admin/api/rekap.php?user_id=1&type=all`

4. **Konten Dinamis (Poster, Kotbah, Materi, Galeri, Jadwal):**
   - `GET /web_admin/api/content.php?type=all`
