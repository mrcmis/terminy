<?php
session_start();
 
if(isset($_SESSION["loggedin"]) && $_SESSION["loggedin"] === true){
    header("location: home.php");
    exit;
}
 
require_once "config.php";
 
$login = $password = "";
$login_err = $password_err = "";
 
if($_SERVER["REQUEST_METHOD"] == "POST"){
 
    if(empty(trim($_POST["login"]))){
        $login_err = "Wprowadz login.";
    } else{
        $login = trim($_POST["login"]);
    }
    
    if(empty(trim($_POST["password"]))){
        $password_err = "Wprowadz haslo.";
    } else{
        $password = trim($_POST["password"]);
    }
    
    if(empty($login_err) && empty($password_err)){
        $sql = "SELECT id, login, password FROM $db_name.base_entity WHERE login = ?";
        if($stmt = mysqli_prepare($link, $sql)){
            mysqli_stmt_bind_param($stmt, "s", $param_login);
            
            $param_login = $login;
            if(mysqli_stmt_execute($stmt)){
                mysqli_stmt_store_result($stmt);
                
                if(mysqli_stmt_num_rows($stmt) == 1){                    
                    mysqli_stmt_bind_result($stmt, $id, $login, $db_password);
                    if(mysqli_stmt_fetch($stmt)){
                        if($password == $db_password){
                            session_start();
                            
                            $_SESSION["loggedin"] = true;
                            $_SESSION["id"] = $id;
                            $_SESSION["login"] = $login;                            
                            
                            header("location: home.php");
                        } else{
                            $password_err = "Podano niepoprawne haslo.";
                        }
                    }
                } else{
                    $login_err = "Brak takiego uzytkownika.";
                }
            } else{
                echo "Blad! Spr�buj p�zniej.";
            }
        }
            mysqli_stmt_close($stmt);
    }
    mysqli_close($link);
}
?>
 
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
        <li><a href="home.html"><span class="glyphicon glyphicon-list"></span> Twoje uslugi</a></li>
        <li><a href="account.html"><span class="glyphicon glyphicon-user"></span> Twoje konto</a></li>
        <li><a href="calendar.html"><span class="glyphicon glyphicon-calendar"></span> Kalendarz</a></li>
		<li class="active"><a href="add.html"><span class="glyphicon glyphicon-plus"></span> Dodaj usluge</a></li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li><a href="index.html"><span class="glyphicon glyphicon-log-in"></span> Wyloguj</a></li>
      </ul>
    </div>
  </div>
</nav>

    <div class="wrapper">
        <div class="page-header">
            <h1>Dodaj nową usługę</h1>
        </div>
        <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post">
            <div class="form-group <?php echo (!empty($login_err)) ? 'has-error' : ''; ?>">
                <label>Nazwa usługi</label>
                <input type="text" name="login" class="form-control" value="<?php echo $login; ?>">
                <span class="help-block"><?php echo $login_err; ?></span>
            </div>    
            <div class="form-group <?php echo (!empty($password_err)) ? 'has-error' : ''; ?>">
                <label>Czas trwania</label>
                <input type="number" name="time" class="form-control">
                <span class="help-block"><?php echo $password_err; ?></span>
            </div>
			    <div class="form-group <?php echo (!empty($password_err)) ? 'has-error' : ''; ?>">
                <label>Cena</label>
                <input type="number" name="cena" class="form-control">
                <span class="help-block"><?php echo $password_err; ?></span>
            </div>
            <div class="form-group">
                <input type="submit" class="btn btn-primary" value="Dodaj usługę">
            </div>
        </form>
    </div>    
</body>
</html>