package com.pauloavelar.inventory.utils

fun returnTrue(block: () -> Unit): Boolean {
    block()
    return true
}
