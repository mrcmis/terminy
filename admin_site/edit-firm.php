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

session_start();
 
if(!isset($_SESSION["loggedin"]) || $_SESSION["loggedin"] !== true){
    header("location: index.php");
    exit;
}

if(!isset($_GET["firm-id"])){
    header("location: home.php");
    exit;
}

require_once "config.php";
 
$email_err = $phone_err = "";
$id = trim($_GET["firm-id"]);
$name = $mail = $phone = "";
$blocking_users = $mail_notification = $reports_generation = 0;

$sql = "SELECT name, mail, phone, blocking_users, mail_notification, reports_generation FROM $db_name.firm WHERE id=?";

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
mysqli_close($link);

// if($_SERVER["REQUEST_METHOD"] == "POST") {

//     $new_email = trim($_POST["email"]);
//     if (!filter_var($new_email, FILTER_VALIDATE_EMAIL)) {
//         $email_err = "Niepoprawny format.";
//     }

//     $new_phone = trim($_POST["phone"]);
//     if(!preg_match("/^[0-9]+$/", $new_phone)) {
//       $phone_err = "Niepoprawny format.";
//     }

    
//     if(empty($login_err) && empty($email_err) && empty($phone_err)) {

//         $sql = "INSERT INTO mojabaza.subject(login, password, type) VALUES(?, ?, 1)";

//         if($stmt = mysqli_prepare($link, $sql)){
//             mysqli_stmt_bind_param($stmt, "ss", $param_login, $param_password);
            
//             $param_login = $login;
//             $param_password = generateRandomString();

//             if(mysqli_stmt_execute($stmt)) {
//                 $sql = "SELECT id FROM mojabaza.subject WHERE login = ?";

//                 if($stmt = mysqli_prepare($link, $sql)){
//                     mysqli_stmt_bind_param($stmt, "s", $param_login);
                    
//                     $param_login = $login;

//                     if(mysqli_stmt_execute($stmt)) {
//                         mysqli_stmt_store_result($stmt);       
//                         mysqli_stmt_bind_result($stmt, $id);
//                         mysqli_stmt_fetch($stmt);

//                         $name = trim($_POST["name"]);
//                         $coded_name = generateCodedName($link);
//                         $blocking_users = isset($_POST['feature-block-users']) ? 1 : 0;
//                         $mail_notification = isset($_POST['feature-mail-notification']) ? 1 : 0;
//                         $reports_generation = isset($_POST['feature-phone-notification']) ? 1 : 0;

//                         $sql = "INSERT INTO mojabaza.firm(id, name, coded_name, mail, phone, blocking_users, mail_notification, reports_generation) 
//                         VALUES(?, ?, ?, ?, ?, $blocking_users, $mail_notification, $reports_generation)";

//                         if($stmt = mysqli_prepare($link, $sql)){
//                             mysqli_stmt_bind_param($stmt, "issss", $id, $name, $coded_name, $email, $phone);
//                             if(mysqli_stmt_execute($stmt)) {
//                                 header("location: home.php?status=added");
//                                 echo "Poprawnie dodano firmę.";
//                             }
//                             else {
//                                 echo "Błąd! Spróbuj później.";
//                             }
//                         } else {
//                             echo "Błąd! Spróbuj później.";
//                         }
//                     }
//                 }
//             } else{
//                 echo "Błąd! Spróbuj później.";
//             }
//         }
//     }
//     mysqli_close($link);
// }

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
                        <input type="checkbox" name="feature-phone-notification" id="feature-phone-notification" value="3"
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