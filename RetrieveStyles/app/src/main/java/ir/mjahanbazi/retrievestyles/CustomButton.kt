package ir.mjahanbazi.retrievestyles

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged

class CustomButton : AppCompatButton, View.OnTouchListener {
    private var clickListener: OnClickListener? = null
    private var customFontStyle: Int = 0
    private var customFontFamily: Int = 0
    private var xText: Float = 0f
    private var yText: Float = 0f
    private var pxStrokeWidth: Float = 0f
    private var pxRadius: Float = 0f
    private var pxInsideRadius: Float = 0f
    private var isClicked: Boolean = false
    private var buttonLabel: String = ""

    private var outsideRect: RectF = RectF()
    private var insideRect: RectF = RectF()
    private var bounds: Rect = Rect()

    private var paintDefaultRectInside = Paint()
    private var paintPressedRectInside = Paint()
    private var paintRectOutside = Paint()
    private var paintText = Paint()

    init {
        setWillNotDraw(false)
        setOnTouchListener(this)
        doAfterTextChanged {
            prepareDraw()
        }

        setTransformationMethod(null)
    }

    constructor(context: Context) : super(context) {
        retrieveStyle()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        retrieveStyle()
    }

    fun retrieveStyle() {
        val attrs: IntArray = intArrayOf(
            resources.getIdentifier("FILL_NORMAL_COLOR_TEST", "attr", context.packageName),
            resources.getIdentifier("FILL_PRESSED_COLOR_TEST", "attr", context.packageName),
            resources.getIdentifier("FONT_FAMILY_TEST", "attr", context.packageName),
            resources.getIdentifier("FONT_STYLE_TEST", "attr", context.packageName),
            resources.getIdentifier("RADIUS_TEST", "attr", context.packageName),
            resources.getIdentifier("STROKE_COLOR_TEST", "attr", context.packageName),
            resources.getIdentifier("STROKE_WIDTH_TEST", "attr", context.packageName),
            resources.getIdentifier("TEXT_COLOR_TEST", "attr", context.packageName),
            resources.getIdentifier("TEXT_SIZE_TEST", "attr", context.packageName))

        var ta: TypedArray = context.obtainStyledAttributes(R.style.button_style_1, attrs)

        @StyleableRes
        var index = 0

        //retrieving style items
        paintDefaultRectInside.color = ta.getColor(index++, Color.RED)
        paintPressedRectInside.color = ta.getColor(index++, Color.YELLOW)
        customFontFamily = ta.getResourceId(index++, 0)
        customFontStyle = ta.getInt(index++, 0)
        pxRadius = ta.getDimension(index++, 0f)
        paintRectOutside.color = ta.getColor(index++, Color.RED)
        pxStrokeWidth = ta.getDimension(index++, 0f)
        paintText.color = ta.getColor(index++, Color.RED)
        paintText.textSize = ta.getDimension(index, 0f)

        ta.recycle()

        paintText.isAntiAlias = true
        paintText.textAlign = Paint.Align.LEFT
        paintText.isFakeBoldText = true
        paintText.isFakeBoldText = if (customFontStyle == 1) true else false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paintText.typeface = resources.getFont(customFontFamily)
        } else {
            paintText.typeface = ResourcesCompat.getFont(context, customFontFamily)
        }

        paintPressedRectInside.isAntiAlias = true
        paintPressedRectInside.style = Paint.Style.FILL

        paintDefaultRectInside.isAntiAlias = true
        paintDefaultRectInside.style = Paint.Style.FILL

        paintRectOutside.isAntiAlias = true
        paintRectOutside.style = Paint.Style.FILL
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        prepareDraw()
    }

    private fun prepareDraw() {

        pxInsideRadius = pxRadius - pxStrokeWidth

        outsideRect = RectF(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat()
        )

        insideRect = RectF(
            pxStrokeWidth,
            pxStrokeWidth,
            (width - pxStrokeWidth),
            (height - pxStrokeWidth)
        )

        //calculating position of text
        buttonLabel = text.toString()
        paintText.getTextBounds(buttonLabel, 0, buttonLabel.length, bounds)
        xText = width / 2f - bounds.width() / 2f - bounds.left
        yText = height / 2f + bounds.height() / 2f - bounds.bottom

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //drawing stroke of button
        canvas.drawRoundRect(
            outsideRect,
            pxRadius,
            pxRadius,
            paintRectOutside
        )
        //drawing inside rect of button
        if (isClicked) {
            canvas.drawRoundRect(
                insideRect,
                pxInsideRadius,
                pxInsideRadius,
                paintPressedRectInside
            )
        } else {
            canvas.drawRoundRect(
                insideRect,
                pxInsideRadius,
                pxInsideRadius,
                paintDefaultRectInside
            )
        }
        //drawing text button
        canvas.drawText(buttonLabel, xText, yText, paintText)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        this.clickListener = listener
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            isClicked = true
        } else if (event.action == MotionEvent.ACTION_UP) {
            isClicked = false
            clickListener!!.onClick(view)
        } else if (event.action == MotionEvent.ACTION_CANCEL) {
            isClicked = false
        }
        invalidate()
        return true
    }

}
