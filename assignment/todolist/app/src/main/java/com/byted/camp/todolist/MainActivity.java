package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.operation.db.AppDatabase;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.byted.camp.todolist.db.TodoContract.FreedEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main_husserl";
    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });

        dbHelper = new TodoDbHelper(this);

        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(!db.isOpen())
            return null;
        String[] projection = {
                BaseColumns._ID,
                FreedEntry.COLUMN_NAME_TASK,
                FreedEntry.COLUMN_NAME_DATETIME,
                FreedEntry.COLUMN_NAME_FINISHED
        };
        String sortOrder =
                FreedEntry.COLUMN_NAME_FINISHED;
        Cursor cursor = db.query(
                FreedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FreedEntry._ID));
            String task = cursor.getString(cursor.getColumnIndex(FreedEntry.COLUMN_NAME_TASK));
            String strDate = cursor.getString(cursor.getColumnIndex(FreedEntry.COLUMN_NAME_DATETIME));
            int sate = cursor.getInt(cursor.getColumnIndex(FreedEntry.COLUMN_NAME_FINISHED));

            Log.d(TAG, strDate);
            SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd EEEE HH:mm:ss");
            bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

            Note note = new Note(itemId);
            note.setContent(task);

            Date date = null;
            try{
                date = bjSdf.parse(strDate);
            }
            catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "日期解析错误 " + e.toString());
            }
            note.setDate(date);

            if(sate == 0)
                note.setState(State.TODO);
            else
                note.setState(State.DONE);
            notes.add(note);
            Log.d(TAG, "\nId: " + itemId + "\ntask: " + task + "\ndate: " + date.toString() + "\nstate: " + sate);
        }
        cursor.close();
        ContentValues values = new ContentValues();


        //mWorkHander.sendEmptyMessage(WorkHander.MSG_QUERY_DATA);

//        return ToDoDataBase.getInstance(this).noteDao().getAll();
        //return null;
        return notes;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FreedEntry.COLUMN_NAME_TASK + " LIKE ?";
        String[] selectionArgs = {note.getContent()};
        int deletedRows = db.delete(FreedEntry.TABLE_NAME, selection, selectionArgs);
        Log.i(TAG, "perform delete data, result:" + deletedRows);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(Note note) {
        // TODO 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FreedEntry.COLUMN_NAME_FINISHED, note.getState().intValue);

        String selection = FreedEntry.COLUMN_NAME_TASK + " LIKE ?";
        String[] selectionArgs = {note.getContent()};

        int count = db.update(
                FreedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(TAG, "perform update data, result:" + count);
    }
}
