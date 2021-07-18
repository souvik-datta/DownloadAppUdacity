package com.souvik.customloader.direction

import com.souvik.customloader.CustomLoader

class LeftToRightDirection : CustomDirection {
    override fun drawDirection(cutomButton: CustomLoader, animatedIntValue: Int) {
        cutomButton.fillingRect.right = animatedIntValue
    }

    override fun resetDirection(cutomButton: CustomLoader) {
        cutomButton.fillingRect.right = 0
    }

    override fun trigger(cutomButton: CustomLoader)= cutomButton.viewWidth

}
