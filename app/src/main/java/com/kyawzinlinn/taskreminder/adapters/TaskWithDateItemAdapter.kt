package com.kyawzinlinn.taskreminder.adapters

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyawzinlinn.taskreminder.data.models.TaskWithDate
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.ToDoWithDayItemBinding

class TaskWithDateItemAdapter(private val onCheckClicked: (Task, Boolean) -> Unit, private val onLonClicked: (Task) -> Unit): ListAdapter<TaskWithDate,TaskWithDateItemAdapter.ViewHolder>(ToDoDayDiffCallBack) {

    class ViewHolder(private val binding: ToDoWithDayItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(toDoWithDay: TaskWithDate, onCheckClicked: (Task, Boolean) -> Unit, onLonClicked: (Task) -> Unit) {

            var isExpanded = toDoWithDay.isExpanded

            binding.container.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
            binding.container.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)

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
                val adapter = TaskItemAdapter(onCheckClicked = { toDo, b ->
                    onCheckClicked(toDo,b)
                }, onLongClicked = {toDo ->
                    onLonClicked(toDo)
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
        holder.bind(getItem(position),onCheckClicked, onLonClicked)
    }

    companion object ToDoDayDiffCallBack: DiffUtil.ItemCallback<TaskWithDate>(){
        override fun areItemsTheSame(oldItem: TaskWithDate, newItem: TaskWithDate): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TaskWithDate, newItem: TaskWithDate): Boolean {
            return oldItem.title == newItem.title
        }

    }
}