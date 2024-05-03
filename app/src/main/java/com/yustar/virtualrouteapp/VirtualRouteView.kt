package com.yustar.virtualrouteapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat


class VirtualRouteView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var border: Paint = Paint()
    private var road: Paint = Paint()
    private var roadLine: Paint = Paint()
    private var path: Path = Path()
    private var selectedOption = ScrollDirection.vertical

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

    private var milestonePoints = arrayListOf(2500, 5000, 7500)
    private var participantProgress = arrayListOf(1000, 2000, 3000, 4000, 7000, 10000, 5500, 11000,
        12000, 10000)
    private var userCoordinates = arrayListOf<CGPoint>()
    private var totalStepsGoal = 10000

    private var originalX = 0f
    private var originalY = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    private val gestureDetectorCompat = GestureDetectorCompat(this.context, object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            Log.e("DevLog", "onDown")
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            Log.e("DevLog", "onScroll")
            Log.e("DevLog", "distanceX = $distanceX")
            Log.e("DevLog", "distanceY = $distanceY")
            this@VirtualRouteView.scrollTo(distanceX.toInt(), distanceY.toInt())
            return false
        }
    })

    /*@SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetectorCompat.onTouchEvent(event)
    }*/

    init {
        /**
         * Border
         */
        initBorder(Color.RED)

        /**
         * Road
         */
        initRoad(Color.BLUE)

        /**
         * White Line
         */
        initRoadLine(Color.GRAY)

        /**
         * Set Background
         */
        setBackground()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val points = if (selectedOption == ScrollDirection.horizontal)
            horizontalControlPoint
        else
            verticalControlPoint

        /**
         * Draw Virtual Route
         */
        drawVirtualRoute(canvas, points)

        /**
         * Add user Position
         */
        addUserPositions(canvas = canvas, participantProgress = participantProgress,
            controlPoints = points)

        /*Log.e("devLog", "canvas.width = ${canvas.width}")
        Log.e("devLog", "canvas.height = ${canvas.height}")*/
    }

    private fun initBorder(color: Int) {
        border.style = Paint.Style.STROKE
        border.color = color
        border.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44f,
            context.resources.displayMetrics)
        border.strokeCap = Paint.Cap.ROUND
        border.isAntiAlias = true
    }

    private fun initRoad(color: Int) {
        road.style = Paint.Style.STROKE
        road.color = Color.BLUE
        road.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f,
            context.resources.displayMetrics)
        road.strokeCap = Paint.Cap.ROUND
        road.isAntiAlias = true
    }

    private fun initRoadLine(color: Int) {
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
        roadLine.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f,
            context.resources.displayMetrics)
        roadLine.strokeCap = Paint.Cap.ROUND
        roadLine.isAntiAlias = true
    }

    private fun setBackground() {
        if (selectedOption == ScrollDirection.horizontal)
            this.background = context.getDrawable(R.drawable.ic_virtual_route_horizontal_bg)
        else
            this.background = context.getDrawable(R.drawable.ic_virtual_route_vertical_bg)
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

    private fun drawVirtualRoute(canvas: Canvas, points: ArrayList<CGPoint>) {
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

    private fun addUserPositions(canvas: Canvas, participantProgress: ArrayList<Int>, controlPoints: ArrayList<CGPoint>) {
        userCoordinates.clear()
        participantProgress.forEach {
            val t = stepsCoveredToT(stepsCovered = it, totalSteps = totalStepsGoal)
            val point = pointsOnStraightLines(controlPoints = controlPoints, t = t.toFloat())
            userCoordinates.add(point)
            addUserOnMap(canvas = canvas, userPosition = point, userImage = R.drawable.ic_virtual_route_pin.toString())
        }
    }

    private fun stepsCoveredToT(stepsCovered: Int, totalSteps: Int): Double {
        return (stepsCovered / totalSteps).toDouble()
    }

    // Function to calculate points on straight lines between control points
    private fun pointsOnStraightLines(controlPoints: ArrayList<CGPoint>, t: Float): CGPoint {
        //guard controlPoints.count >= 2 else { return nil } // Need at least 2 points to define a line

        // Calculate the number of segments between control points
        val numSegments = controlPoints.size - 1

        // Calculate the segment index
        val segmentIndex = (t * numSegments).toInt()

        // Calculate t within the segment
        val segmentT = (t * numSegments) - segmentIndex

        // Ensure the segment index is valid
        //guard segmentIndex >= 0 && segmentIndex < numSegments else { return nil }
        if (segmentIndex in 0..<numSegments) {
            // Calculate the point on the segment
            val startPoint = controlPoints[segmentIndex]
            val endPoint = controlPoints[segmentIndex + 1]

            return pointOnLine(startPoint = startPoint, endPoint = endPoint, t = t)

        } else if (numSegments == segmentIndex)
            return pointOnLine(startPoint = controlPoints[segmentIndex],
                endPoint = controlPoints[segmentIndex], t = segmentT)

        else if (segmentIndex > numSegments)
            return pointOnLine(startPoint = controlPoints[numSegments],
                endPoint = controlPoints[numSegments], t = 1f)

        return CGPoint()
    }

    // Function to calculate a point on a straight line between two points
    private fun pointOnLine(startPoint: CGPoint, endPoint: CGPoint, t: Float): CGPoint {
        val x = startPoint.x + (endPoint.x - startPoint.x) * t
        val y = startPoint.y + (endPoint.y - startPoint.y) * t

        return CGPoint(x.toInt(), y.toInt())
    }

    private fun addUserOnMap(canvas: Canvas, userPosition: CGPoint, userImage: String) {
        val x = userPosition.x
        val y = userPosition.y

        val drawableImg = ContextCompat.getDrawable(context, userImage.toInt())
        val bitmapImg = BitmapFactory.decodeResource(resources, R.drawable.ic_virtual_route_pin)

        drawableImg?.let {
            it.setBounds((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x.toFloat(),
                context.resources.displayMetrics) / 2).toInt(),
                (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y.toFloat(),
                    context.resources.displayMetrics) / 2).toInt(),
                (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (it.intrinsicWidth + x).toFloat(),
                    context.resources.displayMetrics) /2 ).toInt(),
                (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (it.intrinsicHeight + y).toFloat(),
                    context.resources.displayMetrics) / 2).toInt())
            /*it.setBounds(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f,
                context.resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f,
                context.resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, it.intrinsicWidth.toFloat(),
                    context.resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, it.intrinsicHeight.toFloat(),
                    context.resources.displayMetrics).toInt()
            )*/
            it.draw(canvas)
        }

        /*val imageSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f,
            context.resources.displayMetrics)
        val imageView = ImageView(context)
        imageView.setImageDrawable(drawableImg)
        imageView.layoutParams = LinearLayout.LayoutParams(imageSize.toInt(), imageSize.toInt())
        imageView.x = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x - imageSize / 2,
            context.resources.displayMetrics)
        imageView.y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y - imageSize / 2,
            context.resources.displayMetrics)
        imageView.clipToOutline = true*/

        val imageSize = 40f
        val imageView = ImageView(context)
        imageView.setImageDrawable(drawableImg)
        imageView.layoutParams = LinearLayout.LayoutParams(imageSize.toInt(), imageSize.toInt())
        imageView.x = x - imageSize / 2
        imageView.y = y - imageSize / 2
        imageView.clipToOutline = true

        /*val paint = Paint()
        canvas.drawBitmap(bitmapImg, 0f, 0f, paint)*/
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("devLog", "w = $w")
        Log.e("devLog", "h = $h")
        Log.e("devLog", "oldw = $oldw")
        Log.e("devLog", "oldh = $oldh")
    }

    /*override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("devLog", "widthMeasureSpec = $widthMeasureSpec")
        Log.e("devLog", "heightMeasureSpec = $heightMeasureSpec")

        when (selectedOption) {
            ScrollDirection.horizontal -> {
                setMeasuredDimension(widthMeasureSpec +
                        horizontalControlPoint[horizontalControlPoint.size - 1].x
                        *//*TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            (horizontalControlPoint[horizontalControlPoint.size - 1].x).toFloat(),
                            context.resources.displayMetrics
                        ).toInt()*//*,
                    heightMeasureSpec)
            }
            ScrollDirection.vertical -> {
                setMeasuredDimension(widthMeasureSpec, heightMeasureSpec +
                        verticalControlPoint[verticalControlPoint.size - 1].y
                        *//*TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            (verticalControlPoint[verticalControlPoint.size - 1].y).toFloat(),
                            context.resources.displayMetrics).toInt()*//*)
            }
        }
    }*/
}