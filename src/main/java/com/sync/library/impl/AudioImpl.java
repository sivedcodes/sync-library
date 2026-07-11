package com.sync.library.impl;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.app.NotificationManager;

import com.sync.library.models.AudioInfo;

import java.util.ArrayList;
import java.util.List;

public class AudioImpl {

    public static AudioInfo getAudioInfo(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        String ringerMode;
        if (audio == null) {
            ringerMode = "UNKNOWN";
        } else {
            switch (audio.getRingerMode()) {
                case AudioManager.RINGER_MODE_NORMAL:   ringerMode = "NORMAL";   break;
                case AudioManager.RINGER_MODE_SILENT:   ringerMode = "SILENT";   break;
                case AudioManager.RINGER_MODE_VIBRATE:  ringerMode = "VIBRATE";  break;
                default: ringerMode = "UNKNOWN";
            }
        }

        int mediaVolume = audio != null ? audio.getStreamVolume(AudioManager.STREAM_MUSIC) : 0;
        int alarmVolume = audio != null ? audio.getStreamVolume(AudioManager.STREAM_ALARM) : 0;
        int ringVolume = audio != null ? audio.getStreamVolume(AudioManager.STREAM_RING) : 0;
        int notificationVolume = audio != null ? audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION) : 0;

        boolean isDnd = false;
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isDnd = nm.isNotificationPolicyAccessGranted()
                    && nm.getCurrentInterruptionFilter() != NotificationManager.INTERRUPTION_FILTER_ALL;
        }

        String supportedCodecs = getSupportedAudioCodecs();

        return new AudioInfo(ringerMode, mediaVolume, alarmVolume, ringVolume,
                notificationVolume, isDnd, supportedCodecs);
    }

    private static String getSupportedAudioCodecs() {
        List<String> codecs = new ArrayList<>();
        MediaCodecList list = new MediaCodecList(MediaCodecList.ALL_CODECS);
        for (MediaCodecInfo info : list.getCodecInfos()) {
            if (!info.isEncoder()) {
                String[] types = info.getSupportedTypes();
                for (String type : types) {
                    if (type.startsWith("audio/")) {
                        String name = info.getName();
                        if (!codecs.contains(name)) {
                            codecs.add(name);
                        }
                    }
                }
            }
        }
        return String.join(", ", codecs);
    }
}
