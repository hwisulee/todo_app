package com.sld.todolist.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColor
import androidx.core.view.get
import com.sld.todoyoujob.R
import com.sld.todoyoujob.databinding.ActivityEdittaskBinding

class EditTaskActivity: AppCompatActivity() {
    private val binding: ActivityEdittaskBinding by lazy { ActivityEdittaskBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        checkedThemeAndChange()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.add_task_menu, menu)

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            menu!!.findItem(R.id.save).icon = getDrawable(R.drawable.ic_baseline_save_24)
        } else {
            menu!!.findItem(R.id.save).icon = getDrawable(R.drawable.ic_baseline_save_dark_24)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.save -> {
                saveData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkedThemeAndChange() {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight)
        } else {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light)
        }
    }

    private fun initView() {
        val data = intent.getStringExtra("STATE")

        if (data == "add") {
            title = "Add Task"
        } else {
            title = "Edit Task"
            binding.editTaskTitle.setText(intent.getStringExtra("title"))
            binding.editTaskDescription.setText(intent.getStringExtra("description"))
        }
    }

    private fun saveData() {
        val title: String = binding.editTaskTitle.text.toString()
        val description: String = binding.editTaskDescription.text.toString()
        val data = Intent(this, MainActivity::class.java).apply {
            putExtra("title", title)
            putExtra("description", description)

            if (intent.getStringExtra("STATE") == "add") {
                putExtra("STATE", "add")
            } else {
                putExtra("id", intent.getIntExtra("id", 0))
                putExtra("order", intent.getIntExtra("order", 0))
                putExtra("STATE", "edit")
            }
        }

        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert title and description", Toast.LENGTH_SHORT).show()
        } else {
            setResult(RESULT_OK, data)
            finish()
        }
    }
}