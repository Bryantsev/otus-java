package ru.otus.lesson9;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Alexander Bryantsev on 07.08.2019.
 */
class SyncPipe implements Runnable {

    private final InputStream is;
    private final OutputStream os;

    public SyncPipe(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    public void run() {
        try {
            final byte[] buffer = new byte[1024];
            for (int length = 0; (length = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}