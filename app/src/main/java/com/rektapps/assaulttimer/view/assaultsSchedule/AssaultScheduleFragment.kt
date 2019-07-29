package com.rektapps.assaulttimer.view.assaultsSchedule

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.tabs.TabLayout
import com.rektapps.assaulttimer.databinding.AssaultsScheduleBinding
import com.rektapps.assaulttimer.model.enums.AssaultType
import com.rektapps.assaulttimer.utils.getTitleIDForRegion
import com.rektapps.assaulttimer.view.MainActivity
import com.rektapps.assaulttimer.viewmodel.AssaultScheduleListViewModel
import com.rektapps.assaulttimer.viewmodel.shared.EventsViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.assaults_schedule.*
import kotlinx.android.synthetic.main.main_layout.*
import javax.inject.Inject

class AssaultScheduleFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var assaultListViewModel: AssaultScheduleListViewModel

    private lateinit var eventsViewModel: EventsViewModel

    private val onAssaultTypeTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {
            /**unused*/
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
            /**unused*/
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            val assaultType = if (p0!!.position == 0) AssaultType.BFA else AssaultType.LEGION
            assaultListViewModel.onAssaultTypeChanged(assaultType)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initializeViewModels()

        eventsViewModel.getRegionChangedEvent().observe(this, Observer {
            assaultListViewModel.onRegionChanged(it)
            activity!!.setTitle(getTitleIDForRegion(it))
        })

        assaultListViewModel.getRestoreRegionTitleEvent()
            .observe(this, Observer { activity!!.setTitle(getTitleIDForRegion(it)) })

        assaultListViewModel.getRestoreSelectedAssaultTypeEvent().observe(this, Observer { selectAssaultTypeTab(it) })

        assaultListViewModel.getIsListLoadingLiveData().observe(this, Observer {
            hiddenWhenListLoading.isVisible = !it
            assaultsScheduleProgressBar.isVisible = it
        })

        eventsViewModel.getNotificationClickedEvent()
            .observe(this, Observer { assaultListViewModel.onNotificationClicked(it) })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DataBindingUtil.inflate<AssaultsScheduleBinding>(
            inflater,
            com.rektapps.assaulttimer.R.layout.assaults_schedule,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = assaultListViewModel
        }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assaultSchedule.adapter =
            AssaultScheduleAdapter(assaultListViewModel)

        (assaultSchedule.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false //disable blinking when notifyItemChanged() is called


        assaultListViewModel.getUpdateItemOnPositionEvent()
            .observe(this, Observer { assaultSchedule.adapter?.notifyItemChanged(it) })

        assaultListViewModel.getNotificationForActiveAssaultErrorEvent().observe(
            this,
            Observer {
                Toast.makeText(
                    context,
                    com.rektapps.assaulttimer.R.string.activeAssaultNotificationErrorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            })

        assaultsTypeTabLayout.addOnTabSelectedListener(onAssaultTypeTabSelectedListener)
    }

    private fun initializeViewModels() {
        eventsViewModel = ViewModelProviders.of(activity!!).get()
        assaultListViewModel =
            ViewModelProvider((activity as MainActivity).navHost.viewModelStore, viewModelFactory).get()
        lifecycle.addObserver(assaultListViewModel)
    }

    private fun selectAssaultTypeTab(assaultType: AssaultType) {
        val assaultTypeTabPosition = when (assaultType) {
            AssaultType.BFA -> 0
            AssaultType.LEGION -> 1
        }
        assaultsTypeTabLayout.getTabAt(assaultTypeTabPosition)!!.select()
    }

}