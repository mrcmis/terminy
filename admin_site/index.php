<?php

function db_init($link, $db_name) {
    $sql = "SELECT 1 FROM $db_name.base_entity WHERE login = 'admin'";
    if($stmt = mysqli_prepare($link, $sql)) {
        if(mysqli_stmt_execute($stmt)) {
            mysqli_stmt_store_result($stmt);
            if(mysqli_stmt_num_rows($stmt) == 0) {
                $sql = "INSERT INTO $db_name.base_entity(login, password) values('admin', 'admin')";
                if($stmt = mysqli_prepare($link, $sql)) {
                    if(!mysqli_stmt_execute($stmt)) {
                        echo "Błąd! Spróbuj później.";
                    }
                }

                $sql = "INSERT INTO $db_name.privilege(privilege) VALUES ('COMPANY'), ('BLOCKING_USERS'), ('MAIL_NOTIFICATION'), ('REPORTS_GENERATION')";
                if($stmt = mysqli_prepare($link, $sql)) {
                    if(!mysqli_stmt_execute($stmt)) {
                        echo "Błąd! Spróbuj później.";
                    }
                }
            }
        } else {
            echo "Błąd! Spróbuj później.";
        }
    }   
}

session_start();
 
if(isset($_SESSION["loggedin"]) && $_SESSION["loggedin"] === true){
    header("location: home.php");
    exit;
}

require_once "config.php";

db_init($link, $db_name);

 
$login = $password = "";
$login_err = $password_err = "";
 
if($_SERVER["REQUEST_METHOD"] == "POST"){
 
    if(empty(trim($_POST["login"]))){
        $login_err = "Wprowadź login.";
    } else{
        $login = trim($_POST["login"]);
    }
    
    if(empty(trim($_POST["password"]))){
        $password_err = "Wprowadź hasło.";
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
                            $password_err = "Podano niepoprawne hasło.";
                        }
                    }
                } else{
                    $login_err = "Brak takiego użytkownika.";
                }
            } else{
                echo "Błąd! Spróbuj później.";
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
    <title>Terminy - strona administratora</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.css">
    <style type="text/css">
        body{ font: 14px sans-serif; text-align: center; }
        .wrapper{ width: 350px; padding: 20px; margin:auto; }
    </style>
</head>
<body>
    <div class="wrapper">
        <div class="page-header">
            <h1>Logowanie</h1>
        </div>
        <form action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post">
            <div class="form-group <?php echo (!empty($login_err)) ? 'has-error' : ''; ?>">
                <label>Login</label>
                <input type="text" name="login" class="form-control" value="<?php echo $login; ?>">
                <span class="help-block"><?php echo $login_err; ?></span>
            </div>    
            <div class="form-group <?php echo (!empty($password_err)) ? 'has-error' : ''; ?>">
                <label>Hasło</label>
                <input type="password" name="password" class="form-control">
                <span class="help-block"><?php echo $password_err; ?></span>
            </div>
            <div class="form-group">
                <input type="submit" class="btn btn-primary" value="Zaloguj">
            </div>
        </form>
    </div>    
</body>
</html>