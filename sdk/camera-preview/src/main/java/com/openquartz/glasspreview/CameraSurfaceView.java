package com.openquartz.glasspreview;

import android.content.Context;
import android.gesture.Gesture;
import android.hardware.Camera;
import android.util.Log;
import android.view.GestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private GestureDetector mGestureDetector = null;

    private static final int TAKE_PICTURE_REQUEST = 1;

    public CameraSurfaceView(Context context) {
        super(context);

        final SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();

        // Show the Camera display
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            releaseCamera();
        }

//        //START OF MY CODE
//        mGestureDetector = createGestureDetector(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Start the preview for surfaceChanged
        if (camera != null) {
            camera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Do not hold the camera during surfaceDestroyed - view should be gone
        releaseCamera();
    }

    /**
     * Release the camera from use
     */
    public void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}
