package de.lemaik.chunky.denoiser;

import se.llbit.chunky.renderer.DefaultRenderManager;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.log.Log;
import se.llbit.util.TaskTracker;

import java.util.Arrays;

public class DenoisedPathTracingPreviewRenderer extends MultiPassRenderer {
    public static final int PREVIEW_SPP = 32;

    protected final DenoiserSettings settings;
    protected final Denoiser denoiser;

    protected final String id;
    protected final String name;
    protected final String description;
    protected final RayTracer tracer;

    protected final AlbedoTracer albedoTracer = new AlbedoTracer();
    protected final NormalTracer normalTracer;

    public DenoisedPathTracingPreviewRenderer(DenoiserSettings settings, Denoiser denoiser,
                                              String id, String name, String description, RayTracer tracer) {
        this.settings = settings;
        this.denoiser = denoiser;

        this.normalTracer = new NormalTracer(settings);

        this.id = id;
        this.name = name;
        this.description = description;
        this.tracer = tracer;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void render(DefaultRenderManager manager) throws InterruptedException {
        TaskTracker.Task task = manager.getRenderTask();
        task.update("Preview", PREVIEW_SPP, 0, "");

        Scene scene = manager.bufferedScene;
        double[] sampleBuffer = scene.getSampleBuffer();

        RayTracer[] tracers = new RayTracer[] {tracer, albedoTracer, normalTracer};
        float[][] buffers = new float[][] {
                new float[sampleBuffer.length],
                settings.renderAlbedo.get() ? new float[sampleBuffer.length] : null,
                settings.renderNormal.get() ? new float[sampleBuffer.length] : null,
        };
        boolean[] tracerMask = new boolean[3];

        for (int spp = 0; spp < PREVIEW_SPP; spp++) {
            tracerMask[0] = true;
            tracerMask[1] = settings.renderAlbedo.get() && spp < settings.albedoSpp.get();
            tracerMask[2] = settings.renderNormal.get() && spp < settings.normalSpp.get();
            renderPass(manager, manager.context.sppPerPass(), tracers, buffers, tracerMask);

            try {
                float[] denoised = denoiser.denoise(scene.width, scene.height, buffers[0],
                        buffers[1], buffers[2]);
                Arrays.setAll(sampleBuffer, i -> (double) denoised[i]);
            } catch (Denoiser.DenoisingFailedException e) {
                Log.error("Failed to denoise", e);
                break;
            }

            task.update(spp+1);
            if (postRender.getAsBoolean()) break;
        }
    }
}
