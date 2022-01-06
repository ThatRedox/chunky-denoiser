package de.lemaik.chunky.denoiser;

import de.lemaik.chunky.oidn.OpenImageDenoise;
import se.llbit.chunky.Plugin;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ChunkyOptions;
import se.llbit.chunky.renderer.scene.PathTracer;
import se.llbit.chunky.ui.ChunkyFx;
import se.llbit.chunky.ui.render.RenderControlsTabTransformer;

/**
 * This plugin renders normal and albedo maps for use with image de-noisers.
 */
public class DenoiserPlugin implements Plugin {
    public static final String DENOISER_RENDERER_ID = "DenoiserPasses";

    @Override
    public void attach(Chunky chunky) {
        OpenImageDenoise.load("C:\\Users\\co2c6\\Documents\\oidn-1.3.0.x64.vc14.windows\\bin\\OpenImageDenoise.dll");

        DenoiserSettings settings = new DenoiserSettings();
//        Denoiser denoiser = new OidnBinaryDenoiser();
        Denoiser denoiser = new OidnJnaDenoiser();

        DenoisedPathTracingRenderer denoisedPathTracer = new DenoisedPathTracingRenderer(
                settings, denoiser,
                "DenoisedPathTracer",
                "DenoisedPathTracer",
                "DenoisedPathTracer",
                new PathTracer()
        );
        Chunky.addRenderer(denoisedPathTracer);

        DenoisedPathTracingPreviewRenderer denoisedPreview = new DenoisedPathTracingPreviewRenderer(
                settings, denoiser,
                "DenoisedPreview",
                "DenoisedPreview",
                "DenoisedPreview",
                new PathTracer()
        );
        Chunky.addPreviewRenderer(denoisedPreview);

        DenoiserPassRenderer inPlaceDenoisingRenderer = new DenoiserPassRenderer(
                settings, denoiser,
                DENOISER_RENDERER_ID,
                "DenoiserPasses",
                "Renders the denoiser passes."
        );
        Chunky.addRenderer(inPlaceDenoisingRenderer);

        RenderControlsTabTransformer prev = chunky.getRenderControlsTabTransformer();
        chunky.setRenderControlsTabTransformer(tabs -> {
            tabs = prev.apply(tabs);
            tabs.add(new DenoiserTabImpl(settings));
            return tabs;
        });
    }

    public static void main(String[] args) {
        // Start Chunky normally with this plugin attached.
        Chunky.loadDefaultTextures();
        Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
        new DenoiserPlugin().attach(chunky);
        ChunkyFx.startChunkyUI(chunky);
    }
}
