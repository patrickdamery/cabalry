package com.cabalry.custom;
import java.io.IOException;
import java.net.*;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.widget.Toast;
import com.cabalry.db.GlobalKeys;
import com.cabalry.utils.Logger;
import com.cabalry.utils.Preferences;
/**
 * Created by conor on 10/01/15.
 */
public class AudioStreamProgram {

    public byte[] buffer;
    private DatagramSocket socket;
    private int port = 50005;
    AudioRecord recorder;
    private int sampleRate = 16000;
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
                    Logger.log("Socket Created");

                    byte[] buffer = new byte[2048];
                    Logger.log("Buffer created of size " + minBufSize);

                    DatagramPacket packet;
                    final InetAddress destination = InetAddress.getByName(Preferences.getIP());
                    Logger.log("Address retrieved");

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);
                    Logger.log("Recorder initialized");
                    recorder.startRecording();
                    while(status) {

                        // Reading data from MIC into buffer.
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        // Putting buffer in the packet.
                        packet = new DatagramPacket (buffer,buffer.length,destination,port);
                        try {
                            //Try sending audio packet
                            socket.send(packet);
                        } catch (SocketException se) {
                            //don't do anything just keep trying
                            Logger.log("Lost Connection");
                        }
                        Logger.log("MinBufferSize: " + minBufSize);
                    }
                    Logger.log("Ending");
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
        status = false;
        recorder.release();
    }
}