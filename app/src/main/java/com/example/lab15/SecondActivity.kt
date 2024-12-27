package com.example.lab15

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // 獲取傳遞過來的數據
        val items = intent.getStringArrayListExtra("ITEMS") ?: ArrayList()

        // 將數據顯示在 ListView 中
        val listView = findViewById<ListView>(R.id.mainscreen)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
    }
}
