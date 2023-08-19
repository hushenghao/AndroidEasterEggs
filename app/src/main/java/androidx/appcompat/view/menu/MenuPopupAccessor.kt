package androidx.appcompat.view.menu

import android.annotation.SuppressLint
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.PopupMenuAccessor
import com.google.android.material.transition.platform.MaterialSharedAxis

object MenuPopupAccessor {
    @SuppressLint("RestrictedApi")
    fun setTransitions(popupMenu: PopupMenu) {
        val menuPopupHelper = PopupMenuAccessor.getMenuPopupHelper(popupMenu) ?: return
        val standardMenuPopup = menuPopupHelper.popup as? StandardMenuPopup ?: return

        standardMenuPopup.mPopup.setEnterTransition(MaterialSharedAxis(MaterialSharedAxis.Z, true))
        standardMenuPopup.mPopup.setExitTransition(MaterialSharedAxis(MaterialSharedAxis.Z, false))
    }
}