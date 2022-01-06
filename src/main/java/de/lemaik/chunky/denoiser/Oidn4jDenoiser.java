package de.lemaik.chunky.denoiser;

import net.time4tea.oidn.Oidn;
import net.time4tea.oidn.OidnDevice;
import net.time4tea.oidn.OidnFilter;

import java.nio.FloatBuffer;

public class Oidn4jDenoiser implements Denoiser {
    private final Oidn denoiser = new Oidn();

    @Override
    public float[] denoise(int width, int height, float[] beauty, float[] albedo, float[] normal) {
        FloatBuffer beautyBuffer = FloatBuffer.wrap(beauty);
        FloatBuffer albedoBuffer = FloatBuffer.wrap(albedo);
        FloatBuffer normalBuffer = FloatBuffer.wrap(normal);
        FloatBuffer outputBuffer = FloatBuffer.allocate(beauty.length);

        try (OidnDevice device = denoiser.newDevice(Oidn.DeviceType.DEVICE_TYPE_DEFAULT);
             OidnFilter filter = device.raytraceFilter()) {
            filter.setAdditionalImages(albedoBuffer, normalBuffer, width, height);
            filter.setFilterImage(beautyBuffer, outputBuffer, width, height);

            filter.commit();
            filter.execute();
            device.error();
        }

        return outputBuffer.array();
    }
}
