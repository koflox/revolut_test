package com.koflox.revoluttest.ui.rates

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.koflox.revoluttest.R
import com.koflox.revoluttest.databinding.ActivityRatesBinding
import com.koflox.revoluttest.ui.base.BaseActivity
import com.koflox.revoluttest.ui.views.UniversalItemDecorator
import com.koflox.revoluttest.util.EventObserver
import kotlinx.android.synthetic.main.activity_rates.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class RatesActivity : BaseActivity<ActivityRatesBinding, RatesViewModel>() {

    override val viewModel: RatesViewModel
        get() = getViewModel()
    private lateinit var ratesAdapter: RatesAdapter

    override fun getLayoutId() = R.layout.activity_rates

    override fun initViews() {
        dataBinding.viewModel = viewModel
        tvRates.apply {
            layoutManager = LinearLayoutManager(this@RatesActivity)
            adapter = RatesAdapter(viewModel).also { ratesAdapter = it }
            addItemDecoration(
                UniversalItemDecorator(
                    resources.getDimensionPixelSize(R.dimen.indent_medium_plus),
                    UniversalItemDecorator.Type.VERTICAL
                )
            )
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    override fun addObservers() {
        super.addObservers()
        viewModel.title.observe(this, Observer {
            title = it
        })
        viewModel.baseDisplayedCurrency.observe(this, EventObserver {
            tvRates.post { tvRates.smoothScrollToPosition(0) }
        })
        viewModel.userMessage.observe(this, EventObserver { errorMessage ->
            showSnackbarMessage(errorMessage)
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        viewModel.onStop()
        super.onStop()
    }

}