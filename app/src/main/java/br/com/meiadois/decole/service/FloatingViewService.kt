package br.com.meiadois.decole.service

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
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
import br.com.meiadois.decole.activity.user.HomeActivity
import br.com.meiadois.decole.model.Step
import br.com.meiadois.decole.util.Constants

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
    private var steps: List<Step> = Constants.steps(maxWidth, maxHeight)
    private var currentStepIndex = 0
    private var progressGainByStep = 0

    private fun makeTransition(oldIndex: Int, newIndex: Int) {
        val oldStep = steps[oldIndex]
        val newStep = steps[newIndex]

        val pvhX =
            PropertyValuesHolder.ofInt("x", oldStep.positionX, newStep.positionX)
        val pvhY =
            PropertyValuesHolder.ofInt("y", oldStep.positionY, newStep.positionY)

        val translator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)

        translator.addUpdateListener { valueAnimator ->
            val layoutParams =
                mFloatingView.layoutParams as WindowManager.LayoutParams
            layoutParams.x = (valueAnimator.getAnimatedValue("x") as Int)
            layoutParams.y = (valueAnimator.getAnimatedValue("y") as Int)
            mWindowManager.updateViewLayout(mFloatingView, layoutParams)
        }

        translator.duration = 500
        translator.start()
    }

    private fun changeStep(newIndex: Int) {
        val oldIndex = currentStepIndex
        if (newIndex != oldIndex) makeTransition(oldIndex, newIndex)

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
                nextButton.isEnabled = false
                nextButton.setImageResource(
                    R.drawable.ic_arrow_right_disabled
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

    override fun onCreate() {
        super.onCreate()
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
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            stopSelf()
        }
        nextButton.setOnClickListener { changeStep(currentStepIndex + 1) }

        prevButton.setOnClickListener { changeStep(currentStepIndex - 1) }

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById<View>(R.id.root_container)
            .setOnTouchListener(object : OnTouchListener {
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
                                if (isViewCollapsed()) { //When user clicks on the image view of the collapsed layout,
                                    //visibility of the collapsed layout will be changed to "View.GONE"
                                    //and expanded view will become visible.
                                    closeButtonCollapsed.visibility = View.GONE
                                    expandedView.visibility = View.VISIBLE
                                }else{
                                    collapsedView.visibility = View.VISIBLE
                                    closeButtonCollapsed.visibility = View.VISIBLE
                                    expandedView.visibility = View.GONE
                                }
                            }
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mFloatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private fun isViewCollapsed(): Boolean {
        return mFloatingView == null || mFloatingView.findViewById<View>(R.id.expanded_container).visibility == View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}