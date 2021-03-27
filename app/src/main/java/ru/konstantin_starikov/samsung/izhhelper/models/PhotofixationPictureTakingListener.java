package ru.konstantin_starikov.samsung.izhhelper.models;

public interface PhotofixationPictureTakingListener {
    void saveTakedPicture(String picturePath);
    void onPhotofixationFinished(String picturePath);
}
