package com.example.android.idea;
import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Utilities {

    public static final String EXTRAS_IDEA_FILENAME = "EXTRAS_IDEA_FILENAME";
    public static final String FILE_EXTENSION = ".bin";

    public static boolean saveIdea(Context context, Idea idea) {

        String fileName = String.valueOf(idea.getDateTime()) + FILE_EXTENSION;

        FileOutputStream fos;
        ObjectOutputStream oos;

        try{
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(idea);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    static ArrayList<Idea> getAllSavedIdeas(Context context) {
        ArrayList<Idea> ideas = new ArrayList<>();

        File filesDir = context.getFilesDir();
        ArrayList<String> ideaFiles = new ArrayList<>();

        for(String file : filesDir.list()) {
            if(file.endsWith(FILE_EXTENSION)) {
                ideaFiles.add(file);
            }
        }

        FileInputStream fis;
        ObjectInputStream ois;

        for (int i = 0; i < ideaFiles.size(); i++) {
            try{
                fis = context.openFileInput(ideaFiles.get(i));
                ois = new ObjectInputStream(fis);

                ideas.add((Idea) ois.readObject());
                fis.close();
                ois.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }

        return ideas;
    }

    public static Idea getIdeaByFileName(Context context, String fileName) {

        File file = new File(context.getFilesDir(), fileName);
        if(file.exists() && !file.isDirectory()) {

            Log.v("UTILITIES", "File exist = " + fileName);

            FileInputStream fis;
            ObjectInputStream ois;

            try {
                fis = context.openFileInput(fileName);
                ois = new ObjectInputStream(fis);
                Idea note = (Idea) ois.readObject();
                fis.close();
                ois.close();

                return note;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            return null;
        }
    }

    public static boolean deleteFile(Context context, String fileName) {
        File dirFiles = context.getFilesDir();
        File file = new File(dirFiles, fileName);

        if(file.exists() && !file.isDirectory()) {
            return file.delete();
        }

        return false;
    }
}

