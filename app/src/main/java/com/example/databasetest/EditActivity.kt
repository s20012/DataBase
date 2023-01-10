package com.example.databasetest

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.databasetest.customclass.DBHelper
import com.example.databasetest.customclass.ToolBarCustomView
import com.example.databasetest.customclass.ToolBarCustomViewDelegate
import com.example.databasetest.databinding.ActivityMemoBinding
import com.example.databasetest.util.MD5Util
import com.example.databasetest.util.OkHttpUtil
import org.json.JSONObject

@SuppressLint("SimpleDateFormat")
class EditActivity : AppCompatActivity(), ToolBarCustomViewDelegate {
    private lateinit var binding: ActivityMemoBinding
    private var memoId: Long = 0

    private var helper = DBHelper(this)

    companion object{
        private const val TABLE_NAME="memos"
        private const val appId = "20180822000197406"
        private const val pwdKey = "【自分のAPIキー】"
        private const val baiDuTranslateUrl = "https://fanyi-api.baidu.com/api/trans/vip/translate"
        private const val salt = 1435660288
    }
    //网络请求框架
    private var net: OkHttpUtil = OkHttpUtil.instance!!

    //映射值，用来将内容转换为百度需要参数
    private var map = mapOf("Chinese" to "zh", "English" to "en", "Japanese" to "jp")

    //下拉框显示值
    private var array = arrayOf("Chinese", "English", "Japanese")

    //剪切板对象
    private lateinit var clipboardmanager: ClipboardManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        memoId = intent.getLongExtra("id", 0)
        if (memoId != 0L) {
            helper.readableDatabase.use {
                    db -> db.query(TABLE_NAME, arrayOf("id", "title", "content"), "id = ?", arrayOf(memoId.toString()), null, null, null, "1")
                .use { cursor ->
                    if (cursor.moveToFirst()) {
                        binding.textTitle.text = cursor.getString(1)
                        binding.textContent.text = cursor.getString(2)

                    }
                }
            }
        }
        clipboardmanager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        initSpiner(binding.from, 0)
        initSpiner(binding.to, 1)

        binding.translate.setOnClickListener {
            val from = map[binding.from.selectedItem.toString()].toString()

            val to = map[binding.to.selectedItem.toString()].toString()

            doQuery(from, binding.textContent.text.toString(), to)
        }


        if(!binding.textContent.text.toString().equals("")) binding.lotteAnimation.visibility = View.INVISIBLE

        setCustomToolBar()

    }

    override fun onResume() {
        super.onResume()
        DBHelper(this)
    }

    override fun onRestart() {
        super.onRestart()
        val intent = intent
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    private fun initSpiner(sp: Spinner, select: Int) {
        val starAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, array)
        sp.prompt = "Chinese"
        //设置下拉框的数组适配器
        sp.adapter = starAdapter
        //设置下拉框默认的显示第一项
        sp.setSelection(select)
    }

    private fun doQuery(
        from: String,
        textContent: String,
        to: String,
    ) {
        //百度api需要请求参数加密
        val sign = MD5Util.encrypt(appId + textContent + salt + pwdKey)
        //百度api拼接get请求
        val endUrl =
            "$baiDuTranslateUrl?q=$textContent&from=$from&to=$to&appid=$appId&salt=$salt&sign=$sign"
        //请求百度api
        net.httpGet(endUrl,
            object :
                OkHttpUtil.ICallback {
                override fun invoke(string: String?) {
                    //解析返回结果
                    val obj = JSONObject(string!!)
                    if (obj.has("error_code")) {
                        val errorCode = obj.getInt("error_code")
                        val errorMsg = obj.getString("error_msg")
                        if (0 != errorCode) {
                            //返回错误的结果处理
                            runOnUiThread {
                                Toast.makeText(
                                    this@EditActivity,
                                    errorMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return
                        }
                    }
                    //提取翻译后内容
                    val trans_result = obj.getJSONArray("trans_result").get(0) as JSONObject
                    val translateResult = trans_result.getString("dst")
                    runOnUiThread {
                        //显示翻译结果
                        binding.textContent.text = ""
                        binding.textContent.text = translateResult

                    }
                }
            })
    }

    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = "Speak Record"
        toolBarCustomView.configure(title, false, R.drawable.ic_baseline_edit_24,R.drawable.ic_baseline_delete_24)

        // カスタムツールバーを挿入するコンテナ(入れ物)を指定
        val layout: LinearLayout = binding.containerForMainToolbar
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

        val intent = Intent(this, SaveActivity::class.java)
        intent.putExtra("id", memoId)
        startActivity(intent)
    }


    override fun onClickedSecondRightButton() {
        helper.writableDatabase.use {
                db ->
            db.delete(TABLE_NAME, "id = ?", arrayOf(memoId.toString()))
            Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

}