package com.cabalry.util;

/**
 * MovingAverage
 */
public class MovingAverage {

    private int[] mSamples;
    private int mSum;
    private int mCurrentSample;
    private int mSampleCount;

    public MovingAverage(int size) {
        mSamples = new int[size];
        mSum = 0;
        mCurrentSample = 0;
        mSampleCount = 0;
    }

    public void addSample(int sample) {
        if (mSampleCount == mSamples.length) {
            mSum -= mSamples[mCurrentSample];
        } else {
            mSampleCount++;
        }

        mSamples[mCurrentSample] = sample;
        mSum += sample;
        mCurrentSample++;

        if (mCurrentSample >= mSamples.length) {
            mCurrentSample = 0;
        }
    }

    public int getCurrentAverage() {
        if (mSampleCount != 0) {
            return mSum / mSampleCount;
        }

        return 0;
    }
}
