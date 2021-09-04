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
    private var drawableLeftPadding: Float = 0f
    private var drawableTopPadding: Float = 0f
    private var drawableRightPadding: Float = 0f
    private var drawableBottomPadding: Float = 0f

    private var outsideRect: RectF = RectF()
    private var insideRect: RectF = RectF()
    private var bounds: Rect = Rect()
    private var rectDrawable: Rect = Rect()

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
        loadStyle()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        loadStyle()
    }

    fun loadStyle() {
        val attrs: IntArray = intArrayOf(
            resources.getIdentifier("DRAWABLE_BOTTOM_PADDING_TEST", "attr", context.packageName),
            resources.getIdentifier("DRAWABLE_LEFT_PADDING_TEST", "attr", context.packageName),
            resources.getIdentifier("DRAWABLE_RIGHT_PADDING_TEST", "attr", context.packageName),
            resources.getIdentifier("DRAWABLE_TOP_PADDING_TEST", "attr", context.packageName),
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
        drawableBottomPadding = ta.getDimension(index++, 0f)
        drawableLeftPadding = ta.getDimension(index++, 0f)
        drawableRightPadding = ta.getDimension(index++, 0f)
        drawableTopPadding = ta.getDimension(index++, 0f)
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

    fun seCustomTextColor(textColor: Int) {
        paintText.color = textColor
        prepareDraw()
    }

    fun setCustomTextSize(textSize: Float) {
        paintText.textSize = dp2Pixel(textSize)
        prepareDraw()
    }

    fun setCustomRadius(radius: Float) {
        this.pxRadius = dp2Pixel(radius)
        prepareDraw()
    }

    fun setInsidePressedColor(insidePressedColor: Int) {
        paintPressedRectInside.color = insidePressedColor
        prepareDraw()
    }

    fun setInsideDefaultColor(insideDefaultColor: Int) {
        paintDefaultRectInside.color = insideDefaultColor
        prepareDraw()
    }

    fun setCustomStrokeColor(cbStrokeColor: Int) {
        paintRectOutside.color = cbStrokeColor
        prepareDraw()
    }

    fun setCustomStrokeWidth(strokeWidth: Float) {
        this.pxStrokeWidth = dp2Pixel(strokeWidth)
        prepareDraw()
    }

    fun setDrawableTopPadding(padding: Float) {
        this.drawableTopPadding = dp2Pixel(padding)
        prepareDraw()
    }

    fun setDrawableRightPadding(padding: Float) {
        this.drawableRightPadding = dp2Pixel(padding)
        prepareDraw()
    }

    fun setDrawableBottomPadding(padding: Float) {
        this.drawableBottomPadding = dp2Pixel(padding)
        prepareDraw()
    }

    fun setDrawableLeftPadding(padding: Float) {
        this.drawableLeftPadding = dp2Pixel(padding)
        prepareDraw()
    }

    fun setCustomFontFamily(cbFontFamily: Typeface) {
        paintText.typeface = cbFontFamily
        prepareDraw()
    }

    fun setCustomFontStyle(cbFontStyle: Int) {
        paintText.isFakeBoldText = if (cbFontStyle == context.resources.getInteger(R.integer.bold)) true else false
        prepareDraw()
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

        rectDrawable = Rect(
            (pxStrokeWidth + drawableLeftPadding).toInt(),
            (pxStrokeWidth + drawableTopPadding).toInt(),
            (width - pxStrokeWidth - drawableRightPadding).toInt(),
            (height - pxStrokeWidth - drawableBottomPadding).toInt()
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

    private fun dp2Pixel(dp: Float): Float {
        val density = context.resources.displayMetrics.density
        return (dp * density)
    }
}
