package com.sync.library.callbacks;

import com.sync.library.models.FileInfo;
import java.util.List;

public interface FileManagerCallback {
    void onResult(List<FileInfo> files);
    void onError(Exception e);
}
