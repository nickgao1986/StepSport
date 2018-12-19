package com.fourmob.datetimepicker.date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.pic.optimize.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 日期专用控件
 * Created by sbb on 2015/6/8.
 */
public class DatePickerDialogUtil {

    private static final int YEAR_MIN = 1970;  // 默认最小年限

    public enum ShowState {
        SHOW_DATE_ONLY,//仅显示日期
        SHOW_DATE_AND_TEXT_ONLY,//仅显示日期和文字标识
        SHOW_TIME_ONLY,//仅显示时间和文字标识
        SHOW_TIME_AND_TEXT_ONLY,//仅显示日期和文字标识
        BOTH_NO_TEXT,//显示日期和时间
        BOTH_AND_TEXT,//显示日期和时间和文字标识
        SHOW_NO_YEAR//不显示年，其他都展示
    }

    private Context context;

    private View layout;//布局文件

    private NumberPicker year, month, day, hour, minute;

    private View year_txt, month_txt, day_txt, hour_txt, minute_txt;

    private final ShowState DEFAULT_SHOWSTATE = ShowState.SHOW_DATE_ONLY; //默认只显示日期

    private ShowState showState = DEFAULT_SHOWSTATE;

    private Calendar maxDate;

    private Calendar minDate;

    private OnDateChangeListener listener;//监听

    private boolean canWrapSelectorWheel = false;//可以循环滚动

    private AlertDialog dialog;

    private String mTitle;

    private boolean isVarTitle = false;  // 是否改变标题

    public DatePickerDialogUtil(Context context) {
        this(context, null);
    }

    public DatePickerDialogUtil(Context context, ShowState showState) {
        this(context, null, showState, null);
    }

    public DatePickerDialogUtil(Context context, ShowState showState, Calendar maxDate) {
        this(context, null, showState, maxDate, null);
    }

    public DatePickerDialogUtil(Context context, Calendar calendar, ShowState showState, Calendar maxDate) {
        this(context, calendar, showState, maxDate, null);
    }

    /**
     * yoooooooooooooo
     *
     * @param context   上下文
     * @param calendar  默认选中日期
     * @param showState 显示状态
     * @param maxDate   最大日期
     * @param minDate   最小日期
     */
    public DatePickerDialogUtil(Context context, Calendar calendar, ShowState showState, Calendar maxDate, Calendar minDate) {
        init(context, calendar, showState, maxDate, minDate);
    }

    private void init(Context context, Calendar calendar, ShowState showState, Calendar maxDate, Calendar minDate) {
        this.context = context;
        this.layout = LayoutInflater.from(context).inflate(R.layout.timepicker_new, null);
        if (minDate != null) {
            this.minDate = minDate;
        } else {//默认最小值： 1970 年
            this.minDate = Calendar.getInstance(Locale.CHINA);
            this.minDate.set(YEAR_MIN, 0, 1);
        }
        if (maxDate != null) {
            this.maxDate = maxDate;
        } else {//默认最大值：当前时间 + 10年
            this.maxDate = Calendar.getInstance(Locale.CHINA);
            this.maxDate.set(Calendar.YEAR, this.maxDate.get(Calendar.YEAR) + 10);
        }
        if (showState != null) {
            this.showState = showState;
        }
        if (layout != null) {
            year = (NumberPicker) layout.findViewById(R.id.year);
            month = (NumberPicker) layout.findViewById(R.id.month);
            day = (NumberPicker) layout.findViewById(R.id.day);
            hour = (NumberPicker) layout.findViewById(R.id.hour);
            minute = (NumberPicker) layout.findViewById(R.id.min);

            year_txt = layout.findViewById(R.id.year_txt);
            month_txt = layout.findViewById(R.id.month_txt);
            day_txt = layout.findViewById(R.id.day_txt);
            hour_txt = layout.findViewById(R.id.hour_txt);
            minute_txt = layout.findViewById(R.id.min_txt);

            switch (this.showState) {
                case SHOW_DATE_ONLY:
                    showDayOnly();
                    break;
                case SHOW_DATE_AND_TEXT_ONLY:
                    showDayAndTextOnly();
                    break;
                case SHOW_TIME_ONLY:
                    showTimeOnly();
                    break;
                case SHOW_TIME_AND_TEXT_ONLY:
                    showTimeAndTextOnly();
                    break;
                case BOTH_NO_TEXT:
                    showBothTime();
                    break;
                case BOTH_AND_TEXT:
                    showBothTime();
                    showDayText();
                    showTimeText();
                    break;
                case SHOW_NO_YEAR:
                    showNoYear();
                    break;
            }
            setCalendar(calendar);
        }
    }

    public void show() {
        ViewParent parent = layout.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(layout);
        }
        dialog = new AlertDialog.Builder(context)
                .setTitle(mTitle == null ? "选择日期" : mTitle)
                .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onConfirm(getCalendar());
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.setView(layout, 0, 0, 0, 0);
        try {
            dialog.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public interface OnDateChangeListener {
        void onDateChange(Calendar calendar);

        void onConfirm(Calendar calendar);
    }

    public void setCalendar(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        if (listener != null) {
            listener.onDateChange(calendar);
        }

        int _year = calendar.get(Calendar.YEAR);
        int _month = calendar.get(Calendar.MONTH) + 1;
        int _day = calendar.get(Calendar.DAY_OF_MONTH);
        int _hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int _min = calendar.get(Calendar.MINUTE);

        year.setMaxValue(maxDate.get(Calendar.YEAR));
        year.setMinValue(minDate.get(Calendar.YEAR));

        int year_num = _year;
        int month_num = _month;

        setNumberByScoll(year_num,month_num);



        // 年
        year.setFocusable(true);
        year.setFocusableInTouchMode(true);
        year.setValue(_year);

        // 月
        month.setFocusable(true);
        month.setFocusableInTouchMode(true);
        month.setValue(_month);

        // 日
        day.setFocusable(true);
        day.setFocusableInTouchMode(true);
        day.setValue(_day);
        day.setTag("day");

        hour.setMaxValue(23);
        hour.setMinValue(0);
        hour.setFocusable(true);
        hour.setFocusableInTouchMode(true);
        hour.setValue(_hour);

        minute.setMaxValue(59);
        minute.setMinValue(0);
        minute.setFocusable(true);
        minute.setFocusableInTouchMode(true);
        minute.setValue(_min);

        setWrapSelectorWheel(canWrapSelectorWheel);

        // 添加监听
        NumberPicker.OnValueChangeListener wheelListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldValue, int newValue) {
                int year_num = year.getValue();
                int month_num = month.getValue();

                setNumberByScoll(year_num,month_num);

                if (isVarTitle && dialog != null && dialog.isShowing()) {
                    int year_str = year.getValue();
                    int month_str = month.getValue();
                    int day_str = day.getValue();
                    int hour_str = hour.getValue();
                    int min_str = minute.getValue();

                    switch (showState) {
                        case SHOW_DATE_ONLY:
                        case SHOW_DATE_AND_TEXT_ONLY:
                            dialog.setTitle(year_str + "年" + month_str + "月" + day_str + "日");
                            break;
                        case SHOW_TIME_ONLY:
                        case SHOW_TIME_AND_TEXT_ONLY:
                            dialog.setTitle(hour_str + "时" + min_str + "分");
                            break;
                        case BOTH_NO_TEXT:
                        case BOTH_AND_TEXT:
                            dialog.setTitle(year_str + "年" + month_str + "月" + day_str + "日" + hour_str + "时" + min_str + "分");
                            break;
                        case SHOW_NO_YEAR:
                            dialog.setTitle(month_str + "月" + day_str + "日" + hour_str + "时" + min_str + "分");
                            break;
                    }
                }

                if (listener != null) {
                    listener.onDateChange(getCalendar());
                }
            }
        };

        year.setOnValueChangedListener(wheelListener);
        month.setOnValueChangedListener(wheelListener);
        day.setOnValueChangedListener(wheelListener);
        hour.setOnValueChangedListener(wheelListener);
        minute.setOnValueChangedListener(wheelListener);
        wheelListener.onValueChange(null, 0, 0);
    }

    public void setNumberByScoll(int year_num, int month_num) {
        int maxYear = maxDate.get(Calendar.YEAR);
        int maxMonth = maxDate.get(Calendar.MONTH) + 1;
        int maxDay = maxDate.get(Calendar.DAY_OF_MONTH);

        int minYear = minDate.get(Calendar.YEAR);
        int minMonth = minDate.get(Calendar.MONTH) + 1;
        int minDay = minDate.get(Calendar.DAY_OF_MONTH);

        month.setMaxValue(12);
        month.setMinValue(1);

        if (year_num == minYear) {
            month.setMinValue(minMonth);
        }
        if (year_num == maxYear) {
            month.setMaxValue(maxMonth);
        }

        year_num = year.getValue();
        month_num = month.getValue();

        setDayMax(year_num, month_num);

        if (year_num == minYear && month_num == minMonth) {
            day.setMinValue(minDay);
        }
        if (year_num == maxYear && month_num == maxMonth) {
            day.setMaxValue(maxDay);
        }

        setWrapSelectorWheel(canWrapSelectorWheel);
    }

  public void setDayMax(int year_num, int month_num) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        List<String> list_big = Arrays.asList(months_big);
        List<String> list_little = Arrays.asList(months_little);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big
                .contains(String.valueOf(month_num))) {
            day.setMaxValue(31);
            day.setMinValue(1);
        } else if (list_little.contains(String.valueOf(month_num))) {
            day.setMaxValue(30);
            day.setMinValue(1);
        } else {
            if ((year_num % 4 == 0 && year_num % 100 != 0)
                    || year_num % 400 == 0) {
                day.setMaxValue(29);
                day.setMinValue(1);
            } else {
                day.setMaxValue(28);
                day.setMinValue(1);
            }
        }
        setWrapSelectorWheel(canWrapSelectorWheel);
    }

    public Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(year.getValue(), month.getValue() - 1, day.getValue(), hour.getValue(), minute.getValue());
        return calendar;
    }

    private void showBothTime() {
        day.setVisibility(View.VISIBLE);
        year.setVisibility(View.VISIBLE);
        month.setVisibility(View.VISIBLE);
        hour.setVisibility(View.VISIBLE);
        minute.setVisibility(View.VISIBLE);
    }

    private void showDayOnly() {
        day.setVisibility(View.VISIBLE);
        year.setVisibility(View.VISIBLE);
        month.setVisibility(View.VISIBLE);
        hour.setVisibility(View.GONE);
        minute.setVisibility(View.GONE);

        day_txt.setVisibility(View.GONE);
        year_txt.setVisibility(View.INVISIBLE);
        month_txt.setVisibility(View.INVISIBLE);
        hour_txt.setVisibility(View.GONE);
        minute_txt.setVisibility(View.GONE);
    }

    private void showTimeOnly() {
        day.setVisibility(View.GONE);
        year.setVisibility(View.GONE);
        month.setVisibility(View.GONE);
        hour.setVisibility(View.VISIBLE);
        minute.setVisibility(View.VISIBLE);

        day_txt.setVisibility(View.GONE);
        year_txt.setVisibility(View.GONE);
        month_txt.setVisibility(View.GONE);
        hour_txt.setVisibility(View.INVISIBLE);
        minute_txt.setVisibility(View.GONE);
    }

    private void showDayText() {
        day_txt.setVisibility(View.VISIBLE);
        year_txt.setVisibility(View.VISIBLE);
        month_txt.setVisibility(View.VISIBLE);
    }

    private void showTimeText() {
        hour_txt.setVisibility(View.VISIBLE);
        minute_txt.setVisibility(View.VISIBLE);
    }

    private void showNoYear() {
        day.setVisibility(View.VISIBLE);
        year.setVisibility(View.GONE);
        month.setVisibility(View.VISIBLE);
        hour.setVisibility(View.VISIBLE);
        minute.setVisibility(View.VISIBLE);

        day_txt.setVisibility(View.VISIBLE);
        year_txt.setVisibility(View.GONE);
        month_txt.setVisibility(View.VISIBLE);
        hour_txt.setVisibility(View.VISIBLE);
        minute_txt.setVisibility(View.VISIBLE);
    }


    private void showDayAndTextOnly() {
        showDayOnly();
        showDayText();
    }

    private void showTimeAndTextOnly() {
        showTimeOnly();
        showTimeText();
    }

    public Calendar getMaxDate() {
        return maxDate;
    }

    //zxb: 方法有错，应该是用 hour of Day
    public void setMaxDate(Calendar maxDate) {
        year.setMaxValue(maxDate.get(Calendar.YEAR));
        month.setMaxValue(maxDate.get(Calendar.MONTH) + 1);
        day.setMaxValue(maxDate.get(Calendar.DAY_OF_MONTH));
        hour.setMaxValue(maxDate.get(Calendar.HOUR));
        minute.setMaxValue(maxDate.get(Calendar.MINUTE));
        setWrapSelectorWheel(canWrapSelectorWheel);
        this.maxDate = maxDate;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setVarTitle(boolean isVarTitle) {
        this.isVarTitle = isVarTitle;
    }

    public void setWrapSelectorWheel(boolean b) {
        year.setWrapSelectorWheel(b);
        month.setWrapSelectorWheel(b);
        day.setWrapSelectorWheel(b);
        hour.setWrapSelectorWheel(b);
        minute.setWrapSelectorWheel(b);
    }

    public Calendar getMinDate() {
        return minDate;
    }


    /**
    * 推荐使用TimePicker控件 
     * 只能精确到年,这个控件不能实现 区间时间选取，设置min, max也会有问题。
     * @param minDate
     */
    public void setMinDate(Calendar minDate) {
        year.setMinValue(minDate.get(Calendar.YEAR));
        month.setMinValue(1);
        day.setMinValue(1);
        hour.setMinValue(1);
        minute.setMinValue(1);
        setWrapSelectorWheel(canWrapSelectorWheel);
        this.minDate = minDate;
    }

    public OnDateChangeListener getListener() {
        return listener;
    }

    public void setListener(OnDateChangeListener listener) {
        this.listener = listener;
    }

    public boolean isCanWrapSelectorWheel() {
        return canWrapSelectorWheel;
    }

    public void setCanWrapSelectorWheel(boolean canWrapSelectorWheel) {
        this.canWrapSelectorWheel = canWrapSelectorWheel;
    }
}
