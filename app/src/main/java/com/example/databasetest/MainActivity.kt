package com.example.databasetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.databasetest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListViewAdapter(binding.listView)

        binding.add.setOnClickListener {
            val intent = Intent(this, MemoActivity::class.java)
            startActivity(intent)
        }

        binding.listView.setOnItemClickListener{ av, view, position, id ->
            val intent = Intent(this, MemoActivity::class.java)
            val itemId = binding.listView.adapter.getItemId(position)
            intent.putExtra("id", itemId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val helper = DBHelper(this)
        setListViewAdapter(binding.listView)
    }

    fun setListViewAdapter(listView: ListView) {
        val helper = DBHelper(this)
        helper.readableDatabase.use {
            db -> db.query("memos", arrayOf("id", "title", "content"), null,null,null,null,null,null)
            .use { cursor ->
               val memoList = mutableListOf<ListItem>()
                if(cursor.moveToFirst()) {
                    for (i in 1..cursor.count) {
                        val memoId = cursor.getInt(0)
                        val title = cursor.getString(1)
                        memoList.add(ListItem(memoId.toLong(), title))
                        cursor.moveToNext()
                    }
                }
                listView.adapter = CustomListAdapter(this, memoList, R.layout.list_item)
            }
        }
    }
}