package com.koflox.revoluttest.ui.binding

import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.koflox.revoluttest.data.entities.rates.response.CURSOR_NO_SELECTION
import com.koflox.revoluttest.util.notNull

@Suppress("UNCHECKED_CAST")
@BindingAdapter("data")
fun <T> setData(recyclerView: RecyclerView, data: List<T>?) {
    notNull(recyclerView.adapter as? BindableAdapter<T>, data) { adapter, list ->
        adapter.setData(list)
    }
}

@BindingAdapter("iconText", "iconColor", "iconDiameter")
fun setCountryIcon(iv: ImageView, currencyCode: String, iconColor: Int, iconDiameter: Float) {
    val countryCode = currencyCode.take(2)
    val cornerRadius = iconDiameter / 2
    val icon = TextDrawable.builder()
        .buildRoundRect(countryCode, iconColor, cornerRadius.toInt())
    iv.setImageDrawable(icon)
}

@BindingAdapter("cursorSelection")
fun setCursorSelection(et: EditText, cursorSelection: Int) {
    if (cursorSelection != CURSOR_NO_SELECTION) et.setSelection(cursorSelection)
}