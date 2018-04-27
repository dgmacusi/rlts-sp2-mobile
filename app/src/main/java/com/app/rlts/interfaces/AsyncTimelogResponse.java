package com.app.rlts.interfaces;

import com.app.rlts.entity.Timelog;

import java.util.ArrayList;

public interface AsyncTimelogResponse {
    public void retrieveTimelog(ArrayList<Timelog> timelogArray);
}
