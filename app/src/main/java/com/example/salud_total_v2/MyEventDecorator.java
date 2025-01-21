package com.example.salud_total_v2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.Set;

public class MyEventDecorator implements DayViewDecorator {

    private final Drawable highlightDrawable;
    private final HashSet<CalendarDay> dates;

    public MyEventDecorator(int color, Set<CalendarDay> dates) {
        highlightDrawable = new ColorDrawable(color);
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }
}
