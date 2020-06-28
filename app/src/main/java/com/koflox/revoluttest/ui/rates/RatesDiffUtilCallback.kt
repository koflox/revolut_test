package com.koflox.revoluttest.ui.rates

import androidx.recyclerview.widget.DiffUtil
import com.koflox.revoluttest.data.entities.rates.displayed.DisplayedCurrency

class RatesDiffUtilCallback : DiffUtil.Callback() {

    private val oldList = mutableListOf<DisplayedCurrency>()
    private val newList = mutableListOf<DisplayedCurrency>()

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].amount == newList[newItemPosition].amount
    }

    fun setData(oldList: List<DisplayedCurrency>, newList: List<DisplayedCurrency>) {
        this.oldList.clear()
        this.oldList.addAll(oldList)
        this.newList.clear()
        this.newList.addAll(newList)
    }

}