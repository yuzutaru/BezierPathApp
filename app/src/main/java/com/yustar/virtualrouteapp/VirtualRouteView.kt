package com.yustar.virtualrouteapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout


class VirtualRouteView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var border: Paint = Paint()
    private var road: Paint = Paint()
    private var roadLine: Paint = Paint()
    private var path: Path = Path()
    private var selectedOption = ScrollDirection.horizontal

    private var horizontalControlPoint = arrayListOf(
        CGPoint(x= 20, y= 150),
        CGPoint(x= 20, y= 150),
        CGPoint(x= 150, y= 150),
        CGPoint(x= 150, y= 250),
        CGPoint(x= 350, y= 250),
        CGPoint(x= 350, y= 50),
        CGPoint(x= 500, y= 50),
        CGPoint(x= 500, y= 150),
        CGPoint(x= 600, y= 150)
    )

    private var verticalControlPoint = arrayListOf(
        CGPoint(x= 300, y= 50), //0
        CGPoint(x= 300, y= 50),
        CGPoint(x= 300, y= 150),
        CGPoint(x= 250, y= 150),
        CGPoint(x= 250, y= 300), //4
        CGPoint(x= 150, y= 300),
        CGPoint(x= 150, y= 200),
        CGPoint(x= 50, y= 200),
        CGPoint(x= 50, y= 400),  //8
        CGPoint(x= 300, y= 400),
        CGPoint(x= 300, y= 600),
        CGPoint(x= 100, y= 600),
        CGPoint(x= 100, y= 670),  //12
        CGPoint(x= 330, y= 670),
        CGPoint(x= 330, y= 750),
        CGPoint(x= 250, y= 750),
        CGPoint(x= 250, y= 850),  //16
        CGPoint(x= 100, y= 850),
        CGPoint(x= 100, y= 1000),
        CGPoint(x= 250, y= 1000),
        CGPoint(x= 250, y= 950),  //20
        CGPoint(x= 320, y= 950),
        CGPoint(x= 320, y= 1100),
        CGPoint(x= 100, y= 1100),
        CGPoint(x= 100, y= 1200),  //24
        CGPoint(x= 100, y= 1200)
    )

    private var participantProgress = arrayListOf(1000, 2000, 3000, 4000, 7000, 10000, 5500, 11000,
        12000, 10000)
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY =0f

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /**
         * Border
         */
        initBorder(44f, Color.RED)

        /**
         * Road
         */
        initRoad(40f, Color.BLUE)

        /**
         * White Line
         */
        initRoadLine(4f, Color.GRAY)

        val points = if (selectedOption == ScrollDirection.horizontal)
            horizontalControlPoint
        else
            verticalControlPoint

        /**
         * Draw Path
         */
        var startPoint = CGPoint(0, 0)
        var endPoint = CGPoint(0,0)

        points.forEachIndexed { i, controlPoints ->
            if (i == 0 && i + 1 < points.size) {
                if (selectedOption == ScrollDirection.horizontal) {
                    startPoint = CGPoint(controlPoints.x - 5, controlPoints.y)
                    endPoint = controlPoints
                } else {
                    startPoint = CGPoint(controlPoints.x, controlPoints.y - 5)
                    endPoint = points[i + 1]
                }
            } else if (i == points.size - 2 && i + 1 < points.size) {
                if (selectedOption == ScrollDirection.horizontal) {
                    startPoint = controlPoints
                    endPoint = CGPoint(points[i + 1].x + 5, points[i + 1].y)
                } else {
                    startPoint = controlPoints
                    endPoint = CGPoint(points[i + 1].x, points[i + 1].y + 5)
                }
            } else if (i + 1 < points.size) {
                startPoint = controlPoints
                endPoint = points[i + 1]
            }

            drawLine(canvas, startPoint, endPoint)
        }
    }

    private fun initBorder(size: Float, color: Int) {
        border.style = Paint.Style.STROKE
        border.color = color
        border.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
            context.resources.displayMetrics)
        border.strokeCap = Paint.Cap.ROUND
        border.isAntiAlias = true
    }

    private fun initRoad(size: Float, color: Int) {
        road.style = Paint.Style.STROKE
        road.color = Color.BLUE
        road.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f,
            context.resources.displayMetrics)
        road.strokeCap = Paint.Cap.ROUND
        road.isAntiAlias = true
    }

    private fun initRoadLine(size: Float, color: Int) {
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
        roadLine.color = color
        roadLine.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
            context.resources.displayMetrics)
        roadLine.strokeCap = Paint.Cap.ROUND
        roadLine.isAntiAlias = true
    }

    private fun drawPath(canvas: Canvas, xStart: Float, yStart: Float, xEnd: Float, yEnd: Float,
                         xStartAnchor: Float, yStartAnchor: Float,
                         xEndAnchor: Float, yEndAnchor: Float) {
        path.moveTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, xStart,
            context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yStart,
                context.resources.displayMetrics))
        path.cubicTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, xStartAnchor,
            context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yStartAnchor,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, xEndAnchor,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yEndAnchor,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, xEnd,
                context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yEnd,
                context.resources.displayMetrics))
        canvas.drawPath(path, border)
        canvas.drawPath(path, road)
        canvas.drawPath(path, roadLine)
    }

    private fun drawLine(canvas: Canvas, startPoint: CGPoint, endPoint: CGPoint) {
        path.moveTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, startPoint.x.toFloat(),
            context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, startPoint.y.toFloat(),
                context.resources.displayMetrics))

        path.lineTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, endPoint.x.toFloat(),
            context.resources.displayMetrics),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, endPoint.y.toFloat(),
            context.resources.displayMetrics))

        canvas.drawPath(path, border)
        canvas.drawPath(path, road)
        canvas.drawPath(path, roadLine)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (selectedOption) {
            ScrollDirection.horizontal -> {
                setMeasuredDimension(widthMeasureSpec +
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            (horizontalControlPoint[horizontalControlPoint.size - 1].x).toFloat(),
                            context.resources.displayMetrics
                        ).toInt(),
                    heightMeasureSpec)
            }
            ScrollDirection.vertical -> {
                setMeasuredDimension(widthMeasureSpec, heightMeasureSpec +
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            (verticalControlPoint[verticalControlPoint.size - 1].y).toFloat(),
                            context.resources.displayMetrics).toInt())
            }
        }
    }
}