package com.astuetz.viewpager.extensions.fragment;

/**
 * Created by Viktor on 10/20/2015.
 */
public interface Communicator {
    public void Send(int from, int to, Object object);
    public void Receive(int id, Object object);
}