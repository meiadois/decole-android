package br.com.meiadois.decole.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Step
import br.com.meiadois.decole.presentation.user.HomeActivity
import br.com.meiadois.decole.presentation.user.education.RouteDetailsActivity

class FloatingViewService : Service() {

    private lateinit var mWindowManager: WindowManager
    private lateinit var mFloatingView: View
    private lateinit var params: WindowManager.LayoutParams

    private lateinit var progressBar: ProgressBar
    private lateinit var stepDescription: TextView
    private lateinit var prevButton: ImageView
    private lateinit var nextButton: ImageView
    private lateinit var closeButtonCollapsed: ImageView
    private lateinit var collapsedView: View
    private lateinit var expandedView: View

    private val maxWidth =
        Resources.getSystem().displayMetrics.widthPixels
    private val maxHeight =
        Resources.getSystem().displayMetrics.heightPixels
    lateinit var steps: List<Step>
    private var lessonClicked = 0L
    private var routeParent = 0L


    private var currentStepIndex = 0
    private var progressGainByStep = 0

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        lessonClicked = intent.getLongExtra("lessonId", 0L)
        routeParent = intent.getLongExtra("routeId", 0L)
        this.steps = intent.getParcelableArrayListExtra("steps")
        progressGainByStep = 100 / steps.size
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        stepDescription =
            mFloatingView.findViewById<View>(R.id.StepDescriptionTxt) as TextView

        //Set the close button collapsed
        closeButtonCollapsed =
            mFloatingView.findViewById<View>(R.id.close_btn) as ImageView


        //Set the pause button.
        prevButton =
            mFloatingView.findViewById<View>(R.id.prev_btn) as ImageView

        //Set the next button.
        nextButton =
            mFloatingView.findViewById<View>(R.id.next_btn) as ImageView

        progressBar =
            mFloatingView.findViewById<View>(R.id.ProgressBar) as ProgressBar

        // First change to index 0
        changeStep(0)

        //Add the view to the window.
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_TOAST,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //Specify the view position
        params.gravity =
            Gravity.TOP or Gravity.START //Initially view will be added to top-left corner
        params.x = steps[0].positionX
        params.y = steps[0].positionY

        //Add the view to the window
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)


        //The root element of the collapsed view layout
        collapsedView = mFloatingView.findViewById(R.id.collapse_view)
        //The root element of the expanded view layout
        expandedView = mFloatingView.findViewById(R.id.expanded_container)


        closeButtonCollapsed.setOnClickListener {
            //close the service and remove the from from the window
            exitInteractiveMode()
        }
        nextButton.setOnClickListener {
            when (val nextStep = currentStepIndex + 1) {
                steps.size -> exitInteractiveMode()
                else -> changeStep(nextStep)
            }
        }

        prevButton.setOnClickListener { changeStep(currentStepIndex - 1) }

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById<View>(R.id.root_container)
            .setOnTouchListener(OnFloatTouchListener())

        collapsedView.setOnTouchListener(OnFloatTouchListener {
            if (isViewCollapsed()) {
                closeButtonCollapsed.visibility = View.GONE
                expandedView.visibility = View.VISIBLE
            } else {
                collapsedView.visibility = View.VISIBLE
                closeButtonCollapsed.visibility = View.VISIBLE
                expandedView.visibility = View.GONE
            }
        })

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mFloatingView)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

// TODO VERIFICAR SE SERÁ NECESSÁRIO

//    private fun makeTransition(oldIndex: Int, newIndex: Int) {
//        val oldStep = steps[oldIndex]
//        val newStep = steps[newIndex]
//
//        val pvhX =
//            PropertyValuesHolder.ofInt("x", oldStep.positionX, newStep.positionX)
//        val pvhY =
//            PropertyValuesHolder.ofInt("y", oldStep.positionY, newStep.positionY)
//
//        val translator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
//
//        translator.addUpdateListener { valueAnimator ->
//            val layoutParams =
//                mFloatingView.layoutParams as WindowManager.LayoutParams
//            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)
//            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)
//            mWindowManager.updateViewLayout(mFloatingView, layoutParams)
//        }
//
//        translator.duration = 500
//        translator.start()
//    }

    private fun changeStep(newIndex: Int) {
//        val oldIndex = currentStepIndex
//        if (newIndex != oldIndex) makeTransition(oldIndex, newIndex)

        currentStepIndex = newIndex
        stepDescription.text = steps[currentStepIndex].text

        progressBar.progress = progressGainByStep * (1 + currentStepIndex)
        when (currentStepIndex) {
            0 -> {
                prevButton.isEnabled = false
                prevButton.setImageResource(
                    R.drawable.ic_arrow_left_disabled
                )
            }
            steps.size - 1 -> {
                nextButton.setImageResource(
                    R.drawable.ic_arrow_check
                )
            }
            else -> {
                prevButton.isEnabled = true
                prevButton.setImageResource(
                    R.drawable.ic_arrow_left
                )
                nextButton.isEnabled = true
                nextButton.setImageResource(
                    R.drawable.ic_arrow_right
                )
            }

        }
    }

    private fun exitInteractiveMode() {
        Intent(this, RouteDetailsActivity::class.java).also {
            it.addFlags(FLAG_ACTIVITY_NEW_TASK)
            it.putExtra("itemId", routeParent)
            it.putExtra("lessonDone", lessonClicked)
            startActivity(it)
            stopSelf()
        }
    }

    private fun isViewCollapsed(): Boolean {
        return mFloatingView.findViewById<View>(R.id.expanded_container).visibility == View.GONE
    }

    inner class OnFloatTouchListener(val clickHandler: (() -> Unit) = {}) : OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //remember the initial position.
                    initialX = params.x
                    initialY = params.y
                    //get the touch location
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val xDiff = (event.rawX - initialTouchX).toInt()
                    val yDiff = (event.rawY - initialTouchY).toInt()
                    //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    if (xDiff == 0 && yDiff == 0) {
                        v.performClick()
                        clickHandler()
                    }
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    //Calculate the X and Y coordinates of the view.
                    val newX = initialX + (event.rawX - initialTouchX).toInt()
                    val newY = initialY + (event.rawY - initialTouchY).toInt()
                    params.x = when {
                        newX > maxWidth -> maxWidth
                        newX < 0 -> 0
                        else -> newX
                    }
                    params.y = when {
                        newY > maxHeight -> maxHeight
                        newY < 0 -> 0
                        else -> newY
                    }

                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(mFloatingView, params)
                    return true
                }
            }
            return false
        }
    }
}