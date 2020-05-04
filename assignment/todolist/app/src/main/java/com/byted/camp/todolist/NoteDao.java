package com.byted.camp.todolist;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.byted.camp.todolist.beans.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM todolist")
    List<Note> getAll();

    @Insert
    void insertAll(Note... notes);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);
}
