package com.sld.todolist.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sld.todolist.adapter.AppAdapter
import com.sld.todolist.db.AppTable
import com.sld.todolist.db.AppViewModel
import com.sld.todoyoujob.databinding.ActivityMainBinding
import java.util.*

class MainActivity() : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var appViewModel: AppViewModel
    private lateinit var getResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        var order: Int? = null
        var data1 = AppTable()
        var data2 = AppTable()

        appViewModel = ViewModelProvider(this@MainActivity)[AppViewModel::class.java]
        appViewModel.getAll().observe(this@MainActivity) { data ->
            val adapter = AppAdapter()
            val layoutManager = LinearLayoutManager(applicationContext)
            val itemTouchHelperCallback =
                object: ItemTouchHelper.Callback() {
                    override fun getMovementFlags(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ): Int {
                        return if (recyclerView.layoutManager is GridLayoutManager) {
                            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                            val swipeFlags = 0
                            makeMovementFlags(dragFlags, swipeFlags)
                        } else {
                            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                            makeMovementFlags(dragFlags, swipeFlags)
                        }
                    }

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val fromPosition = viewHolder.adapterPosition
                        val toPosition = target.adapterPosition

                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                Collections.swap(data, i, i + 1)
                                var order1: Int = data[i].order!!
                                var order2: Int = data[i + 1].order!!

                                data1 = AppTable(data[i].id, order2, data[i].title, data[i].description)
                                data2 = AppTable(data[i + 1].id, order1, data[i + 1].title, data[i + 1].description)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                Collections.swap(data, i, i - 1)
                                var order1: Int = data[i].order!!
                                var order2: Int = data[i - 1].order!!

                                data1 = AppTable(data[i].id, order2, data[i].title, data[i].description)
                                data2 = AppTable(data[i - 1].id, order1, data[i - 1].title, data[i - 1].description)
                            }
                        }
                        adapter.notifyItemMoved(fromPosition, toPosition)
                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        appViewModel.delete(adapter.getDataAt(viewHolder.adapterPosition))
                    }

                    override fun isLongPressDragEnabled(): Boolean = false

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        appViewModel.update(data1)
                        appViewModel.update(data2)
                        adapter.submitList(data)
                    }
                }
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(binding.recyclerview)

            with(binding) {
                // ItemClickListener
                adapter.onItemClick = {
                    val data = Intent(this@MainActivity, EditTaskActivity::class.java).apply {
                        putExtra("STATE", "edit")
                        putExtra("id", it.id)
                        putExtra("order", it.order)
                        putExtra("title", it.title)
                        putExtra("description", it.description)
                    }
                    getResult.launch(data)
                }

                // ItemLongClickListener
                adapter.onItemLongClick = {
                    adapter.isLongClicked = true
                    bottomNavigationView(adapter, "show")
                    adapter.notifyDataSetChanged()
                }

                // CheckedBox ButtonClickListener
                adapter.onItemChecked = {
                    bottomNavigationView(adapter, "show")
                }

                // Cancel ButtonListener
                buttonCancelTodo.setOnClickListener {
                    adapter.selectedList.clear()
                    adapter.isLongClicked = false
                    bottomNavigationView(adapter, "hide")
                    adapter.notifyDataSetChanged()
                }

                // Delete ButtonListener
                buttonDeleteTodo.setOnClickListener {
                    for (i in adapter.selectedList.indices) {
                        appViewModel.delete(adapter.selectedList[i])
                    }
                    bottomNavigationView(adapter, "hide")
                }

                // Floating Add ButtonListener
                buttonAddTodo.setOnClickListener {
                    val data = Intent(this@MainActivity, EditTaskActivity::class.java).apply {
                        putExtra("STATE", "add")
                    }
                    getResult.launch(data)
                }

                // RecyclerView Setting
                recyclerview.apply {
                    this.adapter = adapter
                    this.layoutManager = layoutManager
                    setHasFixedSize(true)

                    adapter.onStartDrag(object: AppAdapter.OnItemTouchListener {
                        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                            itemTouchHelper.startDrag(viewHolder)
                        }
                    })
                }
            }

            try {
                order = data.maxOfOrNull { it.order!! }
                if (order == null) order = 0
            } catch (_: Exception) { }

            adapter.submitList(data)
        }

        // Activity to Activity Data Communication
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val state = it.data?.getStringExtra("STATE")
                val title = it.data?.getStringExtra("title")
                val description = it.data?.getStringExtra("description")

                if (state == "add") {
                    val data = AppTable(null, order!! + 1, title, description)
                    appViewModel.insert(data)
                }
                else {
                    val id = it.data?.getIntExtra("id", 0)
                    val order = it.data?.getIntExtra("order", 0)
                    val data = AppTable(id, order, title, description)
                    appViewModel.update(data)
                }
            }
        }
    }

    private fun bottomNavigationView(adapter: AppAdapter, case: String) {
        with(binding) {
            when(case) {
                "show" -> {
                    mainTitle.isVisible = false
                    buttonAddTodo.isVisible = false
                    viewBottomBg.isVisible = true
                    textSelectedItems.text = "Selected ${adapter.selectedList.size} items"
                }
                "hide" -> {
                    mainTitle.isVisible = true
                    buttonAddTodo.isVisible = true
                    viewBottomBg.isVisible = false
                }
            }
        }
    }
}