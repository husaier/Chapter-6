package com.byted.camp.todolist.beans;

import android.arch.persistence.room.TypeConverter;

public class StateTypeConverter {
    @TypeConverter
    public int stateToString(State state){
        return state.intValue;
    }

    @TypeConverter
    public State stringToState(int n){
        State state;
        if(n == 1)
            state = State.DONE;
        else
            state = State.TODO;
        return state;
    }
}
