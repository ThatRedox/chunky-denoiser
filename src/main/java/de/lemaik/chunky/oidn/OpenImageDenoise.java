package de.lemaik.chunky.oidn;

import com.sun.jna.Native;

public class OpenImageDenoise {
    private static OpenImageDenoise INSTANCE = null;

    /**
     * Load the OpenImageDenoise native library.
     */
    public static void load(String path) {
        INSTANCE = new OpenImageDenoise(path);
    }

    public static OpenImageDenoise get() {
        if (INSTANCE == null)
            throw new IllegalStateException("Attempted to access OpenImageDenoise before library was loaded.");
        return INSTANCE;
    }

    /**
     * This is the raw library instance. Please use the wrapper classes where possible.
     */
    public final OidnJna.Oidn library;

    private OpenImageDenoise(String path) {
        this.library = Native.load(path, OidnJna.Oidn.class);
    }
}
