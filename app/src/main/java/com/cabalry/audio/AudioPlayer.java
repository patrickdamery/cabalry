package com.cabalry.audio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * AudioPlayer
 */
public class AudioPlayer {
    public static final String TAG = "AudioPlayer";
    private final int SAMPLE_RATE = 16000;
    private final int serverPort = 50010;
    private final int tcpServerPort = 50000;
    private boolean isPlaying = false;
    private InetAddress ip;
    private DatagramSocket audioSocket;
    private DatagramPacket audioPacket;
    private byte[] data = new byte[1024];

    public void startPlayback(Context context, String userip) {

        isPlaying = true;

        Log.i(TAG, "Starting Service");
        AudioTrack audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 2048, AudioTrack.MODE_STREAM);

        if (audioPlayer.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
            audioPlayer.play();

        try {

            // Define ip address to connect to.
            ip = InetAddress.getByName(userip);

            /* TODO fix this
            java.net.UnknownHostException: Unable to resolve host "null": No address associated with hostname
            at java.net.InetAddress.lookupHostByName(InetAddress.java:424)
            at java.net.InetAddress.getAllByNameImpl(InetAddress.java:236)
            at java.net.InetAddress.getByName(InetAddress.java:289)
            at com.cabalry.audio.AudioPlayer.startPlayback(AudioPlayer.java:43) */

            // Define UDP try counter.
            int udpCount = 3;
            do {
                do {
                    try {
                        // Prepare Datagram Socket to receive audio.
                        audioSocket = new DatagramSocket();

                        // Send Server our Port number.
                        String port = Integer.toString(audioSocket.getLocalPort());
                        data = port.getBytes();
                        audioPacket = new DatagramPacket(data, data.length, ip, serverPort);
                        audioSocket.send(audioPacket);

                        // Wait for response.
                        audioSocket.setSoTimeout(6000);
                        audioPacket = new DatagramPacket(data, data.length);
                        boolean portDefined = false;
                        int dedicatedPort = 0;
                        while (!portDefined) {

                            audioSocket.receive(audioPacket);
                            Log.i(TAG, "Received packet.");

                            // Extract data received.
                            String received = new String(audioPacket.getData());
                            received = received.trim();

                            // Check if it is a test.
                            if (received.equalsIgnoreCase("TEST")) {
                                String response = "Good";
                                data = response.getBytes();
                                audioPacket = new DatagramPacket(data, data.length);
                                audioSocket.send(audioPacket);
                            } else {

                                // Save the port and exit loop.
                                dedicatedPort = Integer.parseInt(received);
                                portDefined = true;
                            }
                            Log.i(TAG, "Port " + dedicatedPort);
                        }

                        // Send six different packets to server's dedicated port to ensure we punch a hole through a NAT.
                        Log.i(TAG, "Attempting to punch hole through NAT.");
                        for (int x = 0; x < 6; x++) {
                            audioPacket = new DatagramPacket(data, data.length, ip, dedicatedPort);
                            audioSocket.send(audioPacket);
                        }

                        // Prepare audio Packet to start receiving audio.
                        byte[] keepAlive = new byte[10];
                        DatagramPacket keepAlivePacket = new DatagramPacket(keepAlive, keepAlive.length, ip, dedicatedPort);
                        byte[] receiveData = new byte[2048];
                        audioPacket = new DatagramPacket(receiveData, receiveData.length);

                        do {
                            // Receive audio.
                            audioSocket.receive(audioPacket);

                            // Send back audio to keep alive UDP connection.
                            audioSocket.send(keepAlivePacket);
                            Log.i(TAG, "Receiving Audio.");

                            // Write audio to speakers.
                            audioPlayer.write(receiveData, 0, receiveData.length);
                        } while (isPlaying);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        Log.e(TAG, "Failed connection trying again.");
                        udpCount--;
                    }
                } while (udpCount > 0 && isPlaying);

                do {
                    Log.e(TAG, "UDP Failed, attempting with TCP.");
                    // Setup socket to connect to server.
                    Socket client = new Socket(ip, tcpServerPort);
                    client.setSoTimeout(15000);

                    // Define data input stream.
                    Log.i(TAG, "Get DataInput Stream from server");
                    DataInputStream dis = new DataInputStream(client.getInputStream());

                    long t = System.currentTimeMillis();
                    long end = t + 60000;
                    do {
                        // Get data from input stream.
                        byte[] receiveData = new byte[2048];
                        Log.i(TAG, "Getting Data");

                        try {
                            dis.readFully(receiveData);
                        } catch (SocketTimeoutException e) {
                            // TODO handle case when playback was not started
                            Log.e(TAG, "Error playback was not started!");
                        }

                        // Write audio to speakers.
                        audioPlayer.write(receiveData, 0, receiveData.length);
                    } while (System.currentTimeMillis() < end);

                    // Close all connections.
                    dis.close();
                    client.close();
                } while (isPlaying);
            } while (isPlaying);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlayback() {
        isPlaying = false;
    }
}