package com.byted.camp.todolist.beans;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public String dateToString(Date date){
        return date.toString();
    }

    @TypeConverter
    public Date stringToDate(String string){
        return new Date(string);
    }
}
