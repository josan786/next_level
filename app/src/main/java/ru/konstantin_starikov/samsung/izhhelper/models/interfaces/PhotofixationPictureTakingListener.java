package ru.konstantin_starikov.samsung.izhhelper.models.interfaces;

public interface PhotofixationPictureTakingListener {
    void saveTakedPicture(String picturePath);
    void onPhotofixationFinished(String picturePath);
}
