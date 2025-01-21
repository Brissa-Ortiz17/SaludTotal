package com.example.salud_total_v2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashSet;
import java.util.List;

public class EventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, List<CalendarDay> dates) {
        this.drawable = new ColorDrawable(color);
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}
