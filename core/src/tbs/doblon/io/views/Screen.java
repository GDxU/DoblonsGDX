package tbs.doblon.io.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

import tbs.doblon.io.Utility;
import tbs.doblon.io.ValueAnimator;

/**
 * Created by Michael on 1/28/2015.
 */
public abstract class Screen implements com.badlogic.gdx.Screen {
  /* Todo 'security find-identity -v -p codesigning' for iosSignIdentity

      robovm {
     Configure robovm
    iosSignIdentity = "LZU8BZAM9B.the.bigshots.lostplanet"
    iosProvisioningProfile = "path/to/profile"
    iosSkipSigning = false
    stdoutFifo = ""
    stderrFifo = ""
}
     packaging for any platform navigate to the root
     packaging desktop version>> gradlew desktop:dist
     packaging android version>> gradlew android:assembleRelease
     linux/ios chmod 755 gradlew
     packaging ios version>> gradlew ios:createIPA
     packaging web version>> gradlew html:dist
     TextureAtlas.AtlasRegion region = new TextureAtlas.AtlasRegion(colorTexture, x, y, width, height)
     bring for the following classes > Screen, Utility*/

    private static final ArrayList<ValueAnimator> animations = new ArrayList<ValueAnimator>(10);
    public static int w, h;
    protected static OrthographicCamera camera;
    private static SpriteBatch batch;
    private static ShapeRenderer renderer;

    public Screen() {
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(w, h);
        getDrawTeam();
        init();
    }


    protected static void clear() {
        Gdx.gl.glClearColor(.22f, .22f, .22f, 1);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
    }

    protected static void updateAnimations() {
        if (animations == null)
            return;

        for (int i = 0; i < animations.size(); i++) {
            animations.get(i).update();
        }
    }


    public static void print(String str) {
        System.out.println(str);
    }

    public static void log(String msg) {
        Gdx.app.error("LOG > ", msg);
    }

    private static void getDrawTeam() {
        disposeRenderers();

        try {
            batch = new SpriteBatch();
            try {
                batch.enableBlending();
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            renderer = new ShapeRenderer();
            try {
                renderer.setAutoShapeType(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void disposeRenderers() {
        try {
            batch.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            renderer.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addAnimator(ValueAnimator valueAnimator) {
        System.out.println("null? :" + (animations == null) + "  contaned? : " + animations.contains(valueAnimator));
        if (animations != null && !(animations.contains(valueAnimator)))
            animations.add(valueAnimator);
    }

    public static void removeAnimator(ValueAnimator valueAnimator) {
        if (animations != null && (animations.contains(valueAnimator)))
            animations.remove(valueAnimator);
    }

    public static ShapeRenderer getRenderer() {
        try {
            batch.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!renderer.isDrawing())
                renderer.begin(ShapeRenderer.ShapeType.Filled);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return renderer;
    }

    public static SpriteBatch getBatch() {
        try {
            renderer.end();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!batch.isDrawing())
                batch.begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batch;
    }

    public static OrthographicCamera getCamera() {
        return camera;
    }

    public abstract void update();

    public abstract void init();

    public abstract void onDraw();

    public abstract void getSprites();

    public abstract void disposeSprites();

    @Override
    public void render(float delta) {
        camera.update();
        updateAnimations();
        update();
        clear();
        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);
        onDraw();
        drawHUD();
    }

    @Override
    public void resize(int width, int height) {
        w = width;
        h = height;
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    public abstract void drawHUD();

    @Override
    public void show() {
        getDrawTeam();
        getSprites();
    }

    @Override
    public void dispose() {
        Utility.disposeFont();
        disposeRenderers();
        try {
            disposeSprites();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
