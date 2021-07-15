package ru.konstantin_starikov.samsung.izhhelper.models;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.models.databases.UsersDatabase;

public class Cleaner {
    public static void deleteUnusedFiles(Context context)
    {
        File[] allFiles = getAllFiles(context);
        ArrayList<String> allUsedFilenames = getAllUsedFilenames(context);
        for(File file : allFiles)
        {
            boolean isUsed = false;
            for(String usedFilename : allUsedFilenames)
            {
                if(file.getName().equals(usedFilename))
                {
                    isUsed = true;
                    break;
                }
            }
            if(!isUsed && isImage(file)) file.delete();
        }
    }

    private static File[] getAllFiles(Context context)
    {
       String ANDROID_DATA_DIR = context.getApplicationInfo().dataDir;
       File file = new File(ANDROID_DATA_DIR);
       return file.listFiles();
    }

    private static ArrayList<String> getAllUsedFilenames(Context context)
    {
        ArrayList<String> result = new ArrayList<String>();
        UsersDatabase usersDatabase;
        usersDatabase = new UsersDatabase(context);
        for(Account account : usersDatabase.selectAll())
        {
            result.add(account.getAvatarFilename());
            account.loadViolations(context);
            for(ViolationReport violationReport : account.getViolationReports())
            {
                result.add(violationReport.carNumberPhotoName);
                for(String photoName : violationReport.photosNames)
                {
                    result.add(photoName);
                }
            }
        }
        return result;
    }

    private static boolean isImage(File file)
    {
        boolean result = false;
        String fileExtension = getFileExtension(file);
        if(fileExtension.equals(".png")
                || fileExtension.equals(".jpg")
                || fileExtension.equals(".jpeg")
                || fileExtension.equals(".JPG")
                || fileExtension.equals(".JPEG")) result = true;
        return result;
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}
