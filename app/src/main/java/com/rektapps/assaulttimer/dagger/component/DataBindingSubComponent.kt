package com.rektapps.assaulttimer.dagger.component

import androidx.databinding.DataBindingComponent
import com.rektapps.assaulttimer.dagger.scope.DataBindingScope
import com.rektapps.assaulttimer.view.BindingAdapters
import dagger.Subcomponent

@Subcomponent
@DataBindingScope
interface DataBindingSubComponent: DataBindingComponent {

    override fun getBindingAdapters(): BindingAdapters

    @Subcomponent.Factory
    interface Factory{
        fun create(): DataBindingSubComponent
    }
}