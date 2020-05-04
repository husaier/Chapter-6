package com.byted.camp.todolist.beans;

import java.util.Date;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
@Entity(tableName = "todolist")
public class Note {
    @PrimaryKey
    public final long id;

    @ColumnInfo(name = "datetime")
    @TypeConverters(DateTypeConverter.class)
    private Date date;

    @ColumnInfo(name = "finished")
    @TypeConverters(StateTypeConverter.class)
    private State state;

    @ColumnInfo(name = "task")
    private String content;

    public Note(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
