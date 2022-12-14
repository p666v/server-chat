package ru.itsjava.services;

public interface Observable {
    void  addObserver(Observer observer);
    void  deleteObserver(Observer observer);
    void  notifyObserver(String message);
    void notifyObserverExpectMe(String message, Observer expectMe);
    void notifyObserverMe(String message, Observer Me);// test

}
