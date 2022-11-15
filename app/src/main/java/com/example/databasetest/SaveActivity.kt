package com.example.databasetest

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.databasetest.customclass.DBHelper
import com.example.databasetest.customclass.ToolBarCustomView
import com.example.databasetest.customclass.ToolBarCustomViewDelegate
import com.example.databasetest.databinding.ActivitySaveBinding
import java.text.SimpleDateFormat
import java.util.*

class SaveActivity : AppCompatActivity(), ToolBarCustomViewDelegate {
    private lateinit var binding: ActivitySaveBinding
    private var dialog: AlertDialog? = null
    @SuppressLint("SimpleDateFormat")
    private val day = SimpleDateFormat("yyyy/MM/dd HH:mm")

    companion object{
        private const val TABLE_NAME="memos"
        private const val PERMISSIONS_RECORD_AUDIO = 1000000
        var text = ""
    }
    private var speechRecognizer : SpeechRecognizer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        

        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }

        setCustomToolBar()

        val helper = DBHelper(this)
        val memoId: Long = intent.getLongExtra("id",0)
        if (memoId != 0L) {
            helper.readableDatabase.use {
                    db -> db.query(TABLE_NAME, arrayOf("id", "title", "content"), "id = ?", arrayOf(memoId.toString()), null, null, null, "1")
                .use { cursor ->
                    if (cursor.moveToFirst()) {
                        binding.textTitle.setText(cursor.getString(1))
                        binding.textContent.setText(cursor.getString(2))

                    }
                }
            }
        }

    }

    private fun createRecognitionListenerStringStream(onResult : (String)-> Unit) : RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) {  }
            override fun onReadyForSpeech(params: Bundle) {}
            override fun onBufferReceived(buffer: ByteArray) {}
            override fun onPartialResults(partialResults: Bundle) {
                val stringArray = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                onResult(binding.textContent.text.toString() + stringArray.toString().substring(1, stringArray.toString().length - 1))
            }
            override fun onEvent(eventType: Int, params: Bundle) { onResult("onEvent") }
            override fun onBeginningOfSpeech() {
                onResult(binding.textContent.text.toString())
            }
            override fun onEndOfSpeech() { }
            override fun onError(error: Int) {
                Toast.makeText(applicationContext, "エラー", Toast.LENGTH_LONG).show()
                onResult(binding.textContent.text.toString())
            }
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                onResult(binding.textContent.text.toString() + stringArray.toString().substring(1, stringArray.toString().length - 1))
            }
        }
    }

    private fun setCustomToolBar() {
        val toolBarCustomView = ToolBarCustomView(this)
        toolBarCustomView.delegate = this

        val title = "アプリ"
        toolBarCustomView.configure(title, false, false, R.drawable.ic_baseline_save_alt_24)

        // カスタムツールバーを挿入するコンテナ(入れ物)を指定
        val layout: LinearLayout = binding.containerForSecondToolbar
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
        val editText = AppCompatEditText(this)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream {  editText.setText(it) })
        speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
        if(dialog == null) {
            dialog = AlertDialog.Builder(this)
                .setTitle("入力中...")
                .setView(editText)
                .setPositiveButton("保存") { dialog, _ ->
                    text = editText.text.toString()
                    binding.textContent.setText(text)
                    dialog.dismiss()
                    speechRecognizer?.stopListening()
                }
                .setNegativeButton("キャンセル") { dialog, _ ->
                    speechRecognizer?.stopListening()
                    speechRecognizer?.cancel()
                    dialog.dismiss()
                }
                .create()
        }
        dialog?.show()
    }


    override fun onClickedSecondRightButton() {
        val helper = DBHelper(this)
        val memoId: Long = intent.getLongExtra("id",0)
        helper.writableDatabase.use {
                db ->
            val values = ContentValues().apply {
                put("title", binding.textTitle.text.toString())
                put("Content", binding.textContent.text.toString())
                put("day", day.format(Date()))
            }
            if (memoId != 0L) {
                db.update(TABLE_NAME, values,"id = ?", arrayOf(memoId.toString()))
            } else {
                db.insert(TABLE_NAME,null, values)
            }
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        speechRecognizer?.cancel()

    }
}