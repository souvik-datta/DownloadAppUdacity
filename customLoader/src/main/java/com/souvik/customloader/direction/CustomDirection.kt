package com.souvik.customloader.direction

import com.souvik.customloader.CustomLoader

interface CustomDirection {
    fun drawDirection(customLoader: CustomLoader, animatedIntValue: Int)
    fun resetDirection(customLoader: CustomLoader)
    fun trigger(customLoader: CustomLoader): Int
}