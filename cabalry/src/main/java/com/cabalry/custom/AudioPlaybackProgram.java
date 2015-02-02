package com.cabalry.custom;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by conor on 01/02/15.
 */
public class AudioPlaybackProgram {

    private boolean isPlaying = false;

    public void startPlayback(int bufferSize, int port) {

        AudioTrack audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

        if(audioPlayer.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
            audioPlayer.play();

        try {
            //Define the datagram socket we will receive audio from
            DatagramSocket socket = new DatagramSocket(port);
            //Define bufffer size
            byte[] buffer = new byte[bufferSize];
            //Now prepare the datagram packet so we can start receiving data
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            //ByteArrayInputStream bias = new ByteArrayInputStream(packet.getData());

            do {
                //Receive data
                socket.receive(packet);
                byte[] soundbytes = packet.getData();
                //readBytes = is.read(buffer);

                //if(AudioRecord.ERROR_INVALID_OPERATION != readBytes){
                    audioPlayer.write(soundbytes, 0, soundbytes.length);
                //}
            }
            while(isPlaying);
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlayback() {
        isPlaying = false;
    }
}
