package com.kyawzinlinn.todoreminder.adapters

import android.animation.LayoutTransition
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyawzinlinn.todoreminder.data.models.ToDo
import com.kyawzinlinn.todoreminder.databinding.ToDoListItemBinding

class ToDoListItemAdapter(private val onCheckClicked: (ToDo,Boolean) -> Unit): ListAdapter<ToDo,ToDoListItemAdapter.ViewHolder>(ToDoDiffCallBack) {

    class ViewHolder(val binding: ToDoListItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(toDo: ToDo){
            binding.apply {
                tvTodoTitle.text = toDo.title
                tvTodoDescription.text = toDo.description
                tvTodoTime.text = toDo.time
                cbTodoCheck.isChecked = toDo.isFinished

                if (toDo.isFinished){
                    tvTodoTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTodoDescription.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvTodoTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
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
        holder.bind(getItem(position))

        holder.binding.cbTodoCheck.setOnCheckedChangeListener { compoundButton, b ->
            onCheckClicked(getItem(position),b)
        }
    }

    companion object ToDoDiffCallBack: DiffUtil.ItemCallback<ToDo>(){
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean {
            return oldItem.title == newItem.title && oldItem.id == newItem.id
        }

    }
}