package de.lemaik.chunky.denoiser;

import io.github.ThatRedox.OidnJna.Buffer;
import io.github.ThatRedox.OidnJna.Device;
import io.github.ThatRedox.OidnJna.Filter;
import io.github.ThatRedox.OidnJna.OpenImageDenoise;

public class OidnJnaDenoiser implements Denoiser {
    private final OpenImageDenoise denoiser;

    public OidnJnaDenoiser(OpenImageDenoise denoiser) {
        this.denoiser = denoiser;
    }

    @Override
    public float[] denoise(int width, int height, float[] beauty, float[] albedo, float[] normal) {
        float[] output;
        try (Device device = denoiser.createDevice()) {
            device.commit();

            Buffer outputBuffer = device.createBuffer(beauty.length);
            Buffer beautyBuffer = device.createBuffer(beauty.length);
            beautyBuffer.writeBuffer(beauty);

            Buffer albedoBuffer = null;
            Buffer normalBuffer = null;

            try (Filter filter = device.createFilter("RT")) {
                filter.setFilterParam("hdr", true);

                filter.setFilterImage("output", outputBuffer, width, height);
                filter.setFilterImage("color", beautyBuffer, width, height);

                if (albedo != null) {
                    albedoBuffer = device.createBuffer(albedo.length);
                    albedoBuffer.writeBuffer(albedo);
                    filter.setFilterImage("albedo", albedoBuffer, width, height);
                }
                if (normal != null) {
                    normalBuffer = device.createBuffer(normal.length);
                    normalBuffer.writeBuffer(normal);
                    filter.setFilterImage("normal", normalBuffer, width, height);
                }

                filter.commit();
                filter.execute();

                output = outputBuffer.readBuffer();
            }

            outputBuffer.close();
            beautyBuffer.close();
            if (albedoBuffer != null) albedoBuffer.close();
            if (normalBuffer != null) normalBuffer.close();
        }

        return output;
    }
}
