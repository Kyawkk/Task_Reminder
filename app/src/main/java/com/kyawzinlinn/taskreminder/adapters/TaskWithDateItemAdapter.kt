package com.kyawzinlinn.taskreminder.adapters

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyawzinlinn.taskreminder.data.models.TaskWithDate
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.ToDoWithDayItemBinding
import com.kyawzinlinn.taskreminder.touch_helper.SwipeToDelete
import com.kyawzinlinn.taskreminder.util.isToday
import com.kyawzinlinn.taskreminder.util.isTomorrow

class TaskWithDateItemAdapter(
    private val onCheckClicked: (Task, Boolean) -> Unit,
    private val onSwipeToDelete: (Task) -> Unit,
    private val onTaskClicked: (Task) -> Unit
) : ListAdapter<TaskWithDate, TaskWithDateItemAdapter.ViewHolder>(ToDoDayDiffCallBack) {

    class ViewHolder(private val binding: ToDoWithDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            toDoWithDay: TaskWithDate,
            onCheckClicked: (Task, Boolean) -> Unit,
            onSwipeToDelete: (Task) -> Unit,
            onTaskClicked: (Task) -> Unit
        ) {

            var isExpanded = toDoWithDay.isExpanded

            binding.container.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
            binding.container.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)

            if (!isExpanded) {
                binding.ivDropdown.animate().rotation(180f)
                binding.rvToDo.visibility = View.GONE
            } else {
                binding.ivDropdown.animate().rotation(0f)
                binding.rvToDo.visibility = View.VISIBLE
            }

            binding.tvToDoDate.text =
                if (isToday(toDoWithDay.title)) "Today" else if (isTomorrow(toDoWithDay.title)) "Tomorrow" else toDoWithDay.title

            // setup task list recyclerview
            with(binding.rvToDo) {

                setHasFixedSize(true)
                val adapter = TaskItemAdapter(onCheckClicked = { toDo, b ->
                    onCheckClicked(toDo, b)
                }, onTaskClicked = { toDo ->
                    onTaskClicked(toDo)
                })
                itemAnimator = null
                isNestedScrollingEnabled = false
                setAdapter(adapter)
                adapter.submitList(toDoWithDay.todoList)
                swipeToDeleteTask(this, toDoWithDay.todoList, onSwipeToDelete)
            }

            binding.ivDropdown.setOnClickListener {

                if (toDoWithDay.isExpanded) {
                    binding.ivDropdown.animate().rotation(180f)
                    binding.rvToDo.visibility = View.GONE
                } else {
                    binding.ivDropdown.animate().rotation(0f)
                    binding.rvToDo.visibility = View.VISIBLE
                }

                toDoWithDay.isExpanded = !toDoWithDay.isExpanded
            }

        }

        private fun swipeToDeleteTask(
            recyclerview: RecyclerView,
            todoList: List<Task>,
            onSwipeToDelete: (Task) -> Unit
        ) {
            val swipeToDeleteCallBack = object : SwipeToDelete() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val itemToDelete = todoList.get(viewHolder.adapterPosition)
                    onSwipeToDelete(itemToDelete)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
            itemTouchHelper.attachToRecyclerView(recyclerview)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ToDoWithDayItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onCheckClicked, onSwipeToDelete, onTaskClicked)
    }

    companion object ToDoDayDiffCallBack : DiffUtil.ItemCallback<TaskWithDate>() {
        override fun areItemsTheSame(oldItem: TaskWithDate, newItem: TaskWithDate): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TaskWithDate, newItem: TaskWithDate): Boolean {
            return oldItem.title == newItem.title
        }

    }
}