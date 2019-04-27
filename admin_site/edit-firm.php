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

function set_privileges($link, $db_name, $company_id, $privilege_name) {
    $sql = "INSERT INTO $db_name.privilege(privilege) VALUES ('" . $privilege_name . "')";
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

function remove_privileges($link, $db_name, $company_id, $privilege_name) {
    $sql = "SELECT id from $db_name.privilege p JOIN $db_name.user_privilege up ON p.id=up.privilege_id WHERE privilege='" . $privilege_name . "'
            AND user_id=" . $company_id;
    if($stmt = mysqli_prepare($link, $sql)){
        if(mysqli_stmt_execute($stmt)) {
            mysqli_stmt_store_result($stmt);       
            mysqli_stmt_bind_result($stmt, $privilege_id);
            mysqli_stmt_fetch($stmt);

            $sql = "DELETE FROM $db_name.user_privilege WHERE privilege_id=?";
            if($stmt = mysqli_prepare($link, $sql)){
                mysqli_stmt_bind_param($stmt, "d", $privilege_id);
                if(mysqli_stmt_execute($stmt)) {
                    $sql = "DELETE FROM $db_name.privilege WHERE id = ?";
                    if($stmt = mysqli_prepare($link, $sql)){
                        mysqli_stmt_bind_param($stmt, "d", $privilege_id);
                        if(!mysqli_stmt_execute($stmt)) {
                            echo "Błąd! Spróbuj później.";
                        }
                    }
                }
                else {
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

session_start();
 
if(!isset($_SESSION["loggedin"]) || $_SESSION["loggedin"] !== true){
    header("location: index.php");
    exit;
}

if(!isset($_GET["firm-id"]) && $_SERVER["REQUEST_METHOD"] !== "POST"){
    header("location: home.php");
    exit;
}

require_once "config.php";
 
$email_err = $phone_err = "";
$id = trim($_GET["firm-id"]);
$name = $mail = $phone = "";
$blocking_users = $mail_notification = $reports_generation = 0;

$sql = "SELECT name, mail, phone, blocking_users, mail_notification, reports_generation FROM $db_name.company WHERE id=?";

if($stmt = mysqli_prepare($link, $sql)) {
    mysqli_stmt_bind_param($stmt, "i", $id);
    if(mysqli_execute($stmt)) {
         mysqli_stmt_store_result($stmt);
        if(mysqli_stmt_num_rows($stmt) == 1) {
            mysqli_stmt_bind_result($stmt, $name, $mail, $phone, $blocking_users, $mail_notification, $reports_generation);
            if(mysqli_stmt_fetch($stmt)) {
            } else {
                echo "Błąd! Spróbuj później.";
            }
        } else {
            echo mysqli_error($link);
            echo "Brak firm w bazie danych.";
        }
    } else {
        echo mysqli_error($link);
        echo "Błąd! Spróbuj później.";
    }
    mysqli_stmt_close($stmt);
}

if($_SERVER["REQUEST_METHOD"] == "POST") {

    $new_mail = trim($_POST["email"]);
    if (!filter_var($new_mail, FILTER_VALIDATE_EMAIL)) {
        $email_err = "Niepoprawny format.";
    }

    $new_phone = trim($_POST["phone"]);
    if(!preg_match("/^[0-9]+$/", $new_phone)) {
      $phone_err = "Niepoprawny format.";
    }

    $new_name = trim($_POST["name"]);
    if(empty($email_err) && empty($phone_err)) {
        if($new_name != $name) {
            $sql = "UPDATE $db_name.company SET name=? WHERE id=?";
            if($stmt = mysqli_prepare($link, $sql)){
                mysqli_stmt_bind_param($stmt, "sd", $new_name, $id);
                if(mysqli_stmt_execute($stmt)) {
                    header("location: home.php?status=updated");
                }
                else {
                    echo "Błąd! Spróbuj później.";
                }      
            }
        }
        if($new_mail != $mail) {
            $sql = "UPDATE $db_name.company SET mail=? WHERE id=?";
            if($stmt = mysqli_prepare($link, $sql)){
                mysqli_stmt_bind_param($stmt, "sd", $new_mail, $id);
                if(mysqli_stmt_execute($stmt)) {
                    header("location: home.php?status=updated");
                }
                else {
                    echo "Błąd! Spróbuj później.";
                }      
            }
        }
        if($new_phone != $phone) {
            $sql = "UPDATE $db_name.company SET phone=? WHERE id=?";
            if($stmt = mysqli_prepare($link, $sql)){
                mysqli_stmt_bind_param($stmt, "sd", $new_phone, $id);
                if(mysqli_stmt_execute($stmt)) {
                    header("location: home.php?status=updated");
                }
                else {
                    echo "Błąd! Spróbuj później.";
                }      
            }
        }
        $new_blocking_users = isset($_POST['feature-block-users']) ? 1 : 0;
        $new_mail_notification = isset($_POST['feature-mail-notification']) ? 1 : 0;
        $new_reports_generation = isset($_POST['feature-reports-generation']) ? 1 : 0;

        if($new_blocking_users != $blocking_users) {
            if($new_blocking_users == 0) {
                remove_privileges($link, $db_name, $id, 'BLOCKING_USERS');
            }
            else {
                set_privileges($link, $db_name, $id, 'BLOCKING_USERS');
            }

            $sql = "UPDATE $db_name.company SET blocking_users=? WHERE id=?";
            if($stmt = mysqli_prepare($link, $sql)){
                mysqli_stmt_bind_param($stmt, "dd", $new_blocking_users, $id);
                if(mysqli_stmt_execute($stmt)) {
                    header("location: home.php?status=updated");
                }
                else {
                    echo "Błąd! Spróbuj później.";
                }      
            }
        }
        if($new_mail_notification != $mail_notification) {
            if($new_mail_notification == 0) {
                remove_privileges($link, $db_name, $id, 'MAIL_NOTIFICATION');
            }
            else {
                set_privileges($link, $db_name, $id, 'MAIL_NOTIFICATION');
            }

            $sql = "UPDATE $db_name.company SET mail_notification=? WHERE id=?";
            if($stmt = mysqli_prepare($link, $sql)){
                mysqli_stmt_bind_param($stmt, "dd", $new_mail_notification, $id);
                if(mysqli_stmt_execute($stmt)) {
                    header("location: home.php?status=updated");
                }
                else {
                    echo "Błąd! Spróbuj później.";
                }      
            }
        }
        if($new_reports_generation != $reports_generation) {
            if($new_reports_generation == 0) {
                remove_privileges($link, $db_name, $id, 'REPORTS_GENERATION');
            }
            else {
                set_privileges($link, $db_name, $id, 'REPORTS_GENERATION');
            }

            $sql = "UPDATE $db_name.company SET reports_generation=? WHERE id=?";
            if($stmt = mysqli_prepare($link, $sql)){
                mysqli_stmt_bind_param($stmt, "dd", $new_reports_generation, $id);
                if(mysqli_stmt_execute($stmt)) {
                    header("location: home.php?status=updated");
                }
                else {
                    echo "Błąd! Spróbuj później.";
                }      
            }
        }

    }
}
mysqli_close($link);

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
        <form class="form-horizontal" action="" method="post">
        <fieldset>
        <legend><h1>Edytuj dane firmy</h1></legend>

        <div class="form-group">
            <label class="col-md-4 control-label" for="name">Nazwa firmy</label>  
            <div class="col-md-4">
            <input id="name" name="name" type="text" placeholder="Nazwa firmy" class="form-control input-md" required="" value="<?php echo $name; ?>">
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-4 control-label" for="email">E-mail</label>  
            <div class="col-md-4">
            <input id="email" name="email" type="text" placeholder="E-mail" class="form-control input-md" required="" value="<?php echo $mail; ?>">
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
                        <input type="checkbox" name="feature-block-users" id="feature-block-users" value="1" 
                        <?php echo ($blocking_users) ? 'checked=""' : ''; ?>>
                        Blokowanie niechcianych użytkowników
                    </label>
                </div>
                <div class="checkbox">
                    <label for="feature-mail-notification">
                        <input type="checkbox" name="feature-mail-notification" id="feature-mail-notification" value="2"
                        <?php echo ($mail_notification) ? 'checked=""' : ''; ?>>
                        Powiadomienia mailowe
                    </label>
                </div>
                <div class="checkbox">
                    <label for="feature-phone-notification">
                        <input type="checkbox" name="feature-reports-generation" id="feature-reports-generation" value="3"
                        <?php echo ($reports_generation) ? 'checked=""' : ''; ?>>
                        Generowanie raportów
                    </label>
                </div>
            </div>
        </div>

        <div class="form-group">
            <label class="col-md-4 control-label" for="submit"></label>
            <div class="col-md-4">
                <button id="submit" name="submit" class="btn btn-primary">Edytuj</button>
                <a class="btn btn-link" href="home.php">Powrót</a>
            </div>
        </div>
        </fieldset>
        </form>
    </div>    
</body>
</html>