package com.example.realm_retrofit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.kotlin.where
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var realm: Realm
    private lateinit var adapter: TodoAdapter
    private lateinit var todoRecyclerView: RecyclerView
    lateinit var addTodoFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        setupRecyclerView()
        fetchDataFromApi()

        addTodoFab.setOnClickListener {
            addTodo()
        }
    }

    private fun setupRecyclerView() {
        val todos = realm.where<TodoItem>().findAll()
        adapter = TodoAdapter(todos) { todo -> editOrDeleteTodo(todo) }
        todoRecyclerView.layoutManager = LinearLayoutManager(this)
        todoRecyclerView.adapter = adapter
    }

    private fun addTodo() {
        realm.executeTransaction {

            val todo = it.createObject(TodoItem::class.java)
            todo.title = "New Task"
            todo.description = "Task Description"
        }
        adapter.notifyDataSetChanged()
    }

    private fun editOrDeleteTodo(todo: TodoItem) {
        // Implement edit or delete logic here
    }

    private fun fetchDataFromApi() {
        ApiClient.instance.getTodos().enqueue(object : Callback<List<TodoItem>> {
            override fun onResponse(
                call: Call<List<TodoItem>>,
                response: Response<List<TodoItem>>
            ) {
                if (response.isSuccessful) {
                    val todos = response.body()
                    realm.executeTransaction {
                        it.insertOrUpdate(todos)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<TodoItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

