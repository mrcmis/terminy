<?php

session_start();
 
if(!isset($_SESSION["loggedin"]) || $_SESSION["loggedin"] !== true){
    header("location: index.php");
    exit;
}

function addAlert() {
    if(isset($_GET["status"]) AND $_GET["status"]=="added") {
        echo "<div class='alert alert-success' role='alert'>";
        echo "<b style='color: forestgreen'>Poprawnie dodano firmę</b>";
        echo "</div>";
    }
}

function generateRecords() {
    require_once "config.php";

    $sql = "SELECT f.id, name, coded_name, login, mail, phone FROM $db_name.firm f join $db_name.base_entity s ON f.id=s.id";
    if($result = mysqli_query($link, $sql)){
        if(mysqli_num_rows($result) > 0){
            echo "<table class='table table-hover'>";
                echo "<thead>";
                    echo "<tr>";
                        echo "<th>Nazwa</th>";
                        echo "<th>Nazwa szyfrowana</th>";
                        echo "<th>Login</th>";
                        echo "<th>E-mail</th>";
                        echo "<th>Telefon</th>";
                    echo "</tr>";
                echo "</thead>";
            while($row = mysqli_fetch_array($result)){
                echo "<tr style='text-align: left;'>";
                    echo "<td>" . $row['name'] . "</td>";
                    echo "<td>" . $row['coded_name'] . "</td>";
                    echo "<td>" . $row['login'] . "</td>";
                    echo "<td>" . $row['mail'] . "</td>";
                    echo "<td>" . $row['phone'] . "</td>";
                    echo "<td><a class='btn btn-link' href='edit-firm.php?firm-id=" . $row['id'] . "'>Edytuj dane</a></td>";
                echo "</tr>";
            }
            echo "</table>";
            mysqli_free_result($result);
        } else{
            echo "Brak firm w bazie danych.";
        }
    } else{
        echo "Błąd! Spróbuj później.";
    }
    mysqli_close($link);

}
?>
 
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Terminy - strona administratora</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.css">
    <style type="text/css">
        body{ font: 14px sans-serif; text-align: center; margin:auto; }
    </style>
</head>
<body>
    <div>
        <?php addAlert() ?>
    </div>
    <div class="page-header">
        <h1>Zarządzaj firmami</h1>
    </div>
    <div style="margin: 20px">
        <?php generateRecords() ?>
    </div>
    <div style="padding: 20px">
        <a href="add-firm.php" class="btn btn-primary">Dodaj firmę</a>
    </div>
    <p>
        <a href="reset-password.php" class="btn btn-info">Zmień hasło</a>
        <a href="logout.php" class="btn btn-danger">Wyloguj</a>
    </p>
</body>
</html>