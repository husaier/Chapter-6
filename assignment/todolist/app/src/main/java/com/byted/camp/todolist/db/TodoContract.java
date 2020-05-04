package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

import com.byted.camp.todolist.operation.db.FeedReaderContract;
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FreedEntry.TABLE_NAME + " (" +
                    FreedEntry._ID + " INTEGER PRIMARY KEY," +
                    FreedEntry.COLUMN_NAME_TASK + " TEXT," +
                    FreedEntry.COLUMN_NAME_DATETIME + " TEXT," +
                    //FreedEntry.COLUMN_NAME_PRIORITY + " INTEGER," +
                    FreedEntry.COLUMN_NAME_FINISHED + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FreedEntry.TABLE_NAME;

    private TodoContract() {
    }

    public static class FreedEntry implements BaseColumns {

        public static final String TABLE_NAME = "todolist";

        public static final String COLUMN_NAME_TASK = "task";

        public static final String COLUMN_NAME_DATETIME = "datetime";

       // public static final String COLUMN_NAME_PRIORITY = "priority";

        public static final String COLUMN_NAME_FINISHED = "finished";
    }
}
