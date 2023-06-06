package com.kyawzinlinn.todoreminder.ui

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyawzinlinn.todoreminder.AddTaskActivity
import com.kyawzinlinn.todoreminder.App
import com.kyawzinlinn.todoreminder.adapters.ToDoWithDayItemAdapter
import com.kyawzinlinn.todoreminder.data.models.ToDo
import com.kyawzinlinn.todoreminder.data.models.ToDoWithDay
import com.kyawzinlinn.todoreminder.database.asToDoModel
import com.kyawzinlinn.todoreminder.database.format
import com.kyawzinlinn.todoreminder.databinding.ActivityMainBinding
import com.kyawzinlinn.todoreminder.viewmodel.ToDoViewModel
import com.kyawzinlinn.todoreminder.viewmodel.ToDoViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ToDoViewModelFactory((application as App).database.todoDao())).get(ToDoViewModel::class.java)
        setContentView(binding.root)

        binding.mainContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        setUpToDoListRv()
        setUpClickListeners()

    }

    private fun setUpToDoListRv(){
        viewModel.toDoList.observe(this){
            with(binding.rvToDoWithDay){
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@MainActivity)
                val adapter = ToDoWithDayItemAdapter({toDo, isChecked ->
                    viewModel.updateToDo(
                        ToDo(
                            toDo.id,
                            toDo.title,
                            toDo.description,
                            toDo.date,
                            toDo.time,
                            isChecked
                        )
                    )
                })
                setAdapter(adapter)
                adapter.submitList(it.format())
            }
        }
    }

    private fun setUpClickListeners(){
        with(binding){
            floatingActionButton.setOnClickListener { startActivity(Intent(this@MainActivity,AddTaskActivity::class.java)) }
        }
    }
}