package com.example.lab15

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 取得資料庫實體
        dbrw = MyDBHelper(this).writableDatabase
        // 宣告Adapter並連結ListView
        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, items)
        //findViewById<ListView>(R.id.listView).adapter = adapter
        // 設定監聽器
        setListener()

        // 新增按鈕：跳轉到第二個介面並傳遞數據
        findViewById<Button>(R.id.btnSendToSecondActivity).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putStringArrayListExtra("ITEMS", items)
            startActivity(intent)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close() // 關閉資料庫
    }

    // 設定監聽器
    private fun setListener() {
        val edBook = findViewById<EditText>(R.id.edBook)
        val edPrice = findViewById<EditText>(R.id.editTextMultiLine)

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            if (edBook.length() < 1 || edPrice.length() < 1)
                showToast("欄位請勿留空")
            else
                try {
                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?,?)",
                        arrayOf(edBook.text.toString(), edPrice.text.toString())
                    )
                    showToast("新增:${edBook.text},內容:${edPrice.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("新增失敗:$e")
                }
        }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            // 判斷是否有填入書名或價格
            if (edBook.length() < 1 || edPrice.length() < 1)
                showToast("欄位請勿留空")
            else
                try {
                    // 尋找相同書名的紀錄並更新price欄位的值
                    val sql = "UPDATE myTable SET price = ? WHERE book = ?"
                    dbrw.execSQL(sql, arrayOf(edPrice.text.toString(), edBook.text.toString()))
                    //dbrw.execSQL("UPDATE myTable SET price = ${edPrice.text} WHERE book LIKE '${edBook.text}'")
                    showToast("更新:${edBook.text},價格:${edPrice.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("更新失敗:$e")
                }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            // 判斷是否有填入書名
            if (edBook.length() < 1)
                showToast("書名請勿留空")
            else
                try {
                    // 從myTable資料表刪除相同書名的紀錄
                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE '${edBook.text}'")
                    showToast("刪除:${edBook.text}")
                    cleanEditText()
                } catch (e: Exception) {
                    showToast("刪除失敗:$e")
                }
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            val queryString = if (edBook.length() < 1)
                "SELECT * FROM myTable"
            else
                "SELECT * FROM myTable WHERE book LIKE '${edBook.text}'"

            val c = dbrw.rawQuery(queryString, null)
            c.moveToFirst()
            items.clear()
            showToast("共有${c.count}筆資料")
            for (i in 0 until c.count) {
                items.add("大綱:${c.getString(0)}\t\t\t\t細節:${c.getString(1)}")
                c.moveToNext()
            }
            adapter.notifyDataSetChanged()


                // 獲取輸入的書名和內容
                val book = edBook.text.toString()
                val price = edPrice.text.toString()

                // 將數據構建為一個字符串並添加到數據列表
                //items.add("書名: $book, 價格: $price")

                // 創建 Intent 並傳遞數據列表
                val intent = Intent(this, SecondActivity::class.java)
                intent.putStringArrayListExtra("ITEMS", items)
                startActivity(intent)

                // 清空輸入框
                edBook.setText("")
                edPrice.setText("")
            c.close()
        }
        findViewById<Button>(R.id.btnSendToSecondActivity).setOnClickListener {


        }

    }

    private fun showToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    private fun cleanEditText() {
        findViewById<EditText>(R.id.edBook).setText("")
        findViewById<EditText>(R.id.editTextMultiLine).setText("")
    }
}
