package com.koflox.revoluttest.ui.rates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.koflox.revoluttest.data.entities.rates.displayed.DisplayedCurrency
import com.koflox.revoluttest.databinding.ItemCurrencyBinding
import com.koflox.revoluttest.ui.binding.BindableAdapter
import com.koflox.revoluttest.util.disableCopyPaste
import com.koflox.revoluttest.util.setRebindableTextWatcher
import com.koflox.revoluttest.util.showKeyboard
import java.util.*

class RatesAdapter(
    private val viewModel: RatesViewModel
) : RecyclerView.Adapter<RateViewHolder>(),
    BindableAdapter<DisplayedCurrency> {

    private val diffCallback = RatesDiffUtilCallback()
    private val data = LinkedList<DisplayedCurrency>()

    override fun setData(data: List<DisplayedCurrency>) {
        diffCallback.setData(this.data, data)
        val ratesDiffResult = DiffUtil.calculateDiff(diffCallback)
        this.data.clear()
        this.data.addAll(data)
        ratesDiffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val dataBinding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RateViewHolder(dataBinding, viewModel)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(data[position])
    }

}

class RateViewHolder(
    private val dataBinding: ItemCurrencyBinding,
    private val vm: RatesViewModel
) : RecyclerView.ViewHolder(dataBinding.root) {

    fun bind(currency: DisplayedCurrency) {
        dataBinding.apply {
            viewModel = vm
            item = currency

            itemView.setOnClickListener {
                etCurrencyAmount.requestFocus()
            }
            etCurrencyAmount.apply {
                disableCopyPaste()
                setRebindableTextWatcher { amount ->
                    vm.onAmountChanged(DisplayedCurrency(currency.name, amount))
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        showKeyboard()
                        vm.onCurrencyChosen(currency)
                    }
                }
            }
            executePendingBindings()
        }
    }

}