package org.autoride.autoride.listeners;

public interface PermissionListener {
    void onPermissionCheckCompleted(int requestCode, boolean isPermissionGranted);
}