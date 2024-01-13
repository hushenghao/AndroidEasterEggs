@file:SuppressLint("RestrictedApi")

package com.dede.android_eggs.views.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Outline
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.withStyledAttributes
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dede.android_eggs.R
import com.dede.android_eggs.databinding.FragmentSettingsBinding
import com.dede.android_eggs.databinding.ItemSettingPrefButtonBinding
import com.dede.android_eggs.databinding.ItemSettingPrefGroupBinding
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.settings.more.MoreSettingsActivity
import com.dede.basic.dpf
import com.dede.basic.requireDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehaviorMaterialShapeDrawableAccessor
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.IOverScrollState
import me.everything.android.ui.overscroll.IOverScrollStateListener
import me.everything.android.ui.overscroll.IOverScrollUpdateListener
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter
import kotlin.math.abs
import com.google.android.material.R as M3R

class SettingsFragment : BottomSheetDialogFragment(R.layout.fragment_settings) {

    var onSlide: ((offset: Float) -> Unit)? = null

    var onPreDismiss: (() -> Unit)? = null

    var onDismiss: (() -> Unit)? = null

    private var lastSlideOffset: Float = -1f
    private val callback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_SETTLING) {
                onPreDismiss?.invoke()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (lastSlideOffset == slideOffset) return
            onSlide?.invoke(slideOffset)
            lastSlideOffset = slideOffset
        }
    }

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    override fun onDismiss(dialog: DialogInterface) {
        onDismiss?.invoke()
        super.onDismiss(dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        EdgeUtils.applyEdge(dialog.window)
        val bottomSheetBehavior = dialog.behavior
        bottomSheetBehavior.addBottomSheetCallback(callback)
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.skipCollapsed = true
        dialog.dismissWithAnimation = true
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (pref in SettingsPrefs.providerPrefs()) {
            if (!pref.enable) continue
            binding.llSettings.addView(
                createPrefView(pref, requireContext()),
                binding.llSettings.childCount - 1
            )
        }
        binding.ivMoreSettings.setImageDrawable(
            FontIconsDrawable(
                requireContext(),
                Icons.Rounded.keyboard_double_arrow_up
            )
        )
        binding.ivMoreSettings.setOnClickListener { openMoreSettings() }

        requireContext().withStyledAttributes(
            M3R.style.Widget_Material3_BottomSheet,
            intArrayOf(M3R.attr.shapeAppearance)
        ) {
            val shapeAppearanceId = getResourceId(0, -1)
            if (shapeAppearanceId == -1) {
                return@withStyledAttributes
            }
            requireContext().withStyledAttributes(
                shapeAppearanceId,
                intArrayOf(M3R.attr.cornerSize)
            ) {
                val radius = getDimension(0, 32.dpf)
                binding.root.clipToOutline = true
                binding.root.outlineProvider = createTopRoundOutline(radius)
            }
        }
        val listeners = OpenSettingsOverScrollListeners()
        val scrollBounceEffectDecorator = VerticalOverScrollBounceEffectDecorator(listeners)
        scrollBounceEffectDecorator.setOverScrollUpdateListener(listeners)
        scrollBounceEffectDecorator.setOverScrollStateListener(listeners)

        LocalEvent.receiver(this).register(SettingsPrefs.ACTION_CLOSE_SETTING) {
            dismissAllowingStateLoss()
        }
    }

    private fun openMoreSettings() {
        startActivity(Intent(requireContext(), MoreSettingsActivity::class.java))
    }

    private inner class OpenSettingsOverScrollListeners : IOverScrollDecoratorAdapter,
        IOverScrollStateListener, IOverScrollUpdateListener {

        override fun getView(): View {
            return binding.scrollView
        }

        override fun isInAbsoluteStart(): Boolean {
            return false
        }

        override fun isInAbsoluteEnd(): Boolean {
            val bottomSheetBehavior = (requireDialog() as BottomSheetDialog).behavior
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                return false
            }
            return !binding.scrollView.canScrollVertically(1)
        }

        private var needOpen = false
        override fun onOverScrollStateChange(
            decor: IOverScrollDecor?,
            oldState: Int,
            newState: Int
        ) {
            if (needOpen && newState == IOverScrollState.STATE_IDLE) {
                openMoreSettings()
                needOpen = false
            }
        }

        override fun onOverScrollUpdate(decor: IOverScrollDecor?, state: Int, offset: Float) {
            if (!needOpen && abs(offset) >= binding.tvMoreSettings.height) {
                binding.tvMoreSettings.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                needOpen = true
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val bottomSheetBehavior = (requireDialog() as BottomSheetDialog).behavior
        val shapeDrawable =
            BottomSheetBehaviorMaterialShapeDrawableAccessor.getMaterialShapeDrawable(
                bottomSheetBehavior
            )
        binding.scrollView.background = shapeDrawable
    }

    private fun createTopRoundOutline(radius: Float): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                val path = Path().apply {
                    addRoundRect(
                        0f, 0f, view.width.toFloat(), view.height.toFloat(),
                        floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f),
                        Path.Direction.CW
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    outline.setPath(path)
                } else {
                    @Suppress("DEPRECATION")
                    outline.setConvexPath(path)
                }
            }
        }
    }

    private fun createPrefView(pref: SettingPref, context: Context): View {
        val binding = ItemSettingPrefGroupBinding.inflate(LayoutInflater.from(context))
        if (pref.titleRes != View.NO_ID) {
            binding.tvTitle.setText(pref.titleRes)
        } else if (pref.title != null) {
            binding.tvTitle.text = pref.title
        }

        fun updateOptions() {
            binding.btGroup.removeAllViewsInLayout()
            for (op in pref.options) {
                addOptionButton(context, binding.btGroup, op)
            }

            val op = pref.getSelectedOption(context)
            if (op != null) {
                binding.btGroup.check(op.id)
            }
        }

        updateOptions()

        val listener = object : SettingPref.PrefViewListener {
            override fun onUpdateOptions(options: List<SettingPref.Op>) {
                updateOptions()
            }

            override fun onSelectedOptionChange(op: SettingPref.Op) {
                binding.btGroup.check(op.id)
            }
        }
        binding.btGroup.tag = listener// hold viewListener
        pref.setViewListener(listener)

        binding.btGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val op = pref.findOptionById(checkedId)
                if (op != null) {
                    pref.preformOptionSelected(context, op)
                }
            }
        }
        return binding.root
    }

    private fun addOptionButton(context: Context, parent: ViewGroup, op: SettingPref.Op): View {
        val binding = ItemSettingPrefButtonBinding
            .inflate(LayoutInflater.from(context), parent, true)
        return binding.root.apply {
            id = op.id
            if (op.titleRes != View.NO_ID) {
                text = context.getString(op.titleRes)
            } else if (op.title != null) {
                text = op.title
            } else {
                iconPadding = 0
            }
            if (text != null) {
                TooltipCompat.setTooltipText(this, text)
            }
            val iconMaker = op.iconMaker
            if (iconMaker != null) {
                icon = iconMaker.invoke(context, this)
                iconSize = 0
            } else if (op.iconUnicode != null) {
                icon = FontIconsDrawable(context, op.iconUnicode, M3R.attr.colorSecondary)
            } else if (op.iconRes != View.NO_ID) {
                icon = context.requireDrawable(op.iconRes)
            }
        }
    }

}