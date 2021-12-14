<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDDavidQuesadaJimenez";

//Creamos la conexión
$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Ha sucedido un error inexperado en la conexion de la base de datos");

//generamos la consulta
$sql = "SELECT * FROM Peliculas";
mysqli_set_charset($conexion, "utf8"); //formato de datos utf8

if(!$result = mysqli_query($conexion, $sql)) die();

$peliculas = array(); //creamos un array

while($row = mysqli_fetch_array($result))
{
    $nombre=$row['Nombre'];
    $fecha=$row['Fecha'];
    $genero=$row['Genero'];
    $imagen=$row['Imagen'];

    $peliculas[] = array('nombre'=> $nombre, 'fecha'=> $fecha, 'genero'=> $genero, 'imagen'=> $imagen);

}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Ha sucedido un error inexperado en la desconexion de la base de datos");


//Creamos el JSON
$json_string = json_encode($peliculas);

echo $json_string;