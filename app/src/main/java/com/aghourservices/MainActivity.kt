package com.aghourservices

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import org.json.JSONArray


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = GridLayoutManager(this, 2)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..5) {
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
            data.add(ItemsViewModel(R.drawable.messi, "Item $i"))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(data)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

    }
    private fun makeRequest() {
        AndroidNetworking.get("https://jsonplaceholder.typicode.com/todos/")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    Log.v("Response", response.toString());
                }

                override fun onError(error: ANError?) {
                    // handle error
                    Log.v("Response", error.toString());
                }
            })
    }
}