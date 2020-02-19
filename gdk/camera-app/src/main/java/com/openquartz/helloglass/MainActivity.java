package com.openquartz.helloglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import android.speech.tts.TextToSpeech;

import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.io.File;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {

    private Socket socket = new Socket();
    private static final int SERVERPORT = 8088;
    private static final String SERVER_IP = "3.20.234.93"; //EC2 Public

    private TextToSpeech tts;

    private static final int TAKE_PICTURE_REQUEST = 1;
    private static final int TAKE_VIDEO_REQUEST = 2;
    private GestureDetector mGestureDetector = null;
    private CameraView cameraView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Turn on Gestures
        mGestureDetector = createGestureDetector(this);

        new Thread(new ClientThread()).start();

    }

    /**
     * Gesture detection for fingers on the Glass
     */
    private GestureDetector createGestureDetector(Context context) {
        final GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                    // Tap with a single finger for photo
                    if (gesture == Gesture.TAP) {

                        try{

                            final String str = "test";
                            final PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                            System.out.println(str);

                            final BufferedReader inp = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        out.write(str);

                                        out.flush();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.i(TAG, "SendDataToNetwork: Message send failed. Caught an exception");
                                    }


                                    String response = "";
                                    try {
                                        Thread.sleep(10000);
                                        System.out.println("waited");
                                    }

                                    catch (Exception e) {
                                        e.printStackTrace();
                                        Log.i(TAG, "WaitForResponse: Caught an exception");

                                    }


                                    System.out.println("got");
                                    System.out.println(response);
                                    System.out.println("here");

                                    try {
                                        while ((response = inp.readLine()) != null) {
                                            System.out.println(response);
                                            tts.stop();
                                            tts.speak(response, TextToSpeech.QUEUE_FLUSH, null);
                                        }
                                        System.out.println("past it");
                                    } catch (Exception e) {
                                        System.out.println(response);
                                        e.printStackTrace();
                                        Log.i(TAG, "ReceiveDataFromNetwork: Input reception failed. Caught an exception");
                                    }

                                }
                            }).start();

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                        return true;
                    }

                return false;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Send generic motion events to the gesture detector
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }


    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException     e1) {
                e1.printStackTrace();
            }

        }

    }

}
