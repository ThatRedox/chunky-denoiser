package de.lemaik.chunky.denoiser.denoiser;

import com.sun.jna.Pointer;

public class Filter {
    private final OidnJna.OidnLibrary library;
    private final Device device;
    public final Pointer filter;

    public Filter(OidnJna.OidnLibrary library, Device device, String type) {
        this.library = library;
        this.device = device;
        filter = library.oidnNewFilter(device.device, type);
    }

    public void setFilterImage(String name, Buffer image, int width, int height) {
        OidnJna.size_t zero = new OidnJna.size_t(0);
        library.oidnSetFilterImage(filter, name, image.buffer, 3,
                new OidnJna.size_t(width), new OidnJna.size_t(height),
                zero, zero, zero);
    }

    public void setSharedFilterImage(String name, Pointer ptr, int width, int height) {
        OidnJna.size_t zero = new OidnJna.size_t(0);
        library.oidnSetSharedFilterImage(filter, name, ptr, 3,
                new OidnJna.size_t(width), new OidnJna.size_t(height),
                zero, zero, zero);
    }

    public void releaseFilter() {
        library.oidnReleaseFilter(filter);
    }

    public void retainFilter() {
        library.oidnRetainFilter(filter);
    }
}
