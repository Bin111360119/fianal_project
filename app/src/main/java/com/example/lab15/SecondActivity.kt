package com.example.lab15

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // 初始化資料庫
        dbrw = MyDBHelper(this)

        // 獲取從 MainActivity 傳遞過來的數據
        items = intent.getStringArrayListExtra("ITEMS") ?: ArrayList()

        val listView = findViewById<ListView>(R.id.listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items)
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        // 刪除選擇項目按鈕邏輯
        findViewById<Button>(R.id.btnDeleteSelected).setOnClickListener {
            val position = listView.checkedItemPosition
            if (position != ListView.INVALID_POSITION) {
                val itemToRemove = items[position]
                val bookTitle = itemToRemove.substringAfter("大綱:").substringBefore("\t\t\t\t")
                try {
                    dbrw.writableDatabase.execSQL("DELETE FROM myTable WHERE book = ?", arrayOf(bookTitle))
                    items.removeAt(position) // 從內存中刪除
                    adapter.notifyDataSetChanged() // 更新適配器
                    listView.clearChoices() // 清除選擇狀態
                    showToast("已刪除項目: $bookTitle")
                } catch (e: Exception) {
                    showToast("刪除失敗: ${e.localizedMessage}")
                }
            } else {
                showToast("請先選擇要刪除的項目")
            }
        }

        // 返回按鈕邏輯，將結果返回給 MainActivity
        findViewById<Button>(R.id.btnback).setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putStringArrayListExtra("UPDATED_ITEMS", items)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}