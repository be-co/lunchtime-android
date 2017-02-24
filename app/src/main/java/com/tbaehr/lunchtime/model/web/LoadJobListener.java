package com.tbaehr.lunchtime.model.web;

/**
 * Created by timo.baehr@gmail.com on 09.02.17.
 */
public interface LoadJobListener<T> {
    void onDownloadStarted();

    void onDownloadFailed(String message);

    void onDownloadFinished(T downloadedObject);
}