package com.example.firstapplication.model

class Model private constructor() {
    var user: User? = null

    companion object {
        val shared = Model()
    }
}