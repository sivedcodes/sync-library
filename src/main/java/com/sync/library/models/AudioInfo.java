package com.sync.library.models;

import java.util.Objects;

public class AudioInfo {
    private String ringerMode;
    private int mediaVolume;
    private int alarmVolume;
    private int ringVolume;
    private int notificationVolume;
    private boolean isDnd;
    private String supportedCodecs;

    public AudioInfo(String ringerMode, int mediaVolume, int alarmVolume, int ringVolume,
                     int notificationVolume, boolean isDnd, String supportedCodecs) {
        this.ringerMode = ringerMode;
        this.mediaVolume = mediaVolume;
        this.alarmVolume = alarmVolume;
        this.ringVolume = ringVolume;
        this.notificationVolume = notificationVolume;
        this.isDnd = isDnd;
        this.supportedCodecs = supportedCodecs;
    }

    public String getRingerMode() { return ringerMode; }
    public int getMediaVolume() { return mediaVolume; }
    public int getAlarmVolume() { return alarmVolume; }
    public int getRingVolume() { return ringVolume; }
    public int getNotificationVolume() { return notificationVolume; }
    public boolean isDnd() { return isDnd; }
    public String getSupportedCodecs() { return supportedCodecs; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AudioInfo audioInfo = (AudioInfo) o;
        return mediaVolume == audioInfo.mediaVolume && alarmVolume == audioInfo.alarmVolume
                && ringVolume == audioInfo.ringVolume && notificationVolume == audioInfo.notificationVolume
                && isDnd == audioInfo.isDnd && Objects.equals(ringerMode, audioInfo.ringerMode)
                && Objects.equals(supportedCodecs, audioInfo.supportedCodecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ringerMode, mediaVolume, alarmVolume, ringVolume, notificationVolume, isDnd, supportedCodecs);
    }

    @Override
    public String toString() {
        return "AudioInfo{ringerMode='" + ringerMode + "', mediaVolume=" + mediaVolume
                + ", alarmVolume=" + alarmVolume + ", ringVolume=" + ringVolume
                + ", notificationVolume=" + notificationVolume + ", isDnd=" + isDnd
                + ", supportedCodecs='" + supportedCodecs + "'}";
    }
}
