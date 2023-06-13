package com.kyawzinlinn.taskreminder.animator

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class CustomAnimator : DefaultItemAnimator() {

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        // Apply your custom animation for item appearance
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(400).start()
        dispatchAddFinished(holder)
        return true
    }
}

