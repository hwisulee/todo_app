package com.sld.todolist.adapter

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sld.todolist.db.AppTable
import com.sld.todoyoujob.R
import com.sld.todoyoujob.databinding.ListItemBinding
import java.util.*

class AppAdapter(): ListAdapter<AppTable, AppAdapter.ViewHolder>(diffUtil) {
    private lateinit var touchListener: OnItemTouchListener

    val selectedList = mutableListOf<AppTable>()
    var isLongClicked: Boolean = false
    var onItemClick: ((AppTable) -> Unit)? = null
    var onItemLongClick: ((AppTable) -> Unit)? = null
    var onItemChecked: ((AppTable) -> Unit)? = null

    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    inner class ViewHolder(private val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }

            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(getItem(adapterPosition))
                return@setOnLongClickListener(true)
            }

            binding.itemHandle.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    touchListener.onStartDrag(this)
                }
                return@setOnTouchListener(false)
            }

            binding.checkbox.setOnClickListener {
                if (binding.checkbox.isChecked) {
                    selectedList.add(getItem(adapterPosition))
                } else {
                    selectedList.remove(getItem(adapterPosition))
                }

                onItemChecked?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(data: AppTable) {
            with(binding) {
                title.text = data.title
                description.text = data.description
                binding.checkbox.visibility = if (isLongClicked) View.VISIBLE else View.GONE
                if (!isLongClicked)  binding.checkbox.isChecked = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppAdapter.ViewHolder {
        val view = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        if (parent.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            view.itemHandle.setImageDrawable(getDrawable(parent.context, R.drawable.ic_baseline_drag_handle_24))
        } else {
            view.itemHandle.setImageDrawable(getDrawable(parent.context, R.drawable.ic_baseline_drag_handle_dark_24))
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    fun getDataAt(position: Int): AppTable = getItem(position)

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<AppTable>() {
            override fun areItemsTheSame(oldItem: AppTable, newItem: AppTable): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AppTable, newItem: AppTable): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemTouchListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun onStartDrag(listener: OnItemTouchListener) {
        this.touchListener = listener
    }
}