package com.example.shared.utils

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.example.data.R
import com.example.data.databinding.LayoutBannerBinding

class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutBannerBinding.inflate(LayoutInflater.from(context), this, true)

    private var slideOutAnimation: Animation? = null
    private var duration: Long = 2000
    var layoutGravity = Gravity.TOP
    private var dismissOffsetThreshold = 0f
    private var viewWidth = 0f
    private var swipedOut = false
    private var animationInTop = 0
    private var animationInBottom = 0
    private var animationOutTop = 0
    private var animationOutBottom = 0
    private var isAutoDismissEnabled = false
    private var dismissListener: BannerDismissListener? = null
    private var timeOutDismiss = false
    var isRemovalInProgress = false
        private set
    private val handler = Handler(Looper.getMainLooper())
    private fun initViews(@LayoutRes rootView: Int, viewInitializer: BannerBar.CustomViewInitializer?) {
        if (rootView != 0) {
            inflate(context, rootView, this)
            viewInitializer?.initView(getChildAt(0))
        } else {
            inflate(context, R.layout.layout_banner, this)
        }
        if (getChildAt(0).layoutParams is LayoutParams) {
            val lp = getChildAt(0).layoutParams as LayoutParams
            lp.gravity = Gravity.BOTTOM
        }

        if (rootView == 0) {
            validateLayoutIntegrity()
        }
    }

    fun setParams(params: BannerBar.Params) {
        initViews(params.customViewResource, params.viewInitializer)
        duration = params.duration
        layoutGravity = params.bannerPosition
        animationInTop = params.animationInTop
        animationInBottom = params.animationInBottom
        animationOutTop = params.animationOutTop
        animationOutBottom = params.animationOutBottom
        isAutoDismissEnabled = params.enableAutoDismiss
        dismissListener = params.dismissListener
        binding.banner.setOnClickListener {
            dismiss()
        }
        if (params.iconResId != 0) {
            binding.ivIcon.visibility = VISIBLE
            binding.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, params.iconResId))
            binding.ivIcon.setColorFilter(Color.WHITE)

            if (params.iconAnimator != null) {
                params.iconAnimator?.setTarget(binding.ivIcon)
                params.iconAnimator?.start()
            }
        }
        if (!TextUtils.isEmpty(params.title)) {
            binding.tvTitle.visibility = VISIBLE
            binding.tvTitle.text = params.title
            if (params.titleColor != 0) {
                binding.tvTitle.setTextColor(ContextCompat.getColor(context, params.titleColor))
            }
        }
        if (params.backgroundColor != 0) {
            binding.banner.setBackgroundColor(ContextCompat.getColor(context, params.backgroundColor))
        }
        if (params.backGroundTint != null) {
            binding.banner.backgroundTintList = params.backGroundTint
        }
        createInAnim()
        createOutAnim()

        if (isAutoDismissEnabled) {
            handler.postDelayed({
                timeOutDismiss = true
                dismiss()
            }, duration)
        }
    }

    private fun validateLayoutIntegrity() {
        if (isAutoDismissEnabled) {
            handler.postDelayed({
                timeOutDismiss = true
                dismiss()
            }, duration)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        viewWidth = width.toFloat()
        dismissOffsetThreshold = viewWidth / 3
        if (layoutGravity == Gravity.TOP) {
            super.onLayout(changed, l, 0, r, binding.banner.measuredHeight)
        } else {
            super.onLayout(changed, l, t, r, b)
        }
    }

    private fun createInAnim() {
        val animationResId =
            if (layoutGravity == Gravity.BOTTOM) animationInBottom else animationInTop
        val slideInAnimation = AnimationUtils.loadAnimation(
            context, animationResId
        )
        animation = slideInAnimation
    }

    private fun createOutAnim() {
        val animationResId =
            if (layoutGravity == Gravity.BOTTOM) animationOutBottom else animationOutTop
        slideOutAnimation = AnimationUtils.loadAnimation(context, animationResId)
    }

    fun getDismissListener(): BannerDismissListener? {
        return dismissListener
    }

    fun dismiss(listener: BannerDismissListener? = null) {
        isRemovalInProgress = true
        handler.removeCallbacksAndMessages(null)
        if (listener != null) {
            dismissListener = listener
        }
        if (swipedOut) {
            removeFromParent()
            bannerListenerDismiss(BannerDismissListener.DismissType.USER_DISMISS)
            return
        }
        Log.d("animation:slide_out2","$slideOutAnimation")
        slideOutAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                Log.d("animation","start:${animation.duration}")
            }

            override fun onAnimationEnd(animation: Animation) {
                Log.d("animation","end")
                visibility = GONE
                removeFromParent()
                bannerListenerDismiss(dismissType)
            }

            override fun onAnimationRepeat(animation: Animation) {
                Log.d("animation","repeat")
            }
        })
        Log.d("animation:slide_out1","$slideOutAnimation")
        startAnimation(slideOutAnimation)
    }

    fun silentDismiss() {
        isRemovalInProgress = true
        handler.removeCallbacksAndMessages(null)
        bannerListenerDismiss(BannerDismissListener.DismissType.REPLACE_DISMISS)
        removeFromParent()
    }

    private val dismissType: Int
        get() {
            var dismissType: Int = BannerDismissListener.DismissType.PROGRAMMATIC_DISMISS
            if (timeOutDismiss) {
                dismissType = BannerDismissListener.DismissType.DURATION_COMPLETE
            }
            return dismissType
        }

    private fun bannerListenerDismiss(@BannerDismissListener.DismissType dismissType: Int) {
        if (dismissListener != null) {
            dismissListener?.onDismiss(dismissType)
        }
    }

    private fun removeFromParent() {
        Log.d("animation","removedFromParent")
        handler.postDelayed({
            val parent = parent
            if (parent != null) {
                clearAnimation()
                (parent as ViewGroup).removeView(this@Banner)
            }
        }, 200)
    }
}

interface BannerDismissListener {

    annotation class DismissType {
        companion object {
            var DURATION_COMPLETE = 0
            var USER_DISMISS = 1
            var PROGRAMMATIC_DISMISS = 3
            var REPLACE_DISMISS = 4
        }
    }

    fun onDismiss(@DismissType dismissType: Int)
}
