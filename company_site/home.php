<!--
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
    } elseif (isset($_GET["status"]) AND $_GET["status"]=="updated") {
        echo "<div class='alert alert-success' role='alert'>";
        echo "<b style='color: forestgreen'>Poprawnie zaktualizowano firmę</b>";
        echo "</div>";
    }
}
function generateRecords() {
    require_once "config.php";
    $sql = "SELECT f.id, name, coded_name, login, mail, phone FROM $db_name.company f join $db_name.base_entity s ON f.id=s.id";
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
 -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Terminy - strona Firmy</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.css">
    <style type="text/css">
        body{ font: 14px sans-serif; text-align: center; }
        .wrapper{ width: 350px; padding: 20px; margin:auto; }
    </style>
</head>
<body>
<nav class="navbar navbar-inverse">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>                        
      </button>
      <a class="navbar-brand" href="#">Terminy</a>
    </div>
    <div class="collapse navbar-collapse" id="myNavbar">
      <ul class="nav navbar-nav">
        <li class="active"><a href="home.html"><span class="glyphicon glyphicon-list"></span> Twoje usługi</a></li>
        <li><a href="account.html"><span class="glyphicon glyphicon-user"></span> Twoje konto</a></li>
        <li><a href="calendar.html"><span class="glyphicon glyphicon-calendar"></span> Kalendarz</a></li>
		<li><a href="add.html"><span class="glyphicon glyphicon-plus"></span> Dodaj usługę</a></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li><a href="index.html"><span class="glyphicon glyphicon-log-in"></span> Wyloguj</a></li>
      </ul>
    </div>
  </div>
</nav>

    <div class="wrapper">
        <div class="page-header">
            <h1>Twoje usługi</h1>
        </div>
        
		<div style="margin: 20px">
        	<h3>tu będzie generowany php</h3>
   	 	</div>
    </div>    
</body>
</html>