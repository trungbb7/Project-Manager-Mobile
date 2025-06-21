package com.example.projectmanagerapp.ui.main.response_models

data class CheckListResponseModel (
    var checkListTitle: String = "",
    var checkListItems: List<String> = emptyList()
    )