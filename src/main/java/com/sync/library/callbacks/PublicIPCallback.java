package com.sync.library.callbacks;

public interface PublicIPCallback {
    void onResult(String ip);
    void onError(Exception e);
}
