package com.kyawzinlinn.taskreminder.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.LayoutTransition
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyawzinlinn.taskreminder.App
import com.kyawzinlinn.taskreminder.R
import com.kyawzinlinn.taskreminder.adapters.TaskWithDateItemAdapter
import com.kyawzinlinn.taskreminder.database.Task
import com.kyawzinlinn.taskreminder.database.format
import com.kyawzinlinn.taskreminder.databinding.ActivityMainBinding
import com.kyawzinlinn.taskreminder.util.showDeleteTaskDialog
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModel
import com.kyawzinlinn.taskreminder.viewmodel.ToDoViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ToDoViewModelFactory((application as App).repository)).get(ToDoViewModel::class.java)
        setContentView(binding.root)

        binding.mainContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        setUpToDoListRv()
        setUpClickListeners()

    }

    private fun setUpToDoListRv(){
        viewModel.taskList.observe(this){
            with(binding.rvToDoWithDay){
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
                            toDo.delayTime,
                            isChecked
                        )
                    )
                }, onLonClicked = {toDo ->
                    showDeleteTaskDialog(this@MainActivity){
                        viewModel.deleteTask(toDo)
                    }
                })
                setAdapter(adapter)
                adapter.submitList(it.format())
            }
        }
    }

    private fun setUpClickListeners(){
        with(binding){
            floatingActionButton.setOnClickListener {
                revealAnimation {
                    Handler().postDelayed(Runnable {
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity)
                        val intent = Intent(this@MainActivity, AddTaskActivity::class.java)
                        startActivity(intent,options.toBundle())
                    },0)
                }
            }
        }
    }

    private fun revealAnimation(onFinished: () -> Unit) {
        binding.reveal.visibility = View.VISIBLE
        window.statusBarColor = resources.getColor(R.color.primary_color)

        val cx = binding.reveal.width
        val cy = binding.reveal.height
        val x = (binding.reveal.right)
        val y = (binding.reveal.bottom)

        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val revealAnim =
            ViewAnimationUtils.createCircularReveal(binding.reveal, x, y, 56f, finalRadius)
        revealAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                onFinished()

                binding.reveal.visibility = View.INVISIBLE
                window.statusBarColor = Color.TRANSPARENT
            }
        })
        revealAnim.duration = 400
        revealAnim.start()
    }
}