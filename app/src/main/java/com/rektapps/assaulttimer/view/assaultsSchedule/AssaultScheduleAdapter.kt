package com.rektapps.assaulttimer.view.assaultsSchedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.databinding.AssaultListItemBinding
import com.rektapps.assaulttimer.viewmodel.AssaultScheduleListViewModel
import org.joda.time.Interval

class AssaultScheduleAdapter(private val assaultScheduleListViewModel: AssaultScheduleListViewModel) :
    ListAdapter<AssaultListItem, AssaultListItemViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AssaultListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.assault_list_item,
                parent,
                false),
            assaultScheduleListViewModel
        )

    override fun onBindViewHolder(holder: AssaultListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class AssaultListItemViewHolder(
    private val assaultListItemBinding: AssaultListItemBinding,
    private val assaultListViewModel: AssaultScheduleListViewModel
) : RecyclerView.ViewHolder(assaultListItemBinding.root) {

    fun bind(assaultListItem: AssaultListItem) {
        assaultListItemBinding.assaultListItem = assaultListItem
        assaultListItemBinding.viewModel = assaultListViewModel
        assaultListItemBinding.executePendingBindings()
    }
}

    private class DiffCallback : DiffUtil.ItemCallback<AssaultListItem>() {

    override fun areItemsTheSame(oldItem: AssaultListItem, newItem: AssaultListItem): Boolean {
        return oldItem.assault.id == newItem.assault.id
    }

    override fun areContentsTheSame(oldItem: AssaultListItem, newItem: AssaultListItem): Boolean {
        val oldAssault = oldItem.assault
        val newAssault = newItem.assault
        val oldAssaultInterval = Interval(oldAssault.start, oldAssault.end)
        val newAssaultInterval = Interval(newAssault.start, newAssault.end)
        val isNotificationChanged = oldItem.isNotificationEnabled == newItem.isNotificationEnabled
        return oldAssaultInterval.isEqual(newAssaultInterval) and isNotificationChanged
    }
}