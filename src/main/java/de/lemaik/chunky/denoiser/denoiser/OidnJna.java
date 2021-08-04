package de.lemaik.chunky.denoiser.denoiser;

import com.sun.jna.*;

public class OidnJna {
    private static OidnLibrary INSTANCE = null;
    private OidnJna() {}

    public static OidnLibrary get(String path) {
        if (INSTANCE == null) INSTANCE = Native.load(path, OidnLibrary.class);
        return INSTANCE;
    }

    public static class size_t extends IntegerType {
        public size_t() { this(0); }
        public size_t(long value) { super(Native.SIZE_T_SIZE, value, true); }
    }

    public interface OIDNProgressMonitorFunction extends Callback {
        boolean callback(Pointer userPtr, double n);
    }

    public interface OidnLibrary extends Library {
        Pointer oidnNewDevice(int type);
        void oidnSetDevice1b(Pointer device, String name, boolean value);
        void oidnSetDevice1i(Pointer device, String name, int value);
        boolean oidnGetDevice1b(Pointer device, String name);
        int oidnGetDevice1i(Pointer device, String name);
        void oidnCommitDevice(Pointer device);
        void oidnReleaseDevice(Pointer device);
        void oidnRetainDevice(Pointer device);

        int oidnGetDeviceError(Pointer device, Pointer outMessage);

        Pointer oidnNewBuffer(Pointer device, size_t byteSize);
        Pointer oidnNewSharedBuffer(Pointer device, Pointer memory, size_t byteSize);
        Pointer oidnMapBuffer(Pointer buffer, int access, size_t byteoffset, size_t byteSize);
        void oidnUnmapBuffer(Pointer buffer, Pointer mappedPtr);
        void oidnRetainBuffer(Pointer buffer);
        void oidnReleaseBuffer(Pointer buffer);

        Pointer oidnNewFilter(Pointer device, String type);
        void oidnSetFilterImage(Pointer filter, String name, Pointer buffer, int format,
                                size_t width, size_t height, size_t byteOffset,
                                size_t bytePixelStride, size_t byteRowStride);
        void oidnSetSharedFilterImage(Pointer filter, String name, Pointer ptr, int format,
                                      size_t width, size_t height, size_t byteOffset,
                                      size_t bytePixelStride, size_t byteRowStride);
        void oidnRemoveFilterimage(Pointer filter, String name);
        void oidnSetSharedFilterData(Pointer filter, String name, Pointer ptr, size_t byteSize);
        void oidnUpdateFilterData(Pointer filter, String name);
        void oidnRemoveFilterData(Pointer filter, String name);
        void oidnSetFilter1b(Pointer filter, String name, boolean value);
        void oidnSetFilter1i(Pointer filter, String name, int value);
        void oidnSetFilter1f(Pointer filter, String name, float value);
        boolean oidnGetFilter1b(Pointer filter, String name);
        int oidnGetFilter1i(Pointer filter, String name);
        float oidnGetFilter1f(Pointer filter, String name);
        void oidnSetFilterProgressMonitorFunction(Pointer filter, OIDNProgressMonitorFunction func,
                                                  Pointer userPtr);
        void oidnCommitFilter(Pointer filter);
        void oidnExecuteFilter(Pointer filter);
        void oidnRetainFilter(Pointer filter);
        void oidnReleaseFilter(Pointer filter);
    }
}
