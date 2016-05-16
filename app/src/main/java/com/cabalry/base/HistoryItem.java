package com.cabalry.base;

/**
 * HistoryItem
 */
public class HistoryItem {

    String mUsername;
    int mUserId;
    int mAlarmId;
    String mTimestamp;
    String mState;

    public HistoryItem(String username, int userId, int alarmId, String timestamp, String state) {
        mUsername = username;
        mUserId = userId;
        mAlarmId = alarmId;
        mTimestamp = timestamp;
        mState = state;
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

    public String getState() {
        return mState;
    }

    @Override
    public String toString() {
        return mUsername + "~" + mUserId + "~" + mAlarmId + "~" + mTimestamp;
    }
}
