package com.example.projectmanagerapp.utils

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val BOARD_ROUTE = "board"
    const val CREATE_BOARD_ROUTE = "create_board"
    const val EDIT_BOARD_ROUTE = "edit_board/{boardId}"
    const val BOARD_DETAIL_ROUTE = "board_detail{boardId}"
    const val CARD_DETAIL_ROUTE = "card_detail/{boardId}/{listId}/{cardId}"
    const val MAP_PICKER_ROUTE = "map_picker"
    const val ADD_MEMBER_ROUTE = "add_member/{boardId}"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val FORGOT_PASSWORD_ROUTE = "forgot_password"
    const val PROFILE_ROUTE = "profile"
    const val DEMO = "demo"
}


object Constants {
    const val USER_COLLECTION = "users"
    const val BOARD_COLLECTION = "boards"
    const val CARD_COLLECTION = "cards"
    const val LIST_COLLECTION = "pm_list"
    const val CHECKLIST_COLLECTION = "checklists"
    const val COMMENT_COLLECTION = "comments"

    const val BOARD_BACKGROUND_REF = "board_backgrounds"


    val predefinedBackgroundColors = listOf(
        "#FF0079BF", // Blue
        "#FFD29034", // Orange
        "#FF519839", // Green
        "#FFB04632", // Red
        "#FF89609E", // Purple
        "#FFCD5A91", // Pink
        "#FF4BBF6B", // Light Green
        "#FF00AECC", // Teal
        "#FF838C91"  // Gray
    )
}

enum class BackgroundType {
    COLOR, IMAGE
}