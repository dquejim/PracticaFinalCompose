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
$fecha = $_GET["fecha"];
$genero = $_GET["genero"];
$imagen = $_GET["imagen"];

  $sql = "INSERT INTO Peliculas (Nombre, Fecha, Genero, Imagen) VALUES ('$nombre','$fecha','$genero','$imagen')";
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
