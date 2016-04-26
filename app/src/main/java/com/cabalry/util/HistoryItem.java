package com.cabalry.util;

/**
 * HistoryItem
 */
public class HistoryItem {

    String mUsername;
    int mUserId;
    int mAlarmId;
    String mTimestamp;

    public HistoryItem(String username, int userId, int alarmId, String timestamp) {
        mUsername = username;
        mUserId = userId;
        mAlarmId = alarmId;
        mTimestamp = timestamp;
    }

    public String getUsername() {
        return mUsername;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getAlarmId() {
        return mAlarmId;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    @Override
    public String toString() {
        return mUsername + "~" + mUserId + "~" + mAlarmId + "~" + mTimestamp;
    }

    public static HistoryItem fromString(String str) {
        String[] result = str.split("~");
        return new HistoryItem(result[0], Integer.parseInt(result[1]), Integer.parseInt(result[2]), result[3]);
    }
}
