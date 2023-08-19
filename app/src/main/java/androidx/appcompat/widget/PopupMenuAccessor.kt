package androidx.appcompat.widget

import androidx.appcompat.view.menu.MenuPopupHelper

object PopupMenuAccessor {

    fun getMenuPopupHelper(popupMenu: PopupMenu): MenuPopupHelper? {
        return popupMenu.mPopup
    }
}