package com.fastjetservice.photoresizer.presentation.ui.navigation

sealed class Screens(val route: String) {
    object HomeScreen : Screens("home")
    object EditScreen : Screens("edit")
    object ImageScreen : Screens("image")


    fun routeWithArgs(vararg args: String): String =
        buildString {
            append(route)
            args.forEachIndexed { index, _ ->
                append("?$index={$index}")
            }
        }

    fun paramsWithArgs(vararg args: String): String =
        buildString {
            append(route)
            args.forEachIndexed { index, value ->
                append("?$index=$value")
            }
        }
}

