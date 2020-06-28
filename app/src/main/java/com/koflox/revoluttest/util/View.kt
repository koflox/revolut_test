package com.koflox.revoluttest.util

import android.content.Context
import android.text.TextWatcher
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.doOnTextChanged

val selectionActionModeCallback = object : ActionMode.Callback {
    override fun onCreateActionMode(mode: ActionMode?, menu: Menu) = false
    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu) = false
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem) = false
    override fun onDestroyActionMode(mode: ActionMode?) = Unit
}

/**
 * Prevents showing copy/paste option on selector click
 * //todo doesn't work on MIUI
 */
fun TextView.disableCopyPaste() {
    isLongClickable = false
    setTextIsSelectable(false)
    customSelectionActionModeCallback = selectionActionModeCallback
}

fun TextView.setRebindableTextWatcher(doOnTextChanged: (String) -> Unit) {
    (tag as? TextWatcher)?.let { oldWatcher ->
        removeTextChangedListener(oldWatcher)
    }
    doOnTextChanged { text, _, _, _ ->
        text ?: return@doOnTextChanged
        doOnTextChanged.invoke(text.toString())
    }.also { newWatcher ->
        tag = newWatcher
    }
}

fun View.hideKeyboard() {
    val inputManager: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputManager?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val inputManager: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputManager?.showSoftInput(this, 0)
}