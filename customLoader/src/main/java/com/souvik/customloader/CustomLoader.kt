package com.souvik.customloader

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.souvik.customloader.direction.CustomDirection
import com.souvik.customloader.direction.LeftToRightDirection

class CustomLoader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): AppCompatButton(context, attrs, defStyle){


    internal var viewWidth: Int = 0
    internal var viewHeight: Int = 0
    internal val fillingRect = Rect()

    private val fillingColor: Int
    private val fillingAlpha: Int
    private val fillingDuration: Long
    private val loadingColor : Int
    private var loadingProgress : Int

    private val fillingPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var drawProgress : MutableLiveData<Boolean> = MutableLiveData()
    val _drawProgress : LiveData<Boolean>
        get()=drawProgress

    open var onButtonFilled: (() -> Unit)? = null
    var cutomDirection: CustomDirection = LeftToRightDirection()
    val customWidthValueAnimator = ValueAnimator()

    init {
        drawProgress.value = false
        context.obtainStyledAttributes(attrs, R.styleable.CustomButton, defStyle, 0).apply {
            fillingColor = getColor(R.styleable.CustomButton_fillColor, resources.getColor(R.color.purple_500))
            fillingAlpha = getInt(R.styleable.CustomButton_fillAlpha, 128)
            fillingDuration = getInt(R.styleable.CustomButton_fillDuration, 1500).toLong()
            loadingColor = getColor(R.styleable.CustomButton_loadingColor, Color.YELLOW)
            loadingProgress = getInt(R.styleable.CustomButton_loadingProgress, 0)
        }.recycle()
        fillingPaint.color = fillingColor
        fillingPaint.alpha = fillingAlpha
        customWidthValueAnimator.duration = fillingDuration

        loadingPaint.color = loadingColor
        post {
            viewWidth = width
            viewHeight = height
            fillingRect.bottom = viewHeight
            fillingRect.right = viewWidth
            customWidthValueAnimator.setIntValues(0, viewWidth)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (drawProgress.value == true && onButtonFilled != null){
            canvas?.drawRect(fillingRect, fillingPaint)
            //canvas?.drawCircle((viewWidth*0.7).toFloat(),(viewHeight*0.55).toFloat(),(viewHeight*0.2).toFloat(),loadingPaint)
            //  val rectF = RectF(50f, 20f, 100f, 80f)
            //canvas?.drawArc (rectF, 90f, 45f, false, loadingPaint)

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                drawProgress.value = true
                startFillingAnimation()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                drawProgress.value = false
                stopFillingAnimation()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startFillingAnimation(){
        stopFillingAnimation()
        customWidthValueAnimator.apply {
            addUpdateListener {
                val intValue = it.animatedValue as Int
                cutomDirection.drawDirection(this@CustomLoader, intValue)
                invalidate()
                if (intValue == cutomDirection.trigger(this@CustomLoader)){
                    onButtonFilled?.invoke()
                }
            }
        }.start()
    }

    private fun stopFillingAnimation(){
        customWidthValueAnimator.cancel()
        customWidthValueAnimator.removeAllUpdateListeners()
        cutomDirection.resetDirection(this)
        invalidate()
    }


}