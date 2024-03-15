package com.yustar.bezierpathapp.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


/**
 * Created by Yustar Pramudana on 14/03/24.
 */

class BezierView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var border: Paint = Paint()
    private var road: Paint = Paint()
    private var roadLine: Paint = Paint()
    private var path: Path = Path()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /**
         * Border
         */
        border.style = Paint.Style.STROKE
        border.color = Color.RED
        border.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f,
            context.resources.displayMetrics)
        border.isAntiAlias = true

        /**
         * Road
         */
        road.style = Paint.Style.STROKE
        road.color = Color.BLUE
        road.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f,
            context.resources.displayMetrics)
        road.isAntiAlias = true

        /**
         * White Line
         */
        roadLine.style = Paint.Style.STROKE
        roadLine.setPathEffect(
            DashPathEffect(
                floatArrayOf(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
                    context.resources.displayMetrics),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f,
                        context.resources.displayMetrics)
                ),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f,
                context.resources.displayMetrics)
            )
        )
        roadLine.color = Color.GRAY
        roadLine.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f,
            context.resources.displayMetrics)
        roadLine.isAntiAlias = true

        /**
         * Draw Path
         */
        path.moveTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f,
            context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f,
                context.resources.displayMetrics))
        path.cubicTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260f,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f,
            context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260f,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260f,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260f,
                context.resources.displayMetrics))
        canvas.drawPath(path, border)
        canvas.drawPath(path, road)
        canvas.drawPath(path, roadLine)

        //path.moveTo(147f, 126f)
        //path.cubicTo(388f, 135f, 133f, 263f, 364f, 267f)
        //canvas.drawPath(path, road)
    }
}