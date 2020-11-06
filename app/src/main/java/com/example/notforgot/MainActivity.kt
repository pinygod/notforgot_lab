package com.example.notforgot

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notforgot.adapters.MainRecyclerAdapter
import com.example.notforgot.models.CategoryWithItems
import com.example.notforgot.models.Note
import com.example.notforgot.models.RecyclerObject
import com.example.notforgot.models.User
import com.example.notforgot.room.AppDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MainRecyclerAdapter.ItemListener {
    private lateinit var recyclerAdapter: MainRecyclerAdapter
    private lateinit var listCategories: ArrayList<CategoryWithItems>
    private lateinit var recyclerObjectsList: ArrayList<RecyclerObject>
    private var isActivityCreated = false

    companion object {
        private lateinit var user: User
        fun getUser(): User {
            return user
        }
    }

    override fun onNoteStateChangedToFalse(item: Note, position: Int) {
        item.checkBoxCondition = !item.checkBoxCondition
        AppDatabase.get(application).getItemDao().insertItem(item)
        getCategories()
        recyclerAdapter.notifyDataSetChanged()
        changeVisibilities()
    }

    override fun onNoteClicked(item: Note, position: Int) {
        val myIntent = Intent(this, NoteDetailsActivity::class.java)
        myIntent.putExtra("Note", item)
        startActivity(myIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (intent.hasExtra("User")) {
            user = intent.getSerializableExtra("User") as User
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        getCategories()
        changeVisibilities()
        recyclerAdapter = MainRecyclerAdapter(
            recyclerObjectsList,
            this,
            this
        )
        recyclerView.adapter = recyclerAdapter
        addSwipeGestures()
        isActivityCreated = true
        addNoteButton.setOnClickListener {
            val myIntent = Intent(this, CreateNoteActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        PreferenceUtils.deleteEmail(this)
        PreferenceUtils.deletePassword(this)
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
        return true
    }

    private fun addSwipeGestures() {
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    if (recyclerObjectsList[position].type == Constants.TYPE_NOTE) {
                        AppDatabase.get(application).getItemDao()
                            .deleteItem(recyclerObjectsList[position].item as Note)
                        if (recyclerObjectsList.size > position + 1) {
                            if (recyclerObjectsList[position - 1].type == Constants.TYPE_TITLE && recyclerObjectsList[position + 1].type == Constants.TYPE_TITLE) {
                                recyclerObjectsList.removeAt(viewHolder.adapterPosition)
                                recyclerObjectsList.removeAt(position - 1)
                                changeVisibilities()
                            } else {
                                recyclerObjectsList.removeAt(viewHolder.adapterPosition)
                            }
                        } else {
                            if (recyclerObjectsList[position - 1].type == Constants.TYPE_TITLE) {
                                recyclerObjectsList.removeAt(viewHolder.adapterPosition)
                                recyclerObjectsList.removeAt(position - 1)
                                changeVisibilities()
                            } else {
                                recyclerObjectsList.removeAt(viewHolder.adapterPosition)
                            }
                        }

                        recyclerAdapter.notifyDataSetChanged()
                    }
                }

            }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        if (isActivityCreated) {
            getCategories()
            recyclerAdapter.updateList(recyclerObjectsList)
            changeVisibilities()
        }
    }

    private fun changeVisibilities() {
        if (recyclerObjectsList.isNotEmpty()) {
            emptyImage.visibility = View.INVISIBLE
            emptyText1.visibility = View.INVISIBLE
            emptyText2.visibility = View.INVISIBLE
            recyclerView.visibility = View.VISIBLE
        } else {
            emptyImage.visibility = View.VISIBLE
            emptyText1.visibility = View.VISIBLE
            emptyText2.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
        }
    }

    private fun getCategories() {
        listCategories = AppDatabase.get(application).getCategoryDao()
            .getCategoryWithItems(user.userId) as ArrayList<CategoryWithItems>
        recyclerObjectsList = ArrayList<RecyclerObject>()
        listCategories.forEach {
            if (!it.items.isEmpty()) {
                recyclerObjectsList.add(RecyclerObject(Constants.TYPE_TITLE, it.category))
                it.items.forEach {
                    recyclerObjectsList.add((RecyclerObject(Constants.TYPE_NOTE, it)))
                }
            }
        }
    }

}