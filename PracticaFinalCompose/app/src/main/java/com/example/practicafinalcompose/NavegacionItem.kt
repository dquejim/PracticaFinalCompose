package com.example.practicafinalcompose

sealed class NavegacionItem(var route: String, var icon: Int, var title: String)
{
    object Home : NavegacionItem("home", R.drawable.home, "Home")
    object Add : NavegacionItem("añadir", R.drawable.add, "Añadir")
    object Delete : NavegacionItem("eliminar", R.drawable.remove, "Eliminar")
}