package com.kyawzinlinn.taskreminder.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.kyawzinlinn.taskreminder.App
import com.kyawzinlinn.taskreminder.R
import com.kyawzinlinn.taskreminder.adapters.TaskWithDateItemAdapter
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.database.format
import com.kyawzinlinn.taskreminder.databinding.ActivityMainBinding
import com.kyawzinlinn.taskreminder.util.TASK_INTENT_EXTRA
import com.kyawzinlinn.taskreminder.util.isNotificationPermissionGranted
import com.kyawzinlinn.taskreminder.util.showCheckNotificationPermissionDialog
import com.kyawzinlinn.taskreminder.viewmodel.TaskViewModel
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel =
            ViewModelProvider(this, ToDoViewModelFactory((application as App).repository)).get(
                TaskViewModel::class.java
            )
        setContentView(binding.root)

        setUpTransition()
        setUpToDoListRv()
        setUpClickListeners()

    }

    private fun checkNotificationPermission() {
        if (!isNotificationPermissionGranted(this))
            showCheckNotificationPermissionDialog(this)
    }

    private fun setUpTransition() {
        window.exitTransition = MaterialFadeThrough()
        window.enterTransition = MaterialFadeThrough()
        window.reenterTransition = MaterialFadeThrough()
    }

    private fun setUpToDoListRv() {
        viewModel.taskList.observe(this) {

            if (it.size == 0) binding.noTaskLayout.root.visibility = View.VISIBLE
            else binding.noTaskLayout.root.visibility = View.GONE

            with(binding.rvToDoWithDay) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)

                val adapter = TaskWithDateItemAdapter(onCheckClicked = { toDo, isChecked ->
                    viewModel.updateToDo(
                        Task(
                            toDo.id,
                            toDo.title,
                            toDo.description,
                            toDo.date,
                            toDo.time,
                            isChecked
                        )
                    )
                }, onSwipeToDelete = { task ->
                    viewModel.deleteTask(task)
                    restoreDeleteTask(task)
                }, onTaskClicked = { task ->
                    if (!task.isCompleted) {
                        val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
                        intent.putExtra(TASK_INTENT_EXTRA, task)
                        startActivity(intent)
                    }
                })
                setAdapter(adapter)
                adapter.submitList(it.format())
            }
        }
    }

    private fun restoreDeleteTask(task: Task) {
        val snackbar = Snackbar.make(
            window.decorView,
            "Deleted ${task.title}",
            Snackbar.LENGTH_LONG
        )

        snackbar.setAction("restore") {
            viewModel.addTask(task)
        }
        snackbar.show()
    }

    private fun setUpClickListeners() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    floatingActionButton,
                    "shared"
                )
                val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
                startActivity(intent, options.toBundle())
            }

            ivSearchViewIcon.setOnClickListener {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@MainActivity,
                    ivSearchViewIcon,
                    getString(R.string.search_view_transition)
                )
                val intent = Intent(this@MainActivity, SearchTaskActivity::class.java)
                startActivity(intent, options.toBundle())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }
}