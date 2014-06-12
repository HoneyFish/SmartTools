/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nj.simba.page.logcat;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.Log;
import com.android.ddmlib.Log.LogLevel;
import com.android.ddmlib.MultiLineReceiver;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to monitor a device for logcat messages. It stores the received
 * log messages in a circular buffer.
 */
public final class LogCatReceiver {
    private static final String LOGCAT_COMMAND = "logcat -v long ";
    public static final int LOGCAT_MAIN   = 0;
    public static final int LOGCAT_SYSTEM = 1;
    public static final int LOGCAT_EVENT  = 2;
    public static final int LOGCAT_RADIO  = 3;
    
    private static final int DEVICE_POLL_INTERVAL_MSEC = 1000;
    private static LogCatMessage DEVICE_DISCONNECTED_MESSAGE =
            new LogCatMessage(LogLevel.ERROR, "", "", "",
                    "", "", "Device disconnected");

    private LogCatMessageList mLogMessages;
    private IDevice mCurrentDevice;
    private LogCatOutputReceiver mCurrentLogCatOutputReceiver;
    private Set<ILogCatBufferChangeListener> mLogCatMessageListeners;
    private LogCatMessageParser mLogCatMessageParser;
    private LogCatPidToNameMapper mPidToNameMapper;
    private String mCmdSuffix;

    /**
     * Construct a LogCat message receiver for provided device. This will launch a
     * logcat command on the device, and monitor the output of that command in
     * a separate thread. All logcat messages are then stored in a circular
     * buffer, which can be retrieved using {@link LogCatReceiver#getMessages()}.
     * @param device device to monitor for logcat messages
     * @param prefStore
     */
    public LogCatReceiver(IDevice device, int log) {
        mCurrentDevice = device;
        
        if (log == LOGCAT_MAIN ) {
            mCmdSuffix=""; 
        } else if (log == LOGCAT_SYSTEM) {
            mCmdSuffix="-b system";
        } else if (log == LOGCAT_EVENT) {
            mCmdSuffix="-b events";
        } else if (log == LOGCAT_RADIO) {
            mCmdSuffix="-b radio";
        }
      
        mLogCatMessageListeners = new HashSet<ILogCatBufferChangeListener>();
        mLogCatMessageParser = new LogCatMessageParser();
        mPidToNameMapper = new LogCatPidToNameMapper(mCurrentDevice);

        mLogMessages = new LogCatMessageList(getFifoSize());

        startReceiverThread();
    }

    /**
     * Stop receiving messages from currently active device.
     */
    public void stop() {
        if (mCurrentLogCatOutputReceiver != null) {
            /* stop the current logcat command */
            mCurrentLogCatOutputReceiver.mIsCancelled = true;
            mCurrentLogCatOutputReceiver = null;

            // add a message to the log indicating that the device has been disconnected.
            processLogMessages(Collections.singletonList(DEVICE_DISCONNECTED_MESSAGE));
        }

        mCurrentDevice = null;
    }

    private int getFifoSize() {
        int n = 0;//mPrefStore.getInt(LogCatMessageList.MAX_MESSAGES_PREFKEY);
        return n == 0 ? LogCatMessageList.MAX_MESSAGES_DEFAULT : n;
    }

    private void startReceiverThread() {
        mCurrentLogCatOutputReceiver = new LogCatOutputReceiver();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                /* wait while the device comes online */
                while (mCurrentDevice != null && !mCurrentDevice.isOnline()) {
                    try {
                        Thread.sleep(DEVICE_POLL_INTERVAL_MSEC);
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                try {
                    if (mCurrentDevice != null) {
                        System.out.println(LOGCAT_COMMAND+mCmdSuffix);
                        mCurrentDevice.executeShellCommand(LOGCAT_COMMAND+mCmdSuffix,
                            mCurrentLogCatOutputReceiver, 0);
                    }
                } catch (Exception e) {
                    /* There are 4 possible exceptions: TimeoutException,
                     * AdbCommandRejectedException, ShellCommandUnresponsiveException and
                     * IOException. In case of any of them, the only recourse is to just
                     * log this unexpected situation and move on.
                     */
                    Log.e("Unexpected error while launching logcat. Try reselecting the device.",
                            e);
                }
            }
        });
        t.setName("LogCat output receiver for " + mCurrentDevice.getSerialNumber());
        t.start();
    }

    /**
     * LogCatOutputReceiver implements {@link MultiLineReceiver#processNewLines(String[])},
     * which is called whenever there is output from logcat. It simply redirects this output
     * to {@link LogCatReceiver#processLogLines(String[])}. This class is expected to be
     * used from a different thread, and the only way to stop that thread is by using the
     * {@link LogCatOutputReceiver#mIsCancelled} variable.
     * See {@link IDevice#executeShellCommand(String, IShellOutputReceiver, int)} for more
     * details.
     */
    private class LogCatOutputReceiver extends MultiLineReceiver {
        private boolean mIsCancelled;

        public LogCatOutputReceiver() {
            setTrimLine(false);
        }

        /** Implements {@link IShellOutputReceiver#isCancelled() }. */
        @Override
        public boolean isCancelled() {
            return mIsCancelled;
        }

        @Override
        public void processNewLines(String[] lines) {
            if (!mIsCancelled) {
                processLogLines(lines);
            }
        }
    }

    private void processLogLines(String[] lines) {
        List<LogCatMessage> newMessages = mLogCatMessageParser.processLogLines(lines,
                mPidToNameMapper);
        processLogMessages(newMessages);
    }

    private void processLogMessages(List<LogCatMessage> newMessages) {
        if (newMessages.size() > 0) {
            List<LogCatMessage> deletedMessages;
            synchronized (mLogMessages) {
                deletedMessages = mLogMessages.ensureSpace(newMessages.size());
                mLogMessages.appendMessages(newMessages);
            }
            sendLogChangedEvent(newMessages, deletedMessages);
        }
    }

    /**
     * Get the list of logcat messages received from currently active device.
     * @return list of messages if currently listening, null otherwise
     */
    public LogCatMessageList getMessages() {
        return mLogMessages;
    }

    /**
     * Clear the list of messages received from the currently active device.
     */
    public void clearMessages() {
        mLogMessages.clear();
    }

    /**
     * Add to list of message event listeners.
     * @param l listener to notified when messages are received from the device
     */
    public void addMessageReceivedEventListener(ILogCatBufferChangeListener l) {
        mLogCatMessageListeners.add(l);
    }

    public void removeMessageReceivedEventListener(ILogCatBufferChangeListener l) {
        mLogCatMessageListeners.remove(l);
    }
    
    public void removeAllMessageReceivedEventListener() {
        mLogCatMessageListeners.clear();
    }

    private void sendLogChangedEvent(List<LogCatMessage> addedMessages,
            List<LogCatMessage> deletedMessages) {
        for (ILogCatBufferChangeListener l : mLogCatMessageListeners) {
            l.bufferChanged(addedMessages, deletedMessages);
        }
    }

    /**
     * Resize the internal FIFO.
     * @param size new size
     */
    public void resizeFifo(int size) {
        mLogMessages.resize(size);
    }
}
