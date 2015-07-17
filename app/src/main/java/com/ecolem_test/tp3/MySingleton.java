package com.ecolem_test.tp3;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by akawa_000 on 17/07/2015.
 */
public class MySingleton {
    private static MySingleton mInstance;
    private static Context mCtx;

    private static Socket socket;
    private static CountDownLatch latch;

    private MySingleton(Context context) {
        mCtx = context;
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public static synchronized void initSocketIO(){
        // Socket IO
        try {
            socket = IO.socket("http://localhost:3001");
        } catch (URISyntaxException e) {
            Toast.makeText(mCtx, "URI Syntax error", Toast.LENGTH_SHORT).show();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                System.out.println("Connecté");
                // TODO Auto-generated method stub
                JSONObject json = new JSONObject();
                socket.emit("add user", "Alain");
            }
        }).on("login", new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                Log.d("TOTO", "login done");
            }
        }).on("user joined", new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                Log.d("TOTO", "user join:" + arg0[0]);
            }
        }).on("new message", new Emitter.Listener() {
            @Override
            public void call(Object... arg0) {
                Log.d("TOTO", "new message:" + arg0[0] + " " + arg0[1]);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... arg0) {
                // TODO Auto-generated method stub
                Log.d("TOTO", "Déconnecté");
                latch.countDown();

            }
        });
        socket.connect();
        latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Emit a toast
        }
    }
}

