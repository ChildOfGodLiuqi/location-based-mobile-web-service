package edu.xjtlu.mobilewebservices.decoding;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;


import edu.xjtlu.mobilewebservices.CaptureActivity;
import edu.xjtlu.mobilewebservices.R;
import edu.xjtlu.mobilewebservices.camera.CameraManager;
import edu.xjtlu.mobilewebservices.camera.PlanarYUVLuminanceSource;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.Hashtable;

final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final CaptureActivity activity;
  private final MultiFormatReader multiFormatReader;
  private boolean running = true;

  DecodeHandler(CaptureActivity activity, Hashtable<DecodeHintType, Object> hints) {
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.activity = activity;
  }

  @Override
  public void handleMessage(Message message) {
    if (!running) {
      return;
    }
    switch (message.what) {
      case R.id.decode:
        decode((byte[]) message.obj, message.arg1, message.arg2);
        break;
      case R.id.quit:
        running = false;
        Looper.myLooper().quit();
        break;
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private void decode(byte[] data, int width, int height) {
    long start = System.currentTimeMillis();
    Result rawResult = null;
    PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    try {
      rawResult = multiFormatReader.decodeWithState(bitmap);
    } catch (ReaderException re) {
      // continue
    } finally {
      multiFormatReader.reset();
    }

    if (rawResult != null) {
      // Don't log the barcode contents for security.
      long end = System.currentTimeMillis();
      Log.d(TAG, "Found barcode in " + (end - start) + " ms");
      Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
      Bundle bundle = new Bundle();
      bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
      message.setData(bundle);
      message.sendToTarget();
    } else {
      Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
      message.sendToTarget();
    }
  }

}
