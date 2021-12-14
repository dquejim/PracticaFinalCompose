package com.example.practicafinalcompose

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.practicafinalcompose.modelo.Peliculas
import com.example.practicafinalcompose.retrofit.PeliculasInfo
import com.example.practicafinalcompose.retrofit.PeliculasInstance
import com.example.practicafinalcompose.ui.theme.PracticaFinalComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PracticaFinalComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(scope = scope, scaffoldState = scaffoldState) },
        drawerContent = {
            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
        }
    ) {
        Navigation(navController = navController)
    }
}

@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState) {

    TopAppBar(
        title = { Text(text = "Peliculas", fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, "")
            }
        },
        backgroundColor = Color.White,
        contentColor = Color.Black
    )

}

@Composable
fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {

    val items = listOf(
        NavegacionItem.Home,
        NavegacionItem.Add,
        NavegacionItem.Delete,
    )

    Column(
        modifier = Modifier
            .background(color = Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.LightGray),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()
                    .padding(10.dp)
            )

        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { items ->
            DrawerItem(item = items, selected = currentRoute == items.route, onItemClick = {

                navController.navigate(items.route) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }

                scope.launch {
                    scaffoldState.drawerState.close()
                }

            })
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "By David Quesada Jiménez",
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally)
        )

    }
}
@Composable
fun DrawerItem(item: NavegacionItem, selected: Boolean, onItemClick: (NavegacionItem) -> Unit) {
    val background = if (selected) Color.LightGray else Color.Transparent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) }
            .height(45.dp)
            .background(background)
            .padding(start = 10.dp)
    ) {

        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            colorFilter = ColorFilter.tint(Color.Black),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = Color.Black
        )

    }

}

@Composable
fun HomeScreen() {
    val listaPeliculas = cargarJSON()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally) {
        LazyColumn(
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = CenterHorizontally
        )
        {

            items(listaPeliculas) { pelicula ->

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = pelicula.nombre + " ( "+pelicula.genero+" )  -  "+pelicula.fecha,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .border(2.dp, Color.White)
                            .padding(15.dp, 14.dp, 9.dp, 9.dp)
                            .height(30.dp)
                            .size(30.dp),
                        style = TextStyle(fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold)
                    )
                }

                Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color.White)
                            .background(Color.DarkGray)
                    ){

                        CargarImagen(url = pelicula.imagen)

                        }
                    }
            }
        }
    }


@Composable
fun CargarImagen(url: String) {
    Image(
        painter = rememberImagePainter(url),
        contentDescription = "Imagen",
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(100.dp)),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
fun AddScreen() {

    var textNombre by rememberSaveable { mutableStateOf("") }
    var textFecha by rememberSaveable { mutableStateOf("") }
    var textGenero by rememberSaveable { mutableStateOf("") }
    var textImagen by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
    ) {


        TextField(
            value = textNombre,
            onValueChange = { nuevo ->
                textNombre = nuevo
            },
            label = {
                Text(text = "Nombre")
            },
            modifier = Modifier
                .padding(10.dp, 30.dp, 10.dp, 0.dp)
                .align(CenterHorizontally)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Left)
        )

        TextField(
            value = textFecha,
            onValueChange = { nuevo ->
                textFecha = nuevo
            },
            label = {
                Text(text = "Fecha")
            },
            modifier = Modifier
                .padding(10.dp, 30.dp, 10.dp, 0.dp)
                .align(CenterHorizontally)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Left)
        )

        TextField(
            value = textGenero,
            onValueChange = { nuevo ->
                textGenero = nuevo
            },
            label = {
                Text(text = "Género")
            },
            modifier = Modifier
                .padding(10.dp, 30.dp, 10.dp, 0.dp)
                .align(CenterHorizontally)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Left)
        )

        TextField(
            value = textImagen,
            onValueChange = { nuevo ->
                textImagen = nuevo
            },
            label = {
                Text(text = "URL de la Imagen")
            },
            modifier = Modifier
                .padding(10.dp, 30.dp, 10.dp, 0.dp)
                .align(CenterHorizontally)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Left)
        )

        Spacer(Modifier.height(40.dp) )

        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(Modifier.width(65.dp) )

            Button(
                modifier = Modifier
                    .size(width = 100.dp, height = 50.dp),
                onClick = {
                    insertar(textNombre, textFecha, textGenero, textImagen)
                    textNombre = ""
                    textFecha = ""
                    textGenero = ""
                    textImagen = ""
                    Toast.makeText(context, "Registro añadido", Toast.LENGTH_SHORT).show()

                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black
                )
            ){
                Text(text = "INSERT",
                    color = Color.White
                )
            }

        }
    }
}

@Composable
fun DeleteScreen() {

    var textNombre by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray),
    ) {


        TextField(
            value = textNombre,
            onValueChange = { nuevo ->
                textNombre = nuevo
            },
            label = {
                Text(text = "Nombre")
            },
            modifier = Modifier
                .padding(10.dp, 30.dp, 10.dp, 0.dp)
                .align(CenterHorizontally)
                .background(Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(textAlign = TextAlign.Left)
        )

        Spacer(Modifier.height(40.dp) )

        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(Modifier.width(65.dp) )

            Button(
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(100.dp))
                    .size(width = 100.dp, height = 50.dp),
                onClick = {
                    borrar(textNombre)
                    textNombre = ""
                    Toast.makeText(context, "Registro eliminado", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black
                )
            ){
                Text(text = "DELETE",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavegacionItem.Home.route) {
        composable(NavegacionItem.Home.route) {
            HomeScreen()
        }

        composable(NavegacionItem.Add.route) {
            AddScreen()
        }

        composable(NavegacionItem.Delete.route) {
            DeleteScreen()
        }
    }
}

fun insertar(nombre:String,fecha:String,genero:String,imagen:String){
    val url = "http://iesayala.ddns.net/DavidQuesada/insertDB.php/?nombre=$nombre&fecha=$fecha&genero=$genero&imagen=https://i.blogs.es/aa91a3/cine-/1366_2000.jpg"
    leerUrl(url)
}


fun borrar(nombre:String){
    val url = "http://iesayala.ddns.net/DavidQuesada/deleteDB.php/?nombre=$nombre"
    leerUrl(url)
}

fun leerUrl(urlString:String){
    GlobalScope.launch(Dispatchers.IO){
        val response = try {
            URL(urlString)
                .openStream()
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            "Error with ${e.message}."
            Log.d("io", e.message.toString())
        } catch (e: Exception) {
            "Error with ${e.message}."
            Log.d("io", e.message.toString())
        }
    }

    return
}

@Composable
fun cargarJSON(): PeliculasInfo {
    val contest = LocalContext.current

    var pelis by rememberSaveable { mutableStateOf(PeliculasInfo()) }
    val peli = PeliculasInstance.peliculasInterface.peliculasInfo()

    peli.enqueue(object : Callback<PeliculasInfo> {
        override fun onResponse(
            call: Call<PeliculasInfo>,
            response: Response<PeliculasInfo>
        ) {
            val peliculasInfo: PeliculasInfo? = response.body()
            if (peliculasInfo != null) {
                //    Toast.makeText(contest, userInfo.toString(), Toast.LENGTH_SHORT).show()

                pelis = peliculasInfo

            }

        }

        override fun onFailure(call: Call<PeliculasInfo>, t: Throwable) {

            Toast.makeText(contest, t.toString(), Toast.LENGTH_SHORT).show()
        }

    })

    return pelis

}
