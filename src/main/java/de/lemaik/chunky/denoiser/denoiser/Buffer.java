package de.lemaik.chunky.denoiser.denoiser;

import com.sun.jna.Pointer;

public class Buffer {
    private final OidnJna.OidnLibrary library;
    private final Device device;
    private final long size;
    public final Pointer buffer;

    public Buffer(OidnJna.OidnLibrary library, Device device, long byteSize) {
        this.library = library;
        this.device = device;
        this.size = byteSize;
        buffer = library.oidnNewBuffer(device.device, new OidnJna.size_t(byteSize));
    }

    public Buffer(OidnJna.OidnLibrary library, Device device, Pointer ptr, long byteSize) {
        this.library = library;
        this.device = device;
        this.size = byteSize;
        buffer = library.oidnNewSharedBuffer(device.device, ptr, new OidnJna.size_t(byteSize));
    }

    public float[] readBuffer() {
        Pointer ptr = library.oidnMapBuffer(buffer, 0, new OidnJna.size_t(0), new OidnJna.size_t(size));
        float[] out = new float[(int) (size / 4)];
        System.arraycopy(ptr.getFloatArray(0, out.length), 0,out, 0, out.length);
        library.oidnUnmapBuffer(buffer, ptr);
        return out;
    }

    public void releaseBuffer() {
        library.oidnReleaseBuffer(buffer);
    }

    public void retainBuffer() {
        library.oidnRetainBuffer(buffer);
    }
}
