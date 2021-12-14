<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";

//Creamos la conexión
$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

//Generamos la consulta
$nombre = $_GET["nombre"];

  $sql = "DELETE FROM `Peliculas` WHERE `Nombre` = '$nombre'";
//echo
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8
if (mysqli_query($conexion, $sql)) {
      echo "New record created successfully";
} else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conexion);
}

//Desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");

?>
