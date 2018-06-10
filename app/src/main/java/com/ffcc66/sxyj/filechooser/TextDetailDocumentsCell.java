/*
 * This is the source code of Telegram for Android v. 1.7.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package com.ffcc66.sxyj.filechooser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ffcc66.sxyj.util.AndroidUtilities;

public class TextDetailDocumentsCell extends FrameLayout {

    private TextView textView;   //文件名或文件夹的名称
    private TextView valueTextView; //“文件夹”或文件的大小
    private TextView typeTextView;  //文件类型
    private TextView storageTextView;   //“已导入”三个字的textview
    private ImageView imageView;
    private CheckBox checkBox;

    public TextDetailDocumentsCell(Context context) {
        super(context);

        textView = new TextView(context);
        textView.setTextColor(0xff212121);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT);
        addView(textView);
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = AndroidUtilities.dp(10);
        layoutParams.leftMargin = AndroidUtilities.dp(71);
        layoutParams.rightMargin = AndroidUtilities.dp(16);
        layoutParams.gravity = Gravity.LEFT;
        textView.setLayoutParams(layoutParams);

        valueTextView = new TextView(context);
        valueTextView.setTextColor(0xff8a8a8a);
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setGravity(Gravity.LEFT);
        addView(valueTextView);
        layoutParams = (LayoutParams) valueTextView.getLayoutParams();
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = AndroidUtilities.dp(35);
        layoutParams.leftMargin = AndroidUtilities.dp(71);
        layoutParams.rightMargin = AndroidUtilities.dp(16);
        layoutParams.gravity = Gravity.LEFT;
        valueTextView.setLayoutParams(layoutParams);

        typeTextView = new TextView(context);
        typeTextView.setBackgroundColor(0xff757575);
        typeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        typeTextView.setGravity(Gravity.CENTER);
        typeTextView.setSingleLine(true);
        typeTextView.setTextColor(0xffd1d1d1);
        typeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        typeTextView.setTypeface(Typeface.DEFAULT_BOLD);
        addView(typeTextView);
        layoutParams = (LayoutParams) typeTextView.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(40);
        layoutParams.height = AndroidUtilities.dp(40);
        layoutParams.leftMargin = AndroidUtilities.dp(16);
        layoutParams.rightMargin = AndroidUtilities.dp(0);
        layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        typeTextView.setLayoutParams(layoutParams);

        imageView = new ImageView(context);
        addView(imageView);
        layoutParams = (LayoutParams) imageView.getLayoutParams();
        layoutParams.width = AndroidUtilities.dp(40);
        layoutParams.height = AndroidUtilities.dp(40);
        layoutParams.leftMargin = AndroidUtilities.dp(16);
        layoutParams.rightMargin = AndroidUtilities.dp(0);
        layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(layoutParams);

        checkBox = new CheckBox(context);
        checkBox.setVisibility(GONE);
        checkBox.setFocusable(false);
        addView(checkBox);
        layoutParams = (LayoutParams) checkBox.getLayoutParams();
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
//        layoutParams.topMargin = AndroidUtilities.dp(34);
        layoutParams.leftMargin = AndroidUtilities.dp(16) ;
        layoutParams.rightMargin = AndroidUtilities.dp(16);
        layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        checkBox.setLayoutParams(layoutParams);

        storageTextView = new TextView(context);
        storageTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        storageTextView.setGravity(Gravity.CENTER);
        storageTextView.setSingleLine(true);
        storageTextView.setTextColor(Color.RED);
        storageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        storageTextView.setTypeface(Typeface.DEFAULT_BOLD);
        storageTextView.setVisibility(GONE);
        storageTextView.setText("已导入");
        addView(storageTextView);
        layoutParams = (LayoutParams) storageTextView.getLayoutParams();
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.leftMargin = AndroidUtilities.dp(16) ;
        layoutParams.rightMargin = AndroidUtilities.dp(16);
        layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        storageTextView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), MeasureSpec.EXACTLY));
    }

    /**
     * 设置item属性
     * @param text 名称
     * @param value 属性（文件夹或文件大小）
     * @param type 文件类型
     * @param thumb 绝对路径
     * @param resId 文件图标，只有文件夹有
     * @param isStorage 是否被导入了
     */
    public void setTextAndValueAndTypeAndThumb(String text, String value, String type, String thumb, int resId, boolean isStorage) {
        textView.setText(text);     //设置名称
        valueTextView.setText(value);   //设置是文件夹或者文件的大小

        if (type != null) { //type!=null 表示此item对应的是一个文件
            typeTextView.setVisibility(VISIBLE);
            typeTextView.setText(type);

            if (isStorage){
                storageTextView.setVisibility(VISIBLE);
                checkBox.setVisibility(View.GONE);
            }else{
                storageTextView.setVisibility(GONE);
                checkBox.setVisibility(View.VISIBLE);
            }
        } else {
            typeTextView.setVisibility(GONE);
            checkBox.setVisibility(View.GONE);
            storageTextView.setVisibility(GONE);
        }

        if (resId != 0) {
            if (thumb != null) {
//                imageView.setImage(thumb, "40_40", null);
            } else  {
                imageView.setImageResource(resId);
            }
            imageView.setVisibility(VISIBLE);
        } else {
            imageView.setVisibility(GONE);
        }
    }

    public CheckBox getCheckBox(){
        return checkBox;
    }

    public void setChecked(boolean checked) {
//        if (checkBox.getVisibility() != VISIBLE) {
//            checkBox.setVisibility(VISIBLE);
//        }
        checkBox.setChecked(checked);
    }
}
