package ru.konstantin_starikov.samsung.izhhelper.models;

import java.util.Arrays;
import java.util.List;

public class PhotosNamesCompressor {
    public static String compress(List<String> photosNames)
    {
        if(photosNames.isEmpty()) return null;
        String result ="";
        for(int i = 0; i < photosNames.size() - 1; i++)
        {
            result += photosNames.get(i);
            result += " ";
        }
        result += photosNames.get(photosNames.size() - 1);
        return result;
    }

    public static List<String> deCompress(String compressedPhotosNames)
    {
        List<String> result = Arrays.asList(compressedPhotosNames.split(" "));
        return result;
    }
}
