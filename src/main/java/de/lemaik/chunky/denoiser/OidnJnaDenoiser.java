package de.lemaik.chunky.denoiser;

import com.sun.jna.Pointer;
import de.lemaik.chunky.oidn.OidnJna;
import de.lemaik.chunky.oidn.OpenImageDenoise;

public class OidnJnaDenoiser implements Denoiser {
    @Override
    public float[] denoise(int width, int height, float[] beauty, float[] albedo, float[] normal) throws DenoisingFailedException {
        OidnJna.Oidn library = OpenImageDenoise.get().library;
        float[] output;

        // Create a new device
        Pointer device = library.oidnNewDevice(OidnJna.OIDN_DEVICE_TYPE_DEFAULT);
        library.oidnCommitDevice(device);

        Pointer oidnOutputBuffer = library.oidnNewBuffer(device, new OidnJna.size_t(4L * beauty.length));
        Pointer oidnBeautyBuffer = allocateBuffer(library, device, beauty);
        Pointer oidnAlbedoBuffer = null;
        Pointer oidnNormalBuffer = null;

        // Create a ray tracing filter
        Pointer filter = library.oidnNewFilter(device, "RT");
        library.oidnSetFilter1b(filter, "hdr", true);

        library.oidnSetFilterImage(filter, "output", oidnOutputBuffer, OidnJna.OIDN_FORMAT_FLOAT3,
                new OidnJna.size_t(width), new OidnJna.size_t(height), OidnJna.ZERO, OidnJna.ZERO, OidnJna.ZERO);
        library.oidnSetFilterImage(filter, "color", oidnBeautyBuffer, OidnJna.OIDN_FORMAT_FLOAT3,
                new OidnJna.size_t(width), new OidnJna.size_t(height), OidnJna.ZERO, OidnJna.ZERO, OidnJna.ZERO);

        if (albedo != null) {
            oidnAlbedoBuffer = allocateBuffer(library, device, albedo);
            library.oidnSetFilterImage(filter, "albedo", oidnAlbedoBuffer, OidnJna.OIDN_FORMAT_FLOAT3,
                    new OidnJna.size_t(width), new OidnJna.size_t(height), OidnJna.ZERO, OidnJna.ZERO, OidnJna.ZERO);
        }
        if (normal != null) {
            oidnNormalBuffer = allocateBuffer(library, device, normal);
            library.oidnSetFilterImage(filter, "normal", oidnNormalBuffer, OidnJna.OIDN_FORMAT_FLOAT3,
                    new OidnJna.size_t(width), new OidnJna.size_t(height), OidnJna.ZERO, OidnJna.ZERO, OidnJna.ZERO);
        }

        library.oidnCommitFilter(filter);
        library.oidnExecuteFilter(filter);

        Pointer outputBuffer = library.oidnMapBuffer(oidnOutputBuffer, OidnJna.OIDN_ACCESS_READ,
                OidnJna.ZERO, new OidnJna.size_t(4L * beauty.length));
        output = outputBuffer.getFloatArray(0, beauty.length);
        library.oidnUnmapBuffer(oidnOutputBuffer, outputBuffer);

        library.oidnReleaseFilter(filter);
        library.oidnReleaseBuffer(oidnOutputBuffer);
        library.oidnReleaseBuffer(oidnBeautyBuffer);
        if (oidnAlbedoBuffer != null) library.oidnReleaseBuffer(oidnAlbedoBuffer);
        if (oidnNormalBuffer != null) library.oidnReleaseBuffer(oidnNormalBuffer);
        library.oidnReleaseDevice(device);

        return output;
    }

    private static Pointer allocateBuffer(OidnJna.Oidn library, Pointer device, float[] contents) {
        Pointer buffer = library.oidnNewBuffer(device, new OidnJna.size_t(4L * contents.length));
        Pointer mappedBuffer = library.oidnMapBuffer(buffer, OidnJna.OIDN_ACCESS_WRITE,
                OidnJna.ZERO, new OidnJna.size_t(4L * contents.length));
        mappedBuffer.write(0, contents, 0, contents.length);
        library.oidnUnmapBuffer(buffer, mappedBuffer);
        return buffer;
    }
}
