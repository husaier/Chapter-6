package com.byted.camp.todolist;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.byted.camp.todolist.beans.Note;


@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class ToDoDataBase extends RoomDatabase{
    private static final String DATABASE_NAME = "my_note.db";

    private static volatile ToDoDataBase sInstance;

    public abstract NoteDao noteDao();

    public static ToDoDataBase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (ToDoDataBase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private static ToDoDataBase buildDatabase(Context appContext) {
        return Room.databaseBuilder(appContext, ToDoDataBase.class, DATABASE_NAME)
//                .addMigrations(MIGRATION_1_2)
                .build();
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //
        }
    };
}
