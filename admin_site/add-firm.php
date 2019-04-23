<?php

function generateRandomString($length = 5) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $characters_length = strlen($characters);
    $random_string = '';
    for ($i = 0; $i < $length; $i++) {
        $random_string .= $characters[rand(0, $characters_length - 1)];
    }
    return $random_string;
}

function generateCodedName($link, $db_name) {
    $coded_name = "";
    while (true) {
        $coded_name = generateRandomString(6);

        $sql = "SELECT 1 FROM $db_name.company WHERE coded_name = ?";

        if($stmt = mysqli_prepare($link, $sql)) {
            mysqli_stmt_bind_param($stmt, "s", $param_coded_name);
            $param_coded_name = $coded_name;

            if(mysqli_stmt_execute($stmt)) {
                mysqli_stmt_store_result($stmt);
                if(mysqli_stmt_num_rows($stmt) == 0) {
                    break;
                }
            } else {
                echo "Błąd! Spróbuj później.";
            }
        }
    }
    return $coded_name;
}

function set_privileges($link, $db_name, $company_id) {
    $sql = "INSERT INTO $db_name.privilege(privilege) VALUES ('COMPANY')";
    if($stmt = mysqli_prepare($link, $sql)){
        if(mysqli_stmt_execute($stmt)) {
            $sql = "SELECT LAST_INSERT_ID()";
            if($stmt = mysqli_prepare($link, $sql)){
                if(mysqli_stmt_execute($stmt)) {
                    mysqli_stmt_store_result($stmt);       
                    mysqli_stmt_bind_result($stmt, $privilege_id);
                    mysqli_stmt_fetch($stmt);

                    $sql = "INSERT into $db_name.user_privilege(user_id, privilege_id) values(?,?)";
                    if($stmt = mysqli_prepare($link, $sql)){
                        mysqli_stmt_bind_param($stmt, "dd", $company_id, $privilege_id);
                        if(!mysqli_stmt_execute($stmt)) {
                            echo "Błąd! Spróbuj później.";
                        }
                    } else {
                        echo "Błąd! Spróbuj później.";
                    }
                } else {
                    echo "Błąd! Spróbuj później.";
                }
            }
        }
    }
}

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

function send_login_data($login, $password, $email, $name) {
    header('Content-type: text/html; charset=utf-8');

    require 'phpmailer/src/Exception.php';
    require 'phpmailer/src/PHPMailer.php';
    require 'phpmailer/src/SMTP.php';

    date_default_timezone_set('Europe/Warsaw');

    $mail = new PHPMailer(true);
    try {
        $mail->isSMTP();
        $mail->Host = 'smtp.gmail.com';
        $mail->SMTPAuth = true;
        $mail->Username = "terminy.io@gmail.com";
        $mail->Password = "terminy1!";
        $mail->SMTPSecure = 'tls';
        $mail->Port = 587;

        $mail->CharSet = "UTF-8";
        $mail->setLanguage('pl', '/phpmailer/language');

        $mail->setFrom('terminy.io@gmail.com', 'Terminy - administrator');
        $mail->addAddress($email, $name);

        $mail->Subject = "[TERMINY] Dane do logowania";
        $mail->Body = "login: " . $login . " hasło: " . $password;

        $mail->send();

    } catch (Exception $e) {
        echo $e->getMessage();
        throw new Exception("Mail error", 1);
        
    }
}

session_start();
 
if(!isset($_SESSION["loggedin"]) || $_SESSION["loggedin"] !== true){
    header("location: index.php");
    exit;
}
 
require_once "config.php";
 
$name = $coded_name = $login = $password = $email = $phone = "";
$login_err = $email_err = $phone_err = "";
 
if($_SERVER["REQUEST_METHOD"] == "POST") {

    $sql = "SELECT id FROM $db_name.base_entity WHERE login = ?";
    if($stmt = mysqli_prepare($link, $sql)) {
        mysqli_stmt_bind_param($stmt, "s", $param_login);
        $param_login = trim($_POST["login"]);

        if(mysqli_stmt_execute($stmt)) {
            mysqli_stmt_store_result($stmt);
            if(mysqli_stmt_num_rows($stmt) == 1) {
                $login_err = "Podany login istnieje.";
            } else{
                $login = trim($_POST["login"]);
            }
        } else {
            echo "Błąd! Spróbuj później.";
        }
    }

    $email = trim($_POST["email"]);
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $email_err = "Niepoprawny format.";
    }

    $phone = trim($_POST["phone"]);
    if(!preg_match("/^[0-9]+$/", $phone)) {
      $phone_err = "Niepoprawny format.";
    }

    
    if(empty($login_err) && empty($email_err) && empty($phone_err)) {

        $sql = "INSERT INTO $db_name.base_entity(login, password) VALUES(?, ?)";

        if($stmt = mysqli_prepare($link, $sql)){
            mysqli_stmt_bind_param($stmt, "ss", $param_login, $param_password);
            
            $param_login = $login;
            $random_password = generateRandomString();
            $hashed_password = password_hash($random_password, PASSWORD_DEFAULT);
            $param_password = str_replace("$2y$", "$2a$", "$hashed_password"); // CONVERSION TO SPRING

            if(mysqli_stmt_execute($stmt)) {
                $sql = "SELECT id FROM $db_name.base_entity WHERE login = ?";

                if($stmt = mysqli_prepare($link, $sql)){
                    mysqli_stmt_bind_param($stmt, "s", $param_login);
                    
                    $param_login = $login;

                    if(mysqli_stmt_execute($stmt)) {
                        mysqli_stmt_store_result($stmt);       
                        mysqli_stmt_bind_result($stmt, $id);
                        mysqli_stmt_fetch($stmt);

                        $name = trim($_POST["name"]);
                        $coded_name = generateCodedName($link, $db_name);
                        $blocking_users = isset($_POST['feature-block-users']) ? 1 : 0;
                        $mail_notification = isset($_POST['feature-mail-notification']) ? 1 : 0;
                        $reports_generation = isset($_POST['feature-reports-generation']) ? 1 : 0;

                        $sql = "INSERT INTO $db_name.company(id, name, coded_name, mail, phone, blocking_users, mail_notification, reports_generation) 
                        VALUES(?, ?, ?, ?, ?, $blocking_users, $mail_notification, $reports_generation)";

                        if($stmt = mysqli_prepare($link, $sql)){
                            mysqli_stmt_bind_param($stmt, "issss", $id, $name, $coded_name, $email, $phone);
                            if(mysqli_stmt_execute($stmt)) {
                                send_login_data($login, $random_password, $email, $name);
                                header("location: home.php?status=added");
                                echo "Poprawnie dodano firmę.";
                            }
                            else {
                                echo "Błąd! Spróbuj później.";
                            }
                        } else {
                            echo "Błąd! Spróbuj później.";
                        }

                        set_privileges($link, $db_name, $id);
                    }
                }
            } else{
                echo "Błąd! Spróbuj później.";
            }
        }
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
        .wrapper{ width: 800px; padding: 20px; margin:auto; }
    </style>
</head>
<body>
    <div class="wrapper">
        <form class="form-horizontal" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post">
        <fieldset>
        <legend><h1>Dodaj nową firmę</h1></legend>

        <div class="form-group">
            <label class="col-md-4 control-label" for="name">Nazwa firmy</label>  
            <div class="col-md-4">
            <input id="name" name="name" type="text" placeholder="Nazwa firmy" class="form-control input-md" required="" value="<?php echo $name; ?>">
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-4 control-label" for="login">Login</label>  
            <div class="col-md-4">
            <input id="login" name="login" type="text" placeholder="Login" class="form-control input-md" required="" value="<?php echo $login; ?>">
            <span class="help-block"><?php echo $login_err; ?></span>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-4 control-label" for="email">E-mail</label>  
            <div class="col-md-4">
            <input id="email" name="email" type="text" placeholder="E-mail" class="form-control input-md" required="" value="<?php echo $email; ?>">
            <span class="help-block"><?php echo $email_err; ?></span>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-4 control-label" for="phone">Telefon</label>  
            <div class="col-md-4">
            <input id="phone" name="phone" type="text" placeholder="Telefon" class="form-control input-md" required="" value="<?php echo $phone; ?>"> 
            <span class="help-block"><?php echo $phone_err; ?></span>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-4 control-label" for="features">Dodatkowe funkcjonalności</label>
            <div class="col-md-4">
                <div class="checkbox">
                    <label for="feature-block-users">
                        <input type="checkbox" name="feature-block-users" id="feature-block-users" value="1">
                        Blokowanie niechcianych użytkowników
                    </label>
                </div>
                <div class="checkbox">
                    <label for="feature-mail-notification">
                        <input type="checkbox" name="feature-mail-notification" id="feature-mail-notification" value="2">
                        Powiadomienia mailowe
                    </label>
                </div>
                <div class="checkbox">
                    <label for="feature-reports-generation">
                        <input type="checkbox" name="feature-reports-generation" id="feature-reports-generation" value="3">
                        Generowanie raportów
                    </label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-4 control-label" for="submit"></label>
            <div class="col-md-4">
                <button id="submit" name="submit" class="btn btn-primary">Dodaj</button>
                <a class="btn btn-link" href="home.php">Powrót</a>
            </div>
        </div>
        </fieldset>
        </form>
        <b style="font-style: italic"> Po dodaniu, na podanego e-maila zostane zostaną wysłane dane niezbędne do logowania w aplikacji.</b>
    </div>    
</body>
</html>