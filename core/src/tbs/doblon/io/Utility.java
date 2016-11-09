package tbs.doblon.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;

import java.util.Random;


public class Utility {
    //Todo make sure every app has its own preference name
    public static final Random rand = new Random();
    private static final Color c = new Color();
    private static final Color c2 = new Color();
    public static final Color tmpColor = new Color();
    private static final int[] ints = new int[2];
    private static final GlyphLayout glyphLayout = new GlyphLayout();
    private static final float[] textSize = new float[2];
    private static boolean isFontInit;
    private static BitmapFont font;
    private static final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "uniform mat4 u_projTrans;\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "\n"
            + "void main() {\n"
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "}\n";

    // Random in Range
    public static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static float randFloat(int minX, int maxX) {
        return rand.nextFloat() * (maxX - minX) + minX;
    }

    public static BitmapFont getFont() {

        if (!isFontInit || font == null) {
            font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
            isFontInit = true;
        }
        return font;
    }


    public static Preferences getPreferences() {
        return Gdx.app.getPreferences("prefs");
    }

    public static int getInt(String key) {
        return getPreferences().getInteger(key, 0);
    }

    public static void saveInt(String key, int value) {
        getPreferences().putInteger(key, value).flush();
    }

    public static String getString(String key) {
        return getPreferences().getString(key, "");
    }

    public static void saveString(String key, String value) {
        getPreferences().putString(key, value).flush();
    }

    public static void log(String log) {
        Gdx.app.log("doblons", log);
    }

    public static void drawCenteredText(SpriteBatch batch, int color, String text, float x, float y, float scale) {
        c.set(color);
        drawCenteredText(batch, c, text, x, y, scale);
    }

    public static GlyphLayout measureText(String text, float scale) {
        font.getData().setScale(scale);
        glyphLayout.setText(font, text);
        return glyphLayout;
    }

    public static void drawCenteredText(SpriteBatch batch, Color color, String text, float x, float y, float scale) {

        if (text == null || text.length() < 1) {
            return;
        }
//        font.getData().setScale(scale);
//        textToMeasure = "" + player.score;
//        glyphLayout.setText(font, textToMeasure);
//        color.set(0xFFFFFFFF);
        final BitmapFont font = getFont();
        font.getData().setScale(scale);

        glyphLayout.setText(font, text);
        final float textWidth = glyphLayout.width;
        final float left = x - (textWidth / 2);
        final float textHeight = font.getLineHeight();
        font.setColor(color);
        font.draw(batch, text, left, y + (textHeight / 2));
    }

    public static void drawLeftText(SpriteBatch batch, Color color, String text, float x, float y, float scale) {

        if (text == null || text.length() < 1) {
            return;
        }

        final BitmapFont font = getFont();
        font.getData().setScale(scale * 1.25f);
        font.setColor(color);
        font.draw(batch, text, x, y + font.getLineHeight());
    }

    public static boolean customBool(int i) {
        for (int ii = 0; ii < i; ii++) {
            if (!rand.nextBoolean()) {
                return false;
            }
        }
        return true;
    }

    public static void print(String s) {
        System.out.println(s);
    }

    public static void openLink(String link) {
        Gdx.net.openURI(link);
    }

    public static String formatNumber(int i) {
//  Todo      return NumberFormat.getIntegerInstance().format(i);
        return String.valueOf(i);
    }

    public static float getScale(float textHeight) {
        return textHeight / 192f;
    }

    public static void disposeFont() {
        isFontInit = false;
        try {
            font.dispose();
        } catch (Exception e) {
        }
        font = null;
    }

    public static void dispose(Object o) {
        if (o != null && o instanceof Disposable) {
            try {
                ((Disposable) o).dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        else {
//            Gdx.app.error("Util: ", "Not dosposable");
//        }
    }

    public static Texture getTexture(String name) {
        return new Texture(Gdx.files.internal(name));
    }

    public static ShaderProgram getCircleViewShaderProgram(int light, int dark) {
        //todo rewrite
        c.set(light);
        c2.set(dark);
        final String fragmentShader = "#ifdef GL_ES\n"
                + "#define LOWP lowp\n"
                + "precision mediump float;\n"
                + "#else\n"
                + "#define LOWP \n"
                + "#endif\n"
                + "varying LOWP vec4 v_color;\n"
                + "varying vec2 v_texCoords;\n"
                + "uniform sampler2D u_texture;\n"
                + "void main() {\n"
                + "  vec4 v_c = v_color * texture2D(u_texture, v_texCoords);\n"
                + "  if (v_c.a > 0.95) { \n"

                + "     if ((v_c.r + 0.3 > 0.5 && v_c.r - 0.3 < 0.5)" +
                "&& (v_c.g + 0.3 > 0.5 && v_c.g - 0.3 < 0.5)" +
                "&& (v_c.b + 0.3 > 0.5 && v_c.b - 0.3 < 0.5)) { \n"
                + "           v_c.r =" + c.g + ";\n"
                + "           v_c.g = " + c.b + ";\n"
                + "           v_c.b = " + c.a + ";\n"
                + "         } \n"
                + "if ((v_c.r + 0.3 > 0.0 && v_c.r - 0.3 < 0.0)"
                + "&& (v_c.g + 0.3 > 0.0 && v_c.g - 0.3 < 0.0)"
                + "&& (v_c.b + 0.3 > 0.0 && v_c.b - 0.3 < 0.0)){\n"
                + "           v_c.r = " + c2.g + ";\n"
                + "           v_c.g = " + c2.b + ";\n"
                + "           v_c.b = " + c2.a + ";\n"
                + "         } \n"
                + "  } \n"
                + "   \n"
                + "  gl_FragColor = v_c;\n"
                + "}";

        final ShaderProgram p = new ShaderProgram(vertexShader, fragmentShader);
        if (!p.isCompiled()) {
            Gdx.app.error("getCarShaderProgram failed", p.getLog());
            dispose(p);
            return null;
        }
        return p;
    }

    public static float getDistance(float a, float b, float c, float d) {
        return (float) Math.sqrt((c -= a) * c + (d -= b) * d);
    }

    public static float getDirection(float x1, float y1, float x2, float y2) {
        return (float) Math.atan2(y1 - y2, x1 - x2);
    }

    public static float toDegrees(double rad) {
        return (float) (rad * 180 / Math.PI);
    }

//    private static final String pattern = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx";

//    public static  String getUniqueID () {
//        final StringBuilder builder = new StringBuilder(36);
//        for (int i = 0; i < pattern.length(); i++) {
//
//        }
//    }
//
//    private static String subString(String a){
//        int b = randInt(0,16);
//        return ("x" == a ? b : b & 3 | 8).toString(16);
//    }
}
