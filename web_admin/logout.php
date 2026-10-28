<?php
/**
 * SION CHOIR APP - Logout Web Admin
 * File: logout.php
 */
session_start();
session_unset();
session_destroy();
header("Location: index.php?msg=Anda+telah+berhasil+keluar");
exit;
