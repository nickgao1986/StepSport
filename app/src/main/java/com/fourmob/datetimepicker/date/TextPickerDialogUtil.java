package com.fourmob.datetimepicker.date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.pic.optimize.R;
import com.pic.optimize.helper.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;


public class TextPickerDialogUtil {

    private Context context;

    private LinearLayout layout;//布局文件

    private OnSelectChangeListener listener;//监听
    /**
     * 可以循环滚动
     */
    private boolean canWrapSelectorWheel = false;

    private AlertDialog dialog;

    public List<NumberPicker> pickers;
    private String neutral;
    private DialogInterface.OnClickListener neutralListener;
    private String negative;
    private DialogInterface.OnClickListener negativeListener;
    private String positive;
    private DialogInterface.OnClickListener positiveListener;
    /**
     * 是否隐藏NegativeButton
     */
    private boolean hideNegativeButton = false;

    // FIXME: 2018/8/29 这个的context 必须是Activity，因为是要弹出dialog
    public TextPickerDialogUtil(Context context) {
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.pickers = new ArrayList<>();
        this.layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.textpicker_new, null);
    }

    /**
     * show，显示对话框
     *
     * @param title 对话框标题
     */
    public void show(String title) {
        ViewParent parent = layout.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(layout);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title == null ? "" : title);
        String positiveText = positive;
        if (TextUtils.isEmpty(positiveText)) {
            positiveText = "完成";
        }
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onConfirm(getValues());
                }
                if (positiveListener != null) {
                    positiveListener.onClick(dialog, which);
                }
            }
        });

        String negativeText = negative;
        if (TextUtils.isEmpty(negativeText)) {
            negativeText = "取消";
        }
        if (!hideNegativeButton) {
            builder.setNegativeButton(negativeText, negativeListener);
        }

        if (!TextUtils.isEmpty(neutral)) {
            builder.setNeutralButton(neutral, neutralListener);
        }
        dialog = builder.create();
        dialog.setView(layout, 0, 0, 0, 0);
        try {
            dialog.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public interface OnSelectChangeListener {
        void onDateChange(Integer[] value, NumberPicker changePicker);

        void onConfirm(Integer[] value);
    }

    public Integer[] getValues() {
        Integer[] values = new Integer[pickers.size()];
        for (int i = 0; i < pickers.size(); i++) {
            values[i] = pickers.get(i).getValue();
        }
        return values;
    }

    public NumberPicker addItems(String[] items) {
        return addItems(items, 1);
    }

    public NumberPicker addItems(String[] items, float weight) {
        NumberPicker picker = new NumberPicker(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = weight;
        layoutParams.leftMargin = ScreenUtil.dip2px(context, 15);
        layoutParams.rightMargin = ScreenUtil.dip2px(context, 15);
        picker.setLayoutParams(layoutParams);
        if (items != null) {
            picker.setDisplayedValues(items);
            picker.setMaxValue(items.length - 1);
            picker.setMinValue(0);
        }
        picker.setFocusable(true);
        picker.setFocusableInTouchMode(true);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (listener != null) {
                    listener.onDateChange(getValues(), picker);
                }
            }
        });
        picker.setWrapSelectorWheel(canWrapSelectorWheel);
        layout.addView(picker);
        pickers.add(picker);
        return picker;
    }

    public NumberPicker addItems(String[] items, float weight, boolean shouldHasLeftMargin) {
        NumberPicker picker = new NumberPicker(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = weight;
        if (shouldHasLeftMargin) {
            layoutParams.leftMargin = ScreenUtil.dip2px(context, 15);
        } else {
            layoutParams.rightMargin = ScreenUtil.dip2px(context, 15);
        }
        picker.setLayoutParams(layoutParams);
        if (items != null) {
            picker.setDisplayedValues(items);
            picker.setMaxValue(items.length - 1);
            picker.setMinValue(0);
        }
        picker.setFocusable(true);
        picker.setFocusableInTouchMode(true);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (listener != null) {
                    listener.onDateChange(getValues(), picker);
                }
            }
        });
        picker.setWrapSelectorWheel(canWrapSelectorWheel);
        layout.addView(picker);
        pickers.add(picker);
        return picker;
    }


    public OnSelectChangeListener getListener() {
        return listener;
    }

    public void setListener(OnSelectChangeListener listener) {
        this.listener = listener;
    }

    public boolean isCanWrapSelectorWheel() {
        return canWrapSelectorWheel;
    }

    public void setCanWrapSelectorWheel(boolean canWrapSelectorWheel) {
        this.canWrapSelectorWheel = canWrapSelectorWheel;
    }

    /**
     * fixme 可以改为 builder 模式
     * 设置中立按钮和回调
     *
     * @param neutral         按钮文案
     * @param neutralListener 按钮回调
     */
    public void setNeutralListener(String neutral, DialogInterface.OnClickListener neutralListener) {
        this.neutral = neutral;
        this.neutralListener = neutralListener;
    }

    /**
     * 设置取消按钮
     *
     * @param negative         按钮文案
     * @param negativeListener 按钮回调
     */
    public void setNegativeButton(String negative, DialogInterface.OnClickListener negativeListener) {
        this.negative = negative;
        this.negativeListener = negativeListener;
    }

    /**
     * 隐藏取消按钮
     * @param hide
     */
    public void setNegativeButtonHide(boolean hide) {
        this.hideNegativeButton = hide;
    }

    /**
     * 设置确定按钮
     *
     * @param text     按钮文案
     * @param listener 按钮回调
     */
    public void setPositiveButton(String text, final DialogInterface.OnClickListener listener) {
        this.positive = text;
        this.positiveListener = listener;
    }


}
