package com.kyawzinlinn.todoreminder.adapters

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyawzinlinn.todoreminder.animator.CustomAnimator
import com.kyawzinlinn.todoreminder.data.models.ToDo
import com.kyawzinlinn.todoreminder.data.models.ToDoWithDay
import com.kyawzinlinn.todoreminder.databinding.ToDoWithDayItemBinding

class ToDoWithDayItemAdapter(private val onCheckClicked: (ToDo, Boolean) -> Unit): ListAdapter<ToDoWithDay,ToDoWithDayItemAdapter.ViewHolder>(ToDoDayDiffCallBack) {

    class ViewHolder(private val binding: ToDoWithDayItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(toDoWithDay: ToDoWithDay, onCheckClicked: (ToDo, Boolean) -> Unit) {

            var isExpanded = toDoWithDay.isExpanded

            binding.container.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)

            if (!isExpanded){
                binding.ivDropdown.animate().rotation(180f)
                binding.rvToDo.visibility = View.GONE
            }else{
                binding.ivDropdown.animate().rotation(0f)
                binding.rvToDo.visibility = View.VISIBLE
            }

            binding.tvToDoDate.text = toDoWithDay.title
            with(binding.rvToDo){
                setHasFixedSize(true)
                val adapter = ToDoListItemAdapter(onCheckClicked = {toDo, b ->
                    onCheckClicked(toDo,b)
                })
                itemAnimator = null
                isNestedScrollingEnabled = false
                setAdapter(adapter)
                adapter.submitList(toDoWithDay.todoList)
            }

            binding.ivDropdown.setOnClickListener {

                if (toDoWithDay.isExpanded){
                    binding.ivDropdown.animate().rotation(180f)
                    binding.rvToDo.visibility = View.GONE
                }else{
                    binding.ivDropdown.animate().rotation(0f)
                    binding.rvToDo.visibility = View.VISIBLE
                }

                toDoWithDay.isExpanded = !toDoWithDay.isExpanded
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ToDoWithDayItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),onCheckClicked)
    }

    companion object ToDoDayDiffCallBack: DiffUtil.ItemCallback<ToDoWithDay>(){
        override fun areItemsTheSame(oldItem: ToDoWithDay, newItem: ToDoWithDay): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ToDoWithDay, newItem: ToDoWithDay): Boolean {
            return oldItem.title == newItem.title
        }

    }
}