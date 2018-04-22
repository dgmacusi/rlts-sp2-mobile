package com.app.rlts.interfaces;

import com.app.rlts.entity.Beacon;

import java.util.ArrayList;

public interface AsyncResponse {

    public void retrieveBeacons(ArrayList<Beacon> beacons);
}
