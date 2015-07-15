package com.udacity.jcmb.popularmovies.db;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.udacity.jcmb.popularmovies.model.Movie;

import java.io.File;

/**
 * @author Julio Mendoza on 7/15/15.
 * @see <a href="http://ormlite.com/android/examples/">ORMLite Examples</a>
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final String USER_DIR = "user.dir";
    private static final String RAW_FOLDER_LOCATION = "/app/src/main/res/raw/";
    private static final String FILE_NAME = "ormlite_config.txt";

    private static final Class<?>[] classes = new Class[]{Movie.class};

    public static void main(String[] args) throws Exception {

        boolean created;

        String path = System.getProperty(USER_DIR, null);

        path += RAW_FOLDER_LOCATION;

        System.out.println(path);

        File f = new File(path + FILE_NAME);

        if (f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }

        created = f.createNewFile();
        if (created) {
            writeConfigFile(f, classes);
        }
    }
}
