package com.kyawzinlinn.taskreminder.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.kyawzinlinn.taskreminder.App
import com.kyawzinlinn.taskreminder.R
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.databinding.ActivityAddTaskBinding
import com.kyawzinlinn.taskreminder.service.TaskReminderService
import com.kyawzinlinn.taskreminder.util.TAG
import com.kyawzinlinn.taskreminder.util.TASK_INTENT_EXTRA
import com.kyawzinlinn.taskreminder.util.WorkerUtils.scheduleReminder
import com.kyawzinlinn.taskreminder.util.formatHour
import com.kyawzinlinn.taskreminder.util.showDatePicker
import com.kyawzinlinn.taskreminder.util.showTimePicker
import com.kyawzinlinn.taskreminder.viewmodel.OperationStatus
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModel
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModelFactory
import java.time.LocalDate
import java.time.LocalTime

const val DELAY_MINUTE = 15

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var viewModel: ToDoViewModel
    private var date = LocalDate.now().toString()
    private var time = formatHour(LocalTime.now().hour, LocalTime.now().minute)
    private var delayTime = DELAY_MINUTE
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        //setUpTransition()

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ToDoViewModelFactory((application as App).repository)).get(ToDoViewModel::class.java)

        setContentView(binding.root)

        try {
            task = intent?.extras?.getSerializable(TASK_INTENT_EXTRA) as Task
        }catch (e: Exception){}

        bindUI()

        setUpClickListeners()
    }

    private fun bindUI(){
        with(binding){
            if (task != null){
                tvDate.text = task?.date
                tvTime.text = task?.time
                tvTaskTitle.text = "Update Task"
                edTaskTitle.setText(task?.title)
                edTaskDescription.setText(task?.description)
            }else{
                tvDate.text = date
                tvTime.text = time
                tvTaskTitle.text = "Add New Task"
            }
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

        if (title.isEmpty()) binding.edTaskTitle.error = "Title must not be empty!"
        if (description.isEmpty()) binding.edTaskDescription.error = "Description must not be empty!"

        if(title.isNotEmpty() && description.isNotEmpty()){

            viewModel.apply {
                if(task != null) {
                    val tempTask = Task(
                        id = task?.id ?: 0,
                        title = title,
                        description = description,
                        date = date,
                        time = time,
                        delayTime = 15,
                        isCompleted = task?.isCompleted ?: false
                    )
                    updateToDo(tempTask)
                    scheduleTask(tempTask)
                    Toast.makeText(this@AddTaskActivity,"Updated successfully!",Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                else {
                    val tempTask = Task(
                        title = title,
                        description = description,
                        date = date,
                        time = time,
                        delayTime = 15,
                        isCompleted = false
                    )
                    addTask(tempTask)
                    scheduleTask(tempTask)
                }
            }
        }

    }

    private fun scheduleTask(task: Task){
        viewModel.apply {
            status.observe(this@AddTaskActivity){status ->
                when(status){
                    OperationStatus.LOADING -> Log.d(TAG, "saveTask: loading")
                    OperationStatus.DONE -> {
                        getTask(task.title,task.description).observe(this@AddTaskActivity){
                            val intent = Intent(this@AddTaskActivity, TaskReminderService::class.java)
                            intent.putExtra(TASK_INTENT_EXTRA,it?.get(0))
                            startService(intent)
                        }

                        Toast.makeText(this@AddTaskActivity,"Saved task successfully!",Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                    OperationStatus.ERROR -> Log.d(TAG, "saveTask: error")
                }
            }
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