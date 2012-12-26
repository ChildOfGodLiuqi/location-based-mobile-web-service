package edu.xjtlu.mobilewebservices;

import java.io.IOException;
import java.util.Vector;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import edu.xjtlu.mobilewebservices.camera.CameraManager;
import edu.xjtlu.mobilewebservices.decoding.CaptureActivityHandler;
import edu.xjtlu.mobilewebservices.decoding.InactivityTimer;
import edu.xjtlu.mobilewebservices.view.ViewfinderView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;

public class CaptureActivity extends Activity implements Callback {
	
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private TextView txtResult;
	public Button btnSearch,btnCancel,btnManual,btnVoice;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private String Scan_barcode;
	String Barcode_type;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);
		hasSurface = false;
		
		txtResult = (TextView) findViewById(R.id.textResult);
		inactivityTimer = new InactivityTimer(this);
		
		btnSearch  = (Button) findViewById(R.id.search);
		btnCancel  = (Button) findViewById(R.id.cancel);
		btnManual  = (Button) findViewById(R.id.manual);
		btnVoice   = (Button) findViewById(R.id.voice);
        
		//cancel button
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it1 = new Intent(CaptureActivity.this,MainFunction.class);
				startActivity(it1);
			}
		});
        
        btnVoice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it2 = new Intent(CaptureActivity.this,MainFunction.class);
				startActivity(it2);
			}
		});
        
        btnManual.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent it3 = new Intent(CaptureActivity.this,MainFunction.class);
				startActivity(it3);
			}
		});
       
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		
		//initial CameraManager
		CameraManager.init(getApplication());
		
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			//surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		
		//play sound after obtaining barcode
		playBeepSoundAndVibrate();
		
		//display barcode
		txtResult.setText("Format:"+obj.getBarcodeFormat().toString() + "\nCode Info:"
				+ obj.getText());
		Scan_barcode=obj.getText();
		Barcode_type=obj.getBarcodeFormat().toString();
		//show the hidden button
		btnSearch.setVisibility(0);
		
		//start another activity through intent
		btnSearch.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent_search_soap =new Intent(CaptureActivity.this,MainFunction.class);
			Bundle bundle =new Bundle();
			bundle.putString("key_search_item", Scan_barcode);
			bundle.putString("key_search_type", "Barcode");
			intent_search_soap.putExtras(bundle);
			startActivity(intent_search_soap);
		}
	});
	
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}
