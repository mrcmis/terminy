<?php

define('DB_SERVER', 'remotemysql.com:3306');
define('DB_USERNAME', 'IoY9jiKVdl');
define('DB_PASSWORD', 'Xz2uf258MJ');
define('DB_NAME', 'IoY9jiKVdl');
 
try {
	$link = mysqli_connect(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_NAME);
} catch (Error $e) {
    echo 'Caught error: ',  $e->getMessage(), "\n";
}
 
if($link === false){
    die("Could not connect with DB. " . mysqli_connect_error());
}

$db_name = 'IoY9jiKVdl';
?>