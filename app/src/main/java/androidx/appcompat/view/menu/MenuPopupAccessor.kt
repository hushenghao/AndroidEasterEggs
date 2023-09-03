package androidx.appcompat.view.menu

import android.annotation.SuppressLint
import androidx.appcompat.widget.MenuPopupWindow
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.PopupMenuAccessor
import com.google.android.material.transition.platform.MaterialSharedAxis

object MenuPopupAccessor {
    @SuppressLint("RestrictedApi")
    fun setApi23Transitions(popupMenu: PopupMenu) {
        val menuPopupHelper = PopupMenuAccessor.getMenuPopupHelper(popupMenu) ?: return
        val standardMenuPopup = menuPopupHelper.popup as? StandardMenuPopup ?: return

        val menuPopupWindow: MenuPopupWindow = standardMenuPopup.mPopup
        // support api
        menuPopupWindow.setEnterTransition(MaterialSharedAxis(MaterialSharedAxis.Z, true))
        menuPopupWindow.setExitTransition(MaterialSharedAxis(MaterialSharedAxis.Z, false))
    }
}