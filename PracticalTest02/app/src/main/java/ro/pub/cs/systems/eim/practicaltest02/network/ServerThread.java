package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.net.*;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.model.WeatherForecastInformation;
import ro.pub.cs.systems.eim.practicaltest02.model.Alarma;

public class ServerThread extends Thread {

    private int port = 0;
    private ServerSocket serverSocket = null;

    private HashMap<String, Alarma> data = null;
    private String message;
    //private Alarma data = null;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        this.data = new HashMap<>();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setData(String ip, Alarma al) {
        this.data.put(ip, al);
    }

    public synchronized String getMessage () {
        return message;
    }

    public synchronized void sendCommand(String ip, String command) {
        if (command.contains("set,")) {
            String[] tokens = command.split(",");
            Alarma al = new Alarma(tokens[1], tokens[2]);
            this.data.put(ip, al);
            Log.e(Constants.TAG, "S-a setat alarma pentru: " + ip + " cu ora: " + data.get(ip).toString());
            message = "S-a setat alarma pentru: " + ip + " cu ora: " + data.get(ip).toString();
        } else if (command.contains("reset")) {
            if(this.data.containsKey(ip) != false) {
                this.data.remove(ip);
            }
            message = "Alarm removed";
        } else if (command.contains("poll")) {
            try {
                Socket soc = new Socket("utcnist.colorado.edu"/*InetAddress.getLocalHost()*/, 13);
                BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                //System.out.println(in.readLine());
                Log.e(Constants.TAG, "TIMSADSA: " + in.readLine());
            } catch (IOException e){
                if (Constants.DEBUG) {
                    e.printStackTrace();
                }
            }
            int currenthour = 19;
            int currentminute = 30;
            if (data.containsKey(ip) == false) {
                message = "none";
            } else {
                Alarma al = data.get(ip);
                if (Integer.parseInt(al.getH()) <= currenthour && Integer.parseInt(al.getM()) <= currentminute) {
                    message = "inactive";
                } else {
                    message = "active";
                }
            }
        }
    }

    public synchronized HashMap<String, Alarma> getData() {
        return data;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
            if (Constants.DEBUG) {
                clientProtocolException.printStackTrace();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

}
