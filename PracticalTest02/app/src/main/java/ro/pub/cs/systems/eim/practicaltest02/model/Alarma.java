package ro.pub.cs.systems.eim.practicaltest02.model;

public class Alarma {
    public boolean isSet;
    String h;
    String m;

    public Alarma () {
        h = "";
        m = "";
        isSet = false;
    }

    public Alarma (String hour, String minut) {
        this.h = hour;
        this.m = minut;
    }


    public void setAlarm(String h, String m) {
        this.h = h;
        this.m = m;
    }

    public String getH() {
        return h;
    }

    public  String getM () {
        return m;
    }

    @Override
    public String toString() {

        return h + ":" + m;
    }
}
