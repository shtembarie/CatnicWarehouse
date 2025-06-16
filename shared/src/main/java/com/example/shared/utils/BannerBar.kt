package com.example.shared.utils


import android.animation.AnimatorSet
import android.app.Activity
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.example.data.R

class BannerBar private constructor(private val context: Activity, params: Params?) {
    private val bannerView: Banner?

    init {
        if (params == null) {
            dismiss()
        }
        bannerView = Banner(context)
        bannerView.setParams(params ?: Params())
    }

    private fun show() {
        if (bannerView != null) {
            val decorView = context.window.decorView as ViewGroup
            val content = decorView.findViewById<ViewGroup>(android.R.id.content)
            if (bannerView.parent == null) {
                val parent =
                    if (bannerView.layoutGravity == Gravity.BOTTOM) content else decorView
                addBanner(parent, bannerView)
            }
        }
    }

    fun dismiss() {
        val decorView = context.window.decorView as ViewGroup
        val content = decorView.findViewById<ViewGroup>(android.R.id.content)
        removeFromParent(decorView)
        removeFromParent(content)
    }

    private fun removeFromParent(parent: ViewGroup) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child is Banner) {
                child.dismiss()
                return
            }
        }
    }

    private fun addBanner(parent: ViewGroup, banner: Banner) {
        if (banner.parent != null) {
            return
        }

        // if exists, remove existing banner
        val childCount = parent.childCount - 1
        for (i in childCount downTo 0) {
            val child = parent.getChildAt(i)
            if (child is Banner && !child.isRemovalInProgress) {
                removeStaleBanners(parent, i)
                return
            }
        }
        parent.addView(banner)
    }

    private fun removeStaleBanners(parent: ViewGroup, topBanner: Int) {
        for (i in 0 until topBanner) {
            val child = parent.getChildAt(i)
            if (child is Banner && !child.isRemovalInProgress) {
                child.silentDismiss()
                return
            }
        }
    }

    val view: View?
        get() = bannerView

    class Builder
    /**
     * Create a builder for an banner.
     */ internal constructor(private val context: Activity) {
        private val params = Params()
        fun setIcon(@DrawableRes iconResId: Int): Builder {
            params.iconResId = iconResId
            return this
        }

        fun setTitle(title: String?): Builder {
            params.title = title
            return this
        }

        fun setTitle(@StringRes resId: Int): Builder {
            params.title = context.getString(resId)
            return this
        }

        fun setDuration(duration: Long): Builder {
            params.duration = duration
            return this
        }

        fun setTitleColor(@ColorRes titleColor: Int): Builder {
            params.titleColor = titleColor
            return this
        }

        fun setMessageColor(@ColorRes messageColor: Int): Builder {
            params.messageColor = messageColor
            return this
        }

        fun setBackgroundColor(@ColorRes backgroundColor: Int): Builder {
            params.backgroundColor = backgroundColor
            return this
        }

        fun setBackgroundTint(color: ColorStateList): Builder {
            params.backGroundTint = color
            return this
        }

        fun setLayoutGravity(layoutGravity: Int): Builder {
            return setBannerPosition(layoutGravity)
        }

        fun setBannerPosition(bannerPosition: Int): Builder {
            params.bannerPosition = bannerPosition
            return this
        }

        fun setCustomView(@LayoutRes customView: Int): Builder {
            params.customViewResource = customView
            return this
        }

        fun setCustomViewInitializer(viewInitializer: CustomViewInitializer?): Builder {
            params.viewInitializer = viewInitializer
            return this
        }

        fun setAnimationIn(@AnimRes topAnimation: Int, @AnimRes bottomAnimation: Int): Builder {
            params.animationInTop = topAnimation
            params.animationInBottom = bottomAnimation
            return this
        }

        fun setAnimationOut(@AnimRes topAnimation: Int, @AnimRes bottomAnimation: Int): Builder {
            params.animationOutTop = topAnimation
            params.animationOutBottom = bottomAnimation
            return this
        }

        fun setEnableAutoDismiss(enableAutoDismiss: Boolean): Builder {
            params.enableAutoDismiss = enableAutoDismiss
            return this
        }

        fun setSwipeToDismiss(enableSwipeToDismiss: Boolean): Builder {
            params.enableSwipeToDismiss = enableSwipeToDismiss
            return this
        }

        fun setBannerListener(dismissListener: BannerDismissListener?): Builder {
            params.dismissListener = dismissListener
            return this
        }

        private fun create(): BannerBar {
            return BannerBar(context, params)
        }

        fun show(): BannerBar {
            val banner = create()
            banner.show()
            return banner
        }
    }

    class Params {
        var title: String? = null
        var message: String? = null
        var action: String? = null
        var enableSwipeToDismiss = true
        var enableAutoDismiss = true
        var iconResId = 0
        var backgroundColor = 0
        var titleColor = 0
        var messageColor = 0
        var duration: Long = 2000
        var bannerPosition = Gravity.TOP
        var customViewResource = 0
        var animationInTop: Int = R.anim.slide_in_from_top
        var animationInBottom: Int = R.anim.slide_in_from_bottom
        var animationOutTop: Int = R.anim.slide_out_to_top
        var animationOutBottom: Int = R.anim.slide_out_to_bottom
        var viewInitializer: CustomViewInitializer? = null
        var iconAnimator: AnimatorSet? = null
        var dismissListener: BannerDismissListener? = null
        var backGroundTint: ColorStateList? = null
    }

    interface CustomViewInitializer {
        fun initView(view: View?)
    }

    companion object {
        const val TOP = Gravity.TOP
        const val BOTTOM = Gravity.BOTTOM
        fun build(activity: Activity): Builder {
            return Builder(activity)
        }

        fun dismiss(activity: Activity) {
            BannerBar(activity, null)
        }
    }
}

