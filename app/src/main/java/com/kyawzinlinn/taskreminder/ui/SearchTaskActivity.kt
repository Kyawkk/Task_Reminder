package com.kyawzinlinn.taskreminder.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyawzinlinn.taskreminder.App
import com.kyawzinlinn.taskreminder.adapters.TaskItemAdapter
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.ActivitySearchTaskBinding
import com.kyawzinlinn.taskreminder.util.TASK_INTENT_EXTRA
import com.kyawzinlinn.taskreminder.viewmodel.TaskViewModel
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModelFactory

class SearchTaskActivity : AppCompatActivity() {
    private lateinit var viewModel: TaskViewModel
    private lateinit var binding: ActivitySearchTaskBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchTaskBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(this, ToDoViewModelFactory((application as App).repository)).get(
                TaskViewModel::class.java
            )

        setContentView(binding.root)
        loadTasksFromDatabase()
    }

    private fun searchViewAction(tasks: List<Task>) {
        binding.edSearchTask.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setUpTasksRecyclerview(tasks.filter {
                    it.title.toLowerCase().contains(query.toString().toLowerCase())
                })
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun loadTasksFromDatabase() {
        viewModel.taskList.observe(this) {
            Handler().postDelayed(Runnable {
                searchViewAction(it)
                setUpTasksRecyclerview(it)
            }, 400)
        }
    }

    private fun setUpTasksRecyclerview(tasks: List<Task>) {

        if (tasks.size == 0) showNoTaskFoundLayout()
        else hideNoTaskFoundLayout()

        binding.rvSearchTask.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SearchTaskActivity)
            val adapter = TaskItemAdapter(
                onTaskClicked = { task ->
                    goToTaskDetailActivity(task)
                },
                onCheckClicked = { task, isCompleted ->
                    updateTask(task, isCompleted)
                }
            )
            setAdapter(adapter)
            adapter.submitList(tasks)
        }
    }

    private fun showNoTaskFoundLayout() {
        binding.noSearchTaskFound.apply {
            root.visibility = View.VISIBLE
            tvNoTaskDescription.text = if (binding.edSearchTask.text.trim()
                    .toString().length != 0
            ) "'${binding.edSearchTask.text.toString()}' is no found!"
            else "No Task Found!"
        }
    }

    private fun hideNoTaskFoundLayout() {
        binding.noSearchTaskFound.root.visibility = View.GONE
    }

    private fun updateTask(task: Task, isCompleted: Boolean) {
        viewModel.updateToDo(
            Task(
                task.id,
                task.title,
                task.description,
                task.date,
                task.time,
                isCompleted
            )
        )
    }

    private fun goToTaskDetailActivity(task: Task) {
        val intent = Intent(this, AddTaskActivity::class.java)
        intent.putExtra(TASK_INTENT_EXTRA, task)
        startActivity(intent)
    }
}