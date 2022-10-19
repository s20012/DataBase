package com.example.databasetest

import android.Manifest.permission.RECORD_AUDIO
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.databasetest.databinding.ActivityMemoBinding


class MemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoBinding
    companion object{
        private const val TABLE_NAME="memos"
        private const val PERMISSIONS_RECORD_AUDIO = 10000
    }
    private var speechRecognizer : SpeechRecognizer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val helper = DBHelper(this)

        val granted = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
        }
        val editText = AppCompatEditText(this)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream {  editText.setText(it) })



        binding.daialog.setOnClickListener {
                speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
                AlertDialog.Builder(this)
                    .setTitle("入力中...")
                    .setView(editText)
                    .setPositiveButton("保存") { dialog, _ ->
                        speechRecognizer?.stopListening()
                        binding.textContent.text = editText.text
                        dialog.dismiss()
                    }
                    .create()
                    .show()


        }


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

        binding.saveButton.setOnClickListener{
            helper.writableDatabase.use {
                    db ->
                val values = ContentValues().apply {
                    put("title", binding.textTitle.text.toString())
                    put("Content", binding.textContent.text.toString())
                }
                if (memoId != 0L) {
                    db.update(TABLE_NAME, values,"id = ?", arrayOf(memoId.toString()))
                } else {
                    db.insert(TABLE_NAME,null, values)
                }
            }
            finish()
        }
        binding.deleteButton.setOnClickListener {
            helper.writableDatabase.use {
                    db ->
                db.delete(TABLE_NAME, "id = ?", arrayOf(memoId.toString()))
                Toast.makeText(this, "削除しました", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun createRecognitionListenerStringStream(onResult : (String)-> Unit) : RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) {  }
            override fun onReadyForSpeech(params: Bundle) {  }
            override fun onBufferReceived(buffer: ByteArray) { onResult("onBufferReceived") }
            override fun onPartialResults(partialResults: Bundle) { onResult("onPartialResults") }
            override fun onEvent(eventType: Int, params: Bundle) { onResult("onEvent") }
            override fun onBeginningOfSpeech() {  }
            override fun onEndOfSpeech() { }
            override fun onError(error: Int) { onResult("もう一度お願いします") }
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                onResult(stringArray.toString().substring(1, stringArray.toString().length - 1))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
    }

}