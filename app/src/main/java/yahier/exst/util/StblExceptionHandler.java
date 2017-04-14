package yahier.exst.util;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import io.rong.common.RLog;

/**
 * Created by yahier on 17/1/22.
 */

public class StblExceptionHandler implements Thread.UncaughtExceptionHandler {
    Context mContext;

    public StblExceptionHandler(Context context) {
        this.mContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (ConfigControl.logable) {
                File file = new File(Config.localFilePathCrashLog);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File crashFile = new File(file, DateUtil.getDateYMDHM() + ".txt");
                crashFile.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
                PrintStream printStream = new PrintStream(fileOutputStream);
                ex.printStackTrace(printStream);
                printStream.close();
            }
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        } catch (SecurityException var9) {
            var9.printStackTrace();
        }

        RLog.e("StblExceptionHandler", "uncaughtException", ex);
        System.exit(2);
    }
}
