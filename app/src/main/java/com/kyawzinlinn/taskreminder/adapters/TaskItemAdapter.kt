package com.kyawzinlinn.taskreminder.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.ToDoListItemBinding

class TaskItemAdapter(private val onCheckClicked: (Task, Boolean) -> Unit, private val onLongClicked: (Task) -> Unit): ListAdapter<Task,TaskItemAdapter.ViewHolder>(ToDoDiffCallBack) {

    class ViewHolder(val binding: ToDoListItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(task: Task, onLongClicked: (Task) -> Unit){
            binding.apply {
                tvTodoTitle.text = task.title
                tvTodoDescription.text = task.description
                tvTodoTime.text = task.time
                cbTodoCheck.isChecked = task.isFinished

                if (task.isFinished){
                    tvTodoTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTodoDescription.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTodoTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }

            itemView.setOnLongClickListener {
                onLongClicked(task)

                true
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(ToDoListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),onLongClicked)

        holder.binding.cbTodoCheck.setOnCheckedChangeListener { compoundButton, b ->
            onCheckClicked(getItem(position),b)
        }
    }

    companion object ToDoDiffCallBack: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title == newItem.title && oldItem.id == newItem.id
        }

    }
}