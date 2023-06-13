package com.kyawzinlinn.taskreminder.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.kyawzinlinn.taskreminder.App
import com.kyawzinlinn.taskreminder.R
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.ActivityAddTaskBinding
import com.kyawzinlinn.taskreminder.util.formatHour
import com.kyawzinlinn.taskreminder.util.showDatePicker
import com.kyawzinlinn.taskreminder.util.showTimePicker
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModel
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModelFactory
import java.time.LocalDate
import java.time.LocalTime

const val DELAY_MINUTE = 15

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var viewModel: ToDoViewModel
    private var date = LocalDate.now().toString()
    private var time = formatHour(LocalTime.now().hour, LocalTime.now().hour)
    private var delayTime = DELAY_MINUTE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ToDoViewModelFactory((application as App).database.taskDao())).get(ToDoViewModel::class.java)

        setContentView(binding.root)

        bindUI()

        setUpClickListeners()
    }

    private fun bindUI(){
        with(binding){
            tvDate.text = date
            tvTime.text = time
            tvBefore.setText("$delayTime min before")
        }
        enterAnimation(binding.item1,binding.item2,binding.item3)
    }

    private fun setUpClickListeners(){
        with(binding){
            cvBack.setOnClickListener { onBackPressed() }
            cvSaveTask.setOnClickListener { saveTask() }
            cvDate.setOnClickListener { showDatePickerDialog() }
            cvTime.setOnClickListener { showTimePickerDialog() }
            cvBefore.setOnClickListener {
                binding.tvBefore.isFocusable = true
                binding.tvBefore.isFocusableInTouchMode = true
                binding.tvBefore.requestFocus()
            }
        }
    }

    private fun showDatePickerDialog() {
        showDatePicker(supportFragmentManager){
            binding.tvDate.text = it
            date = it
        }
    }

    private fun showTimePickerDialog(){
        showTimePicker(supportFragmentManager){
            binding.tvTime.text = it
            time = it
        }
    }

    private fun saveTask(){
        val title = binding.edTaskTitle.text.toString()
        val description = binding.edTaskDescription.text.toString()

        if (title.isEmpty()) binding.edTaskTitle.setError("Title must not be empty!")
        if (description.isEmpty()) binding.edTaskDescription.setError("Description must not be empty!")

        if(title.isNotEmpty() && description.isNotEmpty()){
            viewModel.addTask(
                Task(
                    title = title,
                    description = description,
                    date = date,
                    time = time,
                    delayTime = 15,
                    isFinished = false
                )
            )

            Toast.makeText(this,"Saved task successfully!",Toast.LENGTH_SHORT).show()

            onBackPressed()
        }

    }

    private fun enterAnimation(vararg views: View) {
        var delay = 100L
        for (view in views){
            val animation = AnimationUtils.loadAnimation(this, R.anim.enter_anim)
            delay += 100
            animation.startOffset = delay
            animation.duration = 500
            view.startAnimation(animation)
        }
    }
}