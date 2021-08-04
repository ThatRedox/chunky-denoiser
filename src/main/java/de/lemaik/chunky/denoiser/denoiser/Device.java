package de.lemaik.chunky.denoiser.denoiser;

import com.sun.jna.Pointer;

public class Device {
    private final OidnJna.OidnLibrary library;
    public final Pointer device;

    public Device(OidnJna.OidnLibrary library, DeviceType type) {
        this.library = library;
        device = library.oidnNewDevice(type.mapping);
    }

    public Device(OidnJna.OidnLibrary library) {
        this(library, DeviceType.OIDN_DEVICE_TYPE_DEFAULT);
    }

    public int getVersion() {
        return library.oidnGetDevice1i(device, "version");
    }

    public int getVersionMajor() {
        return library.oidnGetDevice1i(device, "versionMajor");
    }

    public int getVersionMinor() {
        return library.oidnGetDevice1i(device, "versionMinor");
    }

    public int getVersionPatch() {
        return library.oidnGetDevice1i(device, "versionPatch");
    }

    public int getVerbose() {
        return library.oidnGetDevice1i(device, "verbose");
    }

    public void setVerbose(int level) {
        library.oidnSetDevice1i(device, "verbose", level);
    }

    public int getNumThreads() {
        return library.oidnGetDevice1i(device, "numThreads");
    }

    public void setNumThreads(int threads) {
        library.oidnSetDevice1i(device, "numThreads", threads);
    }

    public boolean getAffinity() {
        return library.oidnGetDevice1b(device, "setAffinity");
    }

    public void setAffinity(boolean affinity) {
        library.oidnSetDevice1b(device, "setAffinity", affinity);
    }

    public void commitDevice() {
        library.oidnCommitDevice(device);
    }

    public void release() {
        library.oidnReleaseDevice(device);
    }

    public void retain() {
        library.oidnReleaseDevice(device);
    }

    public enum DeviceType {
        OIDN_DEVICE_TYPE_DEFAULT(0),
        OIDN_DEVICE_TYPE_CPU(1);

        public final int mapping;
        DeviceType(int mapping) {
            this.mapping = mapping;
        }
    }
}
