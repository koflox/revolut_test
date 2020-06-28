package com.koflox.revoluttest.ui.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.koflox.revoluttest.util.Network

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected abstract val viewModel: VM

    protected lateinit var dataBinding: DB

    protected var snackbar: Snackbar? = null

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        dataBinding.lifecycleOwner = this
        initViews()
        addObservers()
    }

    open fun initViews() {}

    open fun addObservers() {
        Network.observe(this, Observer {
            viewModel.onNetworkAvailabilityChanged(it)
        })
        viewModel.networkAvailability.observe(this, Observer { (isAvailable, message, duration) ->
            snackbar?.dismiss()
            snackbar = when {
                isAvailable -> null
                else -> showSnackbarMessage(message, duration)
            }
        })
    }

    fun showSnackbarMessage(message: String, duration: Int = Snackbar.LENGTH_LONG): Snackbar? {
        snackbar?.dismiss()
        snackbar = Snackbar.make(findViewById(android.R.id.content), message, duration).apply {
            show()
        }
        return snackbar
    }

}