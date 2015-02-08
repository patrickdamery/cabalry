package com.cabalry.custom;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;

import java.io.DataInputStream;
import java.net.*;

/**
 * Created by conor on 01/02/15.
 */
public class AudioPlaybackProgram {

    private boolean isPlaying = false;
    private final int SAMPLE_RATE = 16000;
    private final int listenPort = 50010;
    private InetAddress ip;
    private Socket client;
    private DataInputStream dis;

    public void startPlayback(int bufferSize, int port) {

        isPlaying = true;

        Logger.log("Starting Service");
        AudioTrack audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

        if(audioPlayer.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
            audioPlayer.play();

        try {
            // Define buffer
            byte[] buffer = new byte[bufferSize];

            // Define ip address to connect to
            ip = InetAddress.getByName(Preferences.getIP());

            // Setup socket to connect to server
            Logger.log("Connect to server");
            client = new Socket(ip, listenPort);
            client.setSoTimeout(15000);

            // Define data input stream
            Logger.log("Get DataInput Stream from server");
            dis = new DataInputStream(client.getInputStream());

            do {
                dis.readFully(buffer);
                audioPlayer.write(buffer, 0, buffer.length);
            } while(isPlaying);

            dis.close();
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlayback() {
        isPlaying = false;
    }
}
