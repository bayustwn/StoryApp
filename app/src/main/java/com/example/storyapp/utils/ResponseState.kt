package com.example.storyapp.utils

sealed class ResponseState <out T> {
    class Success<out R>(val data: R): ResponseState<R>()
    class Error(val message: String): ResponseState<Nothing>()
    data object loading : ResponseState<Nothing>()
}