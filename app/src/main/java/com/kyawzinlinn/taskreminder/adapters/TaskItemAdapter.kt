package com.kyawzinlinn.taskreminder.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.ToDoListItemBinding
import com.kyawzinlinn.taskreminder.util.convertDateAndTimeToSeconds

class TaskItemAdapter(
    private val onCheckClicked: (Task, Boolean) -> Unit,
    private val onTaskClicked: (Task) -> Unit
) : ListAdapter<Task, TaskItemAdapter.ViewHolder>(ToDoDiffCallBack) {

    class ViewHolder(val binding: ToDoListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                tvTodoTitle.text = task.title
                tvTodoDescription.text = task.description
                tvTodoTime.text = task.time
                cbTodoCheck.isChecked = task.isCompleted

                if (task.isCompleted) {
                    tvTodoTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTodoDescription.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTodoTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else if (!task.isCompleted && convertDateAndTimeToSeconds(
                        task.date,
                        task.time
                    ) <= 0
                ) {
                    cbTodoCheck.isClickable = false

                    tvTodoTitle.paintFlags = Paint.SUBPIXEL_TEXT_FLAG
                    tvTodoDescription.paintFlags = Paint.SUBPIXEL_TEXT_FLAG
                    tvTodoTime.paintFlags = Paint.SUBPIXEL_TEXT_FLAG
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ToDoListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

        holder.binding.cbTodoCheck.setOnCheckedChangeListener { compoundButton, b ->
            onCheckClicked(getItem(position), b)
        }

        holder.binding.taskItem.setOnClickListener { onTaskClicked(getItem(position)) }
    }

    companion object ToDoDiffCallBack : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title == newItem.title && oldItem.id == newItem.id
        }

    }
}