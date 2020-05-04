package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.byted.camp.todolist.db.TodoContract.FreedEntry;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.db.FeedReaderContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity-husaier";

    private EditText editText;
    private Button addBtn;

    private TodoDbHelper dbHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);

        dbHelper = new TodoDbHelper(this);  //连接数据库

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();  //disconnect
    }

    private boolean saveNote2Database(final String content) {
        // TODO 插入一条新数据，返回是否插入成功
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FreedEntry.COLUMN_NAME_TASK, content);
        Date date = new Date();
        SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd EEEE HH:mm:ss");
        bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String bjDate = bjSdf.format(date);
        values.put(FreedEntry.COLUMN_NAME_DATETIME, bjDate);
        values.put(FreedEntry.COLUMN_NAME_FINISHED, 0);
        long newRowId = db.insert(FreedEntry.TABLE_NAME, null, values);
        if(newRowId > 0){
            Log.d(TAG, "perform add data, result: " + newRowId);
            return true;
        }else{
            Log.d(TAG, "插入失败！");
            return false;
        }
    }
}
