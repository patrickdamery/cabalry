package com.cabalry.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.DataInputStream;
import java.net.*;

/**
 * Created by conor on 01/02/15.
 */
public class AudioPlayer {
    private static final String TAG = "AudioPlayer";

    private static final int SAMPLE_RATE = 16000;
    private static final int LISTEN_PORT = 50010;

    private boolean mIsPlaying = false;
    private InetAddress mIP;
    private Socket mClient;
    private DataInputStream mDataInputStream;

    public void startPlayback(String ip, int bufferSize) {
        Log.i(TAG, "On startPlayback()");
        mIsPlaying = true;

        AudioTrack audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

        if(audioPlayer.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
            audioPlayer.play();

        try {
            // Define mBuffer
            byte[] buffer = new byte[bufferSize];

            // Define mIP address to connect to
            mIP = InetAddress.getByName(ip);
            do {
                try {
                    Log.i(TAG, "Attempting to connect to server ...");

                    // Setup socket to connect to server.
                    mClient = new Socket(mIP, LISTEN_PORT);
                    mClient.setSoTimeout(15000);

                    // Define data input stream
                    Log.i(TAG, "Get DataInput Stream from server");
                    mDataInputStream = new DataInputStream(mClient.getInputStream());

                    do {
                        // Get data from input stream
                        Log.i(TAG, "Getting Data ...");
                        mDataInputStream.readFully(buffer);

                        // Write audio to speakers
                        audioPlayer.write(buffer, 0, buffer.length);
                    } while (mIsPlaying);

                    // Close all connections
                    mDataInputStream.close();
                    mClient.close();
                } catch (ConnectException ce) {
                    Log.e(TAG, "Failed connection trying again");
                }
            } while (mIsPlaying);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopPlayback() { mIsPlaying = false;  }
}
