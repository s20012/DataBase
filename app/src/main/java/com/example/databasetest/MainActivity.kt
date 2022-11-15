package com.example.databasetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import com.example.databasetest.customclass.*
import com.example.databasetest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ToolBarCustomViewDelegate {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListViewAdapter(binding.listView)

        // デフォルトのアクションバーを非表示にする
        supportActionBar?.hide()

        setCustomToolBar()

        binding.listView.setOnItemClickListener{ _, _, position, _ ->
            val intent = Intent(this, EditActivity::class.java)
            val itemId = binding.listView.adapter.getItemId(position)
            intent.putExtra("id", itemId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        DBHelper(this)
        setListViewAdapter(binding.listView)
    }

    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = "アプリ"
        toolBarCustomView.configure(title, true, true, R.drawable.ic_baseline_add_24)

        // カスタムツールバーを挿入するコンテナ(入れ物)を指定
        val layout: LinearLayout = binding.containerForToolbar
        // ツールバーの表示をコンテナに合わせる
        toolBarCustomView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // カスタムツールバーを表示する
        layout.addView(toolBarCustomView)
    }

    override fun onClickedLeftButton() {
        // 前の画面に戻る
        finish()
    }

    override fun onClickedFirstRightButton() {
        TODO("Not yet implemented")
    }


    override fun onClickedSecondRightButton() {
        val intent = Intent(this, SaveActivity::class.java)
        startActivity(intent)
    }

    private fun setListViewAdapter(listView: ListView) {
        val helper = DBHelper(this)
        helper.readableDatabase.use {
            db -> db.query("memos", arrayOf("id", "title", "content", "day"), null,null,null,null,null,null)
            .use { cursor ->
               val memoList = mutableListOf<ListItem>()
                if(cursor.moveToFirst()) {
                    for (i in 1..cursor.count) {
                        val memoId = cursor.getInt(0)
                        val title = cursor.getString(1)
                        val day = cursor.getString(3)
                        memoList.add(ListItem(memoId.toLong(), title, day))
                        cursor.moveToNext()
                    }
                }
                listView.adapter = CustomListAdapter(this, memoList, R.layout.list_item)
            }
        }
    }
}