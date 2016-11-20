package tbs.doblon.io;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class GameBase extends ApplicationAdapter {
    private static final Color bg = new Color(0x8c93b2ff);
    public static int screenWidth, screenHeight;
    private static Level level;
    private static SpriteBatch spriteBatch;
    private static ShapeRenderer shapeRenderer;

    private static boolean isDisposeCalled = true;
    private static long lastUpdate = System.currentTimeMillis();
    public static long delta;
    public static ShapeRenderer.ShapeType line = ShapeRenderer.ShapeType.Line;
    public static ShapeRenderer.ShapeType fill = ShapeRenderer.ShapeType.Filled;

    public static SpriteBatch spriteBatch() {
        if (shapeRenderer.isDrawing())
            shapeRenderer.end();

        if (!spriteBatch.isDrawing())
            spriteBatch.begin();

        return spriteBatch;
    }

    public static ShapeRenderer shapeRenderer(ShapeRenderer.ShapeType shapeType) {
        if (spriteBatch.isDrawing())
            spriteBatch.end();

        if (!shapeRenderer.isDrawing())
            shapeRenderer.begin(shapeType);
        else {
            if (shapeType != shapeRenderer.getCurrentType()) {
                shapeRenderer.end();
                shapeRenderer.begin(shapeType);
            }
        }

        return shapeRenderer;
    }

    @Override
    public void create() {
        resume();
        super.create();
    }

    @Override
    public void resume() {
        initDisposable();
        super.resume();
    }

    private static void initDisposable() {
        if (!isDisposeCalled)
            return;
        try {
            Utility.dispose(spriteBatch);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utility.getFont();
        //Todo read and write data
        //Todo fonts (3), sprite sheet
        spriteBatch = new SpriteBatch(6);
        shapeRenderer = new ShapeRenderer();
        BitmapLoader.init();
        GameController.init();
        isDisposeCalled = false;
    }

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        if (level == null) {
            level = new Level();
        }

        super.resize(width, height);
    }

    @Override
    public void render() {
        delta = (System.currentTimeMillis() - lastUpdate);
        lastUpdate = System.currentTimeMillis();
        clear();
        update();
        onDraw();
        drawFPS();
    }

    protected static void clear() {
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
    }

    @Override
    public void pause() {
        //Todo
        super.pause();
    }

    public static void update() {
        try {
            level.update(delta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void onDraw();

    @Override
    public void dispose() {
        isDisposeCalled = true;
        //Todo
        try {
            Utility.disposeFont();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BitmapLoader.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Utility.dispose(spriteBatch);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Utility.dispose(shapeRenderer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.dispose();
    }

    public static void enableAlpha() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableAlpha() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    void drawFPS() {
        final String fps = "fps: " + Gdx.graphics.getFramesPerSecond();
        final float scale = Utility.getScale(50);
        final GlyphLayout layout = Utility.measureText(fps, scale);
        float w = layout.width, h = layout.height, x = (GameBase.screenWidth * 0.95f) - (w * 0.5f), y = (GameBase.screenHeight * 0.95f) - (h * 0.5f);

        shapeRenderer(fill);
        enableAlpha();
        shapeRenderer.setColor(0, 0, 0, 0.1f);
        shapeRenderer.rect(x - ((w * 1.1f) / 2), y - ((h * 1.1f) / 2), (w * 1.1f), (h * 1.1f));
        Utility.drawCenteredText(spriteBatch(), 0xffffffff, fps, x, y, scale);
        disableAlpha();

    }


}
