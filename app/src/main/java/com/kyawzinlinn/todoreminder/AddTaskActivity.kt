package com.kyawzinlinn.todoreminder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kyawzinlinn.todoreminder.databinding.ActivityAddTaskBinding

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}