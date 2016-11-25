package tbs.uilib.view.base_classes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Michael on 1/29/2015.
 */
public class Camera extends OrthographicCamera {
    //TODO OVERHAUL
    //Todo ** set a random zoom value when a button is clicked, then log the results , get the clicks to work at different scales *** Camera.getRefArea();
    protected static final float minZoom = 1, maxZoom = 3;
    public static Rectangle currentViewRect = new Rectangle();
    public static Camera camera;
    private static Rectangle ogArea, refArea;
    private static float camX, camY;

    public Camera(Rectangle area) {
        ogArea = area;
        camera = this;
        checkCameraPos();
    }


    public static Rectangle getOgArea() {
        return ogArea;
    }


    public static void setArea(Rectangle area) {
        Camera.refArea = area;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        if (refArea != null) {

//Todo            camX = (position.x - refArea.getX()) / refArea.getWidth();
//            camY = (position.y - refArea.getY()) / refArea.getHeight();

            camX = camX < 0 ? 0 : camX;
            camY = camY < 0 ? 0 : camY;

            camX = camX > 1 ? 1 : camX;
            camY = camY > 1 ? 1 : camY;

            zoom = zoom < minZoom ? minZoom : zoom;
            zoom = zoom > maxZoom ? maxZoom : zoom;
            this.zoom = zoom;
            System.out.println("setZoom :" + zoom);
        }
        checkCameraPos();
    }

    @Override
    public void update(boolean updateFrustum) {
        super.update(updateFrustum);
    }

    public void setPosition(float x, float y) {

        if (refArea == null) {
            checkCameraPos();
        } else {
            final float areaX = refArea.getX();
            final float areaY = refArea.getY();
            final float areaW = refArea.width;
            final float areaH = refArea.height;

            x = x + viewportWidth > areaX + areaW ? areaX + areaW - viewportWidth : x;
            x = x < areaX ? areaX : x;

            y = y + viewportHeight > areaY + areaW ? areaY + areaH - viewportHeight : y;
            y = y < areaY ? areaY : y;

            position.x = x;
            position.y = y;

            if (currentViewRect == null)
                currentViewRect = new Rectangle();

            currentViewRect.set(position.x - (viewportWidth / 2), position.y - (viewportHeight / 2), position.x + viewportWidth, viewportHeight);
            update();
        }
        final float scale = getScale();
    }

    public void checkCameraPos() {

        if (refArea == null) {
            refArea = new Rectangle();
            refArea.set(ogArea);
        }

        final float scale = getScale();
        float w = ogArea.width * scale;
        w = w < viewportWidth ? viewportWidth : w;
        float h = ogArea.height * scale;
        h = h < viewportHeight ? viewportHeight : h;

        final float x = position.x + (w * camX);
        final float y = position.y + (h * camY);

//        refArea.set(
//                ogArea.getX(),
//                ogArea.getY(),
//                width, height
//        );
        refArea.set(
                ogArea.getX() + (viewportWidth * (1f - scale) / 2),
                ogArea.getY() + (viewportHeight * (1f - scale) / 2),
                w, h
        );

        setPosition(x, y);

    }

    public float getScale() {
        return 1 / zoom;
    }

    public boolean isInFrustum(float x, float y) {
        return frustum.pointInFrustum(x, y, 0);
    }

    public boolean isInFrustum(Rectangle rect) {
        return isInFrustum(rect.x, rect.y, rect.width, rect.height);
    }

    public boolean isInFrustum(float x, float y, float w, float h) {
        return frustum.boundsInFrustum(x, y, 0, w, h, 0);
    }
}
