<?php

define('DB_SERVER', 'localhost:3306');
define('DB_USERNAME', 'tutorial');
define('DB_PASSWORD', 'Passw0rd');
define('DB_NAME', 'mojabaza');
 
try {
	$link = mysqli_connect(DB_SERVER, DB_USERNAME, DB_PASSWORD, DB_NAME);
} catch (Error $e) {
    echo 'Caught error: ',  $e->getMessage(), "\n";
}
 
if($link === false){
    die("Could not connect with DB. " . mysqli_connect_error());
}

$db_name = 'mojabaza';
?>