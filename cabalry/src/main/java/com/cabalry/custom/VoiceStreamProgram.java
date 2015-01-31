package com.cabalry.custom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;

/**
 * Created by conor on 10/01/15.
 */
public class VoiceStreamProgram {

    public byte[] buffer;
    public static DatagramSocket socket;
    private int port = 50005;
    AudioRecord recorder;

    private int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;

    public void startStream() {
        status = true;

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    Logger.log("VS - Socket Created");

                    byte[] buffer = new byte[minBufSize];

                    Logger.log("VS - Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    final InetAddress destination = InetAddress.getByName(Preferences.getString(GlobalKeys.IP));
                    Logger.log("VS - Address retrieved");


                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);
                    Logger.log("VS - Recorder initialized");

                    recorder.startRecording();


                    while(status == true) {

                        //reading data from MIC into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        //putting buffer in the packet
                        packet = new DatagramPacket (buffer,buffer.length,destination,port);

                        socket.send(packet);
                        System.out.println("MinBufferSize: " +minBufSize);
                    }



                } catch(UnknownHostException e) {
                    Logger.log("VS - UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.log("VS - IOException");
                }
            }

        });
        streamThread.start();
    }

    public void stopStream() {
        status = false;
        recorder.release();
    }
}
