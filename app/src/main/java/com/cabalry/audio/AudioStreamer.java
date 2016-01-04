package com.cabalry.audio;
import java.io.IOException;
import java.net.*;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by conor on 10/01/15.
 */
public class AudioStreamer {
    private static final String TAG = "AudioStreamer";

    private static final int PORT = 50005;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private byte[] mBuffer;
    private DatagramSocket mSocket;
    private AudioRecord mRecorder;
    private int mMinBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private boolean mStatus = true;

    public void startStream(final String ip) {
        mStatus = true;
        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    Log.i(TAG, "Socket Created");

                    byte[] buffer = new byte[2048];
                    Log.i(TAG, "Buffer created of size " + mMinBufferSize);

                    DatagramPacket packet;
                    final InetAddress destination = InetAddress.getByName(ip);
                    Log.i(TAG, "Address retrieved");

                    mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, mMinBufferSize *10);
                    Log.i(TAG, "Recorder initialized");
                    mRecorder.startRecording();
                    while(mStatus) {

                        // Reading data from MIC into mBuffer.
                        mMinBufferSize = mRecorder.read(buffer, 0, buffer.length);

                        // Putting mBuffer in the packet.
                        packet = new DatagramPacket (buffer,buffer.length,destination, PORT);
                        try {
                            //Try sending audio packet
                            socket.send(packet);
                        } catch (SocketException se) {
                            //don't do anything just keep trying
                            Log.w(TAG, "Lost Connection");
                        }
                        Log.i(TAG, "MinBufferSize: " + mMinBufferSize);
                    }
                    Log.i(TAG, "Ending");
                    socket.close();
                } catch(UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        streamThread.start();
    }

    public void stopStream() {
        mStatus = false;
        mRecorder.release();
    }
}