package com.rektapps.assaulttimer.view

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rektapps.assaulttimer.R
import com.rektapps.assaulttimer.dagger.scope.DataBindingScope
import com.rektapps.assaulttimer.model.enums.AssaultState
import com.rektapps.assaulttimer.storage.settings.AppConfig
import com.rektapps.assaulttimer.time.formatter.AssaultDateTimeFormatter
import com.rektapps.assaulttimer.view.assaultsSchedule.AssaultListItem
import com.rektapps.assaulttimer.view.assaultsSchedule.AssaultScheduleAdapter
import org.joda.time.DateTime
import org.joda.time.Duration
import javax.inject.Inject

@DataBindingScope
class BindingAdapters @Inject constructor(
    private val assaultDateTimeFormatter: AssaultDateTimeFormatter,
    val appConfig: AppConfig
) {

    @BindingAdapter(value = ["startDateTime", "endDateTime"])
    fun TextView.setAssaultDate(startDateTime: DateTime, endDateTime: DateTime) {
        val startDateTimeString = assaultDateTimeFormatter.getAssaultListItemDateTimeString(startDateTime)
        val endDateTimeString = assaultDateTimeFormatter.getAssaultListItemDateTimeString(endDateTime)
        text = context.getString(R.string.assaultStartEndTimeTemplate, startDateTimeString, endDateTimeString)
    }

    @BindingAdapter("setTimer")
    fun TextView.setTimer(remainingDuration: Duration) {
        text = assaultDateTimeFormatter.getTimerDurationString(remainingDuration)
    }

    @BindingAdapter("setAssaultStateText")
    fun TextView.setAssaultStateText(assaultState: AssaultState?) {
        assaultState?.let {
            setText(
                when (assaultState) {
                    AssaultState.INCOMING -> R.string.assaultIncoming
                    AssaultState.ACTIVE -> R.string.assaultActive
                    AssaultState.ENDING -> R.string.assaultEnding
                    AssaultState.ENDED -> R.string.assaultEnded
                }
            )
        }
    }

    @BindingAdapter("setAssaults")
    fun RecyclerView.setAssault(assaultListItems: List<AssaultListItem>?) {
        assaultListItems?.let { (adapter as AssaultScheduleAdapter).submitList(assaultListItems) }
    }

    @BindingAdapter("setAssaultStateTextColor")
    fun TextView.setAssaultStateTextColor(assaultState: AssaultState?) {
        assaultState?.let {
            val color = when (assaultState) {
                AssaultState.INCOMING, AssaultState.ENDED -> getTextViewPrimaryColorFromCurrentTheme(context)
                AssaultState.ACTIVE -> ContextCompat.getColor(context, R.color.assaultActiveColor)
                AssaultState.ENDING -> ContextCompat.getColor(context, R.color.assaultEndingColor)
            }
            setTextColor(color)
        }
    }

    //Return white text color if theme is dark and black text color if theme is white
    private fun getTextViewPrimaryColorFromCurrentTheme(context: Context): Int {
        val typedValue = TypedValue()
        val textColorPrimaryID = android.R.attr.textColorPrimary
        context.theme.resolveAttribute(textColorPrimaryID, typedValue, true)

        val textPrimaryColorReference = typedValue.data
        val arr = context.theme.obtainStyledAttributes(
            textPrimaryColorReference,
            intArrayOf(textColorPrimaryID)
        ) //this method shows an error in logs about invalid id and i don't know why but everything works fine
        val textViewPrimaryColorFromCurrentTheme = arr.getColor(0, -1)

        arr.recycle()
        return textViewPrimaryColorFromCurrentTheme
    }

}

