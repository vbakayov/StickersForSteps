package com.astuetz.viewpager.extensions.fragment;


        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.util.Log;
        import android.widget.Toast;

/**
 *
\
 */
public class StepDetector implements SensorEventListener
{
//    private final static String TAG = "StepDetector";
//    public final static String ACTION_STEPS = "ACTION_STEPS";
//    private float   mLimit = 10;
//    private float   mLastValues[] = new float[3*2];
//    private float   mScale[] = new float[2];
//    private float   mYOffset;
//    private int     count=0;
//    private float   mLastDirections[] = new float[3*2];
//    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
//    private float   mLastDiff[] = new float[3*2];
//    private int     mLastMatch = -1;
//    private StepListener stepListener;

    private static final String TAG = "Pedometer";
    private static final String PREFS_NAME = "PedometerPrefs";

    private static final int DIMENSIONS = 3;
    private static final int INTERVAL_VARIATION = 250;
    private static final int NUM_INTERVALS = 2;
    private static final int WIN_SIZE = 20;
    private static final int MIN_SATELLITES = 4;
    private static final float STRIDE_LENGTH = (float) 0.73;
    private static final float PEAK_VALLEY_RANGE = (float) 4.0;
    private StepListener stepListener;
//
//    private final Context context;;
//    private final SensorManager sensorManager;
//    private final LocationManager locationManager;
//
//    private Location prevLocation;
//    private Location currentLocation;
//    private Location locationWhenGPSLost;

    private int       stopDetectionTimeout = 2000;
    private int       winPos = 0, intervalPos = 0;
    private int       numStepsWithFilter = 0, numStepsRaw = 0;
    private int       lastNumSteps = 0;
    private int[]     peak = new int[DIMENSIONS];
    private int[]     valley = new int[DIMENSIONS];
    private float[]   lastValley = new float[DIMENSIONS];
    private float[][] lastValues = new float[WIN_SIZE][DIMENSIONS];
    private float[]   prevDiff = new float[DIMENSIONS];
    private float     strideLength = STRIDE_LENGTH;
    private float     totalDistance = 0;
    private float     distWhenGPSLost = 0;
    private float     gpsDistance = 0;
    private long[]    stepInterval = new long[NUM_INTERVALS];
    private long      stepTimestamp = 0;
    private long      elapsedTimestamp = 0;
    private long      startTime = 0, prevStopClockTime = 0;
    private long      gpsStepTime = 0;
    private boolean[] foundValley = new boolean[DIMENSIONS];
    private boolean   startPeaking = false;
    private boolean   foundNonStep = true;
    private boolean   gpsAvailable = false;
    private boolean   calibrateSteps = true;
    private boolean   pedometerPaused = true;
    private boolean   useGps = true;
    private boolean   statusMoving = false;
    private boolean   firstGpsReading = true;



    /** Constructor. */
    public StepDetector(StepListener stepListener) {

        // some initialization
        winPos = 0;
        startPeaking = false;
        numStepsWithFilter = 0;
        numStepsRaw = 0;

        firstGpsReading = true;
        gpsDistance = 0;

        for (int i = 0; i < DIMENSIONS; i++) {
            foundValley[i] = true;
            lastValley[i] = 0;
        }
        this.stepListener=stepListener;
    }


//        public StepDetector(StepListener stepListener) {
//            int h = 480; // TODO: remove this constant
//            mYOffset = h * 0.5f;
//            mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
//            mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
//            this.stepListener = stepListener;
//        }



//    public void setSensitivity(float sensitivity) {
//        mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
//    }



//    //public void onSensorChanged(int sensor, float[] values) {
//    public void onSensorChanged(SensorEvent event) {
//        Sensor sensor = event.sensor;
//        synchronized (this) {
//            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
//            }
//            else {
//                int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
//                if (j == 1) {
//                    float vSum = 0;
//                    for (int i=0 ; i<3 ; i++) {
//                        final float v = mYOffset + event.values[i] * mScale[j];
//                        vSum += v;
//                    }
//                    int k = 0;
//                    float v = vSum / 3;
//
//                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
//                    if (direction == - mLastDirections[k]) {
//                        // Direction changed
//                        int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
//                        mLastExtremes[extType][k] = mLastValues[k];
//                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);
//
//                        if (diff > mLimit) {
//
//                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
//                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
//                            boolean isNotContra = (mLastMatch != 1 - extType);
//
//                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
//                                Log.i(TAG, "step");
////                                for (StepListener stepListener : mStepListeners) {
////                                    stepListener.onStep();
////                                }
//                                stepListener.onStep();
//
//                                mLastMatch = extType;
//                            }
//                            else {
//                                mLastMatch = -1;
//                            }
//                        }
//                        mLastDiff[k] = diff;
//                    }
//                    mLastDirections[k] = direction;
//                    mLastValues[k] = v;
//                }
//            }
//        }
//    }
public void SimpleStep(int simpleSteps, float distance) {
//    EventDispatcher.dispatchEvent(this, "SimpleStep", simpleSteps, distance);
  //  Toast.makeText(this, "SimpleStep", Toast.LENGTH_LONG).show();
}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        float[] values = event.values;
        // Check if the middle reading within the current window represents
        // a peak/valley.
        if (startPeaking) {
            getPeak();
            getValley();
        }
        // Find largest peak-valley range amongst the three
        // accelerometer axis
        int argmax = prevDiff[0] > prevDiff[1] ? 0 : 1;
        argmax = prevDiff[2] > prevDiff[argmax] ? 2 : argmax;
        // Process each of the X, Y and Z accelerometer axis values
        for (int k = 0; k < DIMENSIONS; k++) {
            // Peak is detected
            if (startPeaking && peak[k] >= 0) {
                if (foundValley[k] &&
                        lastValues[peak[k]][k] - lastValley[k] > PEAK_VALLEY_RANGE) {
                    // Step detected on axis k with maximum peak-valley range.
                    if (argmax == k) {
                        long timestamp = System.currentTimeMillis();
                        stepInterval[intervalPos] = timestamp - stepTimestamp;
                        intervalPos = (intervalPos + 1) % NUM_INTERVALS;
                        stepTimestamp = timestamp;
                        if (areStepsEquallySpaced()) {
                            if (foundNonStep) {
                                numStepsWithFilter += NUM_INTERVALS;
                                if (!gpsAvailable) {
                                    totalDistance += strideLength * NUM_INTERVALS;
                                }
                                foundNonStep = false;
                            }
                            numStepsWithFilter++;
                            stepListener.onStep();
                            //WalkStep(numStepsWithFilter, totalDistance);
                            if (!gpsAvailable) {
                                totalDistance += strideLength;
                            }
                        } else {
                            foundNonStep = true;
                        }
                        numStepsRaw++;
                       // stepListener.onStep();
                       // SimpleStep(numStepsRaw, totalDistance);
                        if (!statusMoving) {
                            statusMoving = true;
                         //   StartedMoving();
                        }
                    }
                    foundValley[k] = false;
                    prevDiff[k] = lastValues[peak[k]][k] - lastValley[k];
                } else {
                    prevDiff[k] = 0;
                }
            }
            // Valley is detected
            if (startPeaking && valley[k] >= 0) {
                foundValley[k] = true;
                lastValley[k] = lastValues[valley[k]][k];
            }
            // Store latest accelerometer reading in the window.
            lastValues[winPos][k] = values[k];
        }
        elapsedTimestamp = System.currentTimeMillis();
        if (elapsedTimestamp - stepTimestamp > stopDetectionTimeout) {
            if (statusMoving) {
                statusMoving = false;
              //  StoppedMoving();
            }
            stepTimestamp = elapsedTimestamp;
        }
        // Force inequality with previous value. This helps with better
        // peak/valley detection.
        int prev = winPos - 1 < 0 ? WIN_SIZE - 1 : winPos - 1;
        for (int k = 0; k < DIMENSIONS; k++) {
            if (lastValues[prev][k] == lastValues[winPos][k]) {
                lastValues[winPos][k] += 0.001;
            }
        }
        // Once the buffer is full, start peak/valley detection.
        if (winPos == WIN_SIZE - 1 && !startPeaking) {
            startPeaking = true;
        }
        // Increment position within the window.
        winPos = (winPos + 1) % WIN_SIZE;
    }


    /**
     * Finds average of the last NUM_INTERVALS number of step intervals
     * and checks if they are roughly equally spaced.
     */
    private boolean areStepsEquallySpaced() {
        float avg = 0;
        int num = 0;
        for (long interval : stepInterval) {
            if (interval > 0) {
                num++;
                avg += interval;
            }
        }
        avg = avg / num;
        for (long interval : stepInterval) {
            if (Math.abs(interval - avg) > INTERVAL_VARIATION) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the current middle of the window is the local peak.
     * TODO(user): Combine getPeak and getValley into one method.
     */
    private void getPeak() {
        int mid = (winPos + WIN_SIZE / 2) % WIN_SIZE;
        for (int k = 0; k < DIMENSIONS; k++) {
            peak[k] = mid;
            for (int i = 0; i < WIN_SIZE; i++) {
                if (i != mid && lastValues[i][k] >= lastValues[mid][k]) {
                    peak[k] = -1;
                    break;
                }
            }
        }
    }

    /**
     * Checks if the current middle of the window is the local valley.
     */
    private void getValley() {
        int mid = (winPos + WIN_SIZE / 2) % WIN_SIZE;
        for (int k = 0; k < DIMENSIONS; k++) {
            valley[k] = mid;
            for (int i = 0; i < WIN_SIZE; i++) {
                if (i != mid && lastValues[i][k] <= lastValues[mid][k]) {
                    valley[k] = -1;
                    break;
                }
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

}

