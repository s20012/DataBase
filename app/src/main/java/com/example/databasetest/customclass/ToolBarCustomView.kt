package com.example.databasetest.customclass

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.databasetest.R

interface ToolBarCustomViewDelegate {
    fun onClickedLeftButton()
    fun onClickedFirstRightButton()
    fun onClickedSecondRightButton()
}

class ToolBarCustomView : LinearLayout {
    var delegate: ToolBarCustomViewDelegate? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        LayoutInflater.from(context).inflate(R.layout.custom_tool_bar, this, true)
    }

    // ツールバーに表示する文字や、ボタンの表示/非表示の切り替えを設定する
    fun configure(titleText: String, isHideLeftButton: Boolean, isHideFirstRightButton: Boolean, isHideSecondRightButton: Int) {
        //　カスタムツールバーのImageButtonとTextViewを取得する
        val titleTextView: TextView = findViewById(R.id.text_title)
        val leftButton: ImageButton = findViewById(R.id.btn_left)
        val rightFirstButton: ImageButton = findViewById(R.id.btn_first_right)
        val rightSecondButton: ImageButton = findViewById(R.id.btn_second_right)


        // TextViewに文字を設定g
        // ImageViewの表示/非表示を切り替える
        titleTextView.text = titleText
        leftButton.visibility = if (isHideLeftButton) View.INVISIBLE else View.VISIBLE
        rightFirstButton.visibility = if (isHideFirstRightButton) View.INVISIBLE else View.VISIBLE
        rightSecondButton.setImageResource(isHideSecondRightButton)

        // ボタンがクリックされたときのリスナーを設定
        // 実際の処理は画面ごとのActivityで設定
        leftButton.setOnClickListener {
            delegate?.onClickedLeftButton()
        }
        rightFirstButton.setOnClickListener {
            delegate?.onClickedFirstRightButton()
        }
        rightSecondButton.setOnClickListener {
            delegate?.onClickedSecondRightButton()
        }
    }
}