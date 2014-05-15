package app.sand.throughputtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class OperationView extends View implements
		OnSharedPreferenceChangeListener {

	private static final int PADDING = 4;
	private SharedPreferences mPref;
	private Bitmap mSD;
	private Bitmap mMem;
	private Bitmap mCpu;
	private Bitmap mBackground;
	private Canvas mCanvas;
	private TextPaint mTextPaint;
	private Paint mPaint;
	private int mIconsize;
	private String mResult;
	private Rect mRectMem;
	private Rect mRectCpu;
	private Rect mRectSD;
	private Rect mRectIcon;

	
	
	
	public OperationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public OperationView(Context context, AttributeSet attrs){
		super(context, attrs);
		init();
	}
	
	public OperationView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		mPref.registerOnSharedPreferenceChangeListener(this);
		//read the preference
		onSharedPreferenceChanged(null, null);
		
		mResult = "Drag from one icon to another to test the transfer speed between them on another.\n";
			
		Bitmap sd_original = BitmapFactory.decodeResource(getResources(), R.drawable.sd);
		Bitmap mem_original = BitmapFactory.decodeResource(getResources(), R.drawable.memory);
		Bitmap cpu_original = BitmapFactory.decodeResource(getResources(), R.drawable.cpu);
		mIconsize = getResources().getDimensionPixelSize(R.dimen.diskicon);
		mSD = Bitmap.createScaledBitmap(sd_original, mIconsize, mIconsize, true);
		mMem = Bitmap.createScaledBitmap(mem_original, mIconsize, mIconsize, true);
		mCpu = Bitmap.createScaledBitmap(cpu_original, mIconsize, mIconsize, true);
		
		mBackground = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		mCanvas = new Canvas(mBackground);
        mCanvas.drawColor(0xff808080); //Opaque gray
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(0xffff6100);
        mPaint = new Paint();
        mPaint.setStrokeWidth(0);
        mPaint.setFilterBitmap(false);
        
        mRectIcon = new Rect(0, 0, mIconsize, mIconsize);
        mRectMem = new Rect(PADDING * 2, PADDING * 2, PADDING * 2 + mIconsize, PADDING * 2 + mIconsize);
        mRectCpu = new Rect(PADDING * 5 + mIconsize, PADDING * 2, PADDING * 5 + 2 * mIconsize, PADDING * 2 + mIconsize);
        mRectSD = new Rect(PADDING * 2, PADDING * 6 + mIconsize, PADDING * 2 + mIconsize, PADDING * 6 + 2 * mIconsize);
        
        OnTouchListener onTouch = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				boolean isactionbegin = false;
				boolean isactioncomplete = false;
				int srcX = 0;
				int srcY = 0; 
				int destX = 0;
				int destY = 0;
				int source = 0;
				int dest = 0;
				String srcDisk = "UNKOWN DISK";
				String destDisk = "UNKOWN DISK";
				Log.d("View", "event: " + event.getAction());
				if(action == MotionEvent.ACTION_DOWN) {
					isactionbegin = true;
					srcX = (int) event.getX();
					srcY = (int) event.getY();
				}
				else if(action == MotionEvent.ACTION_UP) {
					destX = (int) event.getX();
					destY = (int) event.getY();
					isactioncomplete = true;
				}
				if(isactioncomplete) {
					isactioncomplete = false;
					if(mRectMem.contains(srcX, srcY)) {
						source = 1;
					}
					else if(mRectCpu.contains(srcX, srcY)) {
						source = 2;
					}
					else if(mRectSD.contains(srcX, srcY)) {
						source = 3;
					}
					else {
						source = 0;
					}
					if(mRectMem.contains(destX, destY)) {
						dest = 1;
					}
					else if(mRectCpu.contains(destX, destY)) {
						dest = 2;
					}
					else if(mRectSD.contains(destX, destY)) {
						dest = 3;
					}
					else {
						dest = 0;
					}
					switch(source) {
					case 0:
						srcDisk = "UNKOWN DISK";
						break;
					case 1:
						srcDisk = "Memory";
						break;
					case 2:
						srcDisk = "Register";
					case 3:
						srcDisk = "SD Card";
						break;
					default:
						srcDisk = "UNKOWN DISK";	
					}
					switch(dest) {
					case 0:
						destDisk = "UNKOWN DISK";
						break;
					case 1:
						destDisk = "Memory";
						break;
					case 2:
						destDisk = "Register";
					case 3:
						destDisk = "SD Card";
						break;
					default:
						destDisk = "UNKOWN DISK";
					}
				    
					mResult = "copy form " + srcDisk + " to " + destDisk; 
					if(source > 0 && dest > 0) {
						mResult += " test result is:\n"; 
					}
					else {
						mResult += " Please Retry! \n";
					}
					postInvalidate();
				}
				return true;
			}
		};		
		setOnTouchListener(onTouch);
		
	}

    @Override
    protected void onDraw(Canvas canvas) {
    	drawBackground(canvas);
    	drawIcon(canvas);
        drawMultiText(canvas, PADDING, PADDING * 10 + 2 * mIconsize + mTextPaint.getTextSize(), this.getWidth() - PADDING * 2);
    	super.onDraw(canvas);
    }

	private void drawIcon(Canvas canvas) {
		mPaint.setColor(0x80ffffff);
    	mPaint.setStyle(Style.FILL_AND_STROKE);
    	mPaint.setFilterBitmap(false);
    	
    	canvas.drawRect(PADDING, PADDING, this.getWidth() - PADDING, PADDING * 3 + mIconsize, mPaint);
    	canvas.drawBitmap(mMem, mRectIcon, mRectMem, mPaint);
    	canvas.drawBitmap(mCpu, mRectIcon, mRectCpu, mPaint);  	
    	
    	canvas.drawRect(PADDING, PADDING * 5 + mIconsize, this.getWidth() - PADDING, PADDING * 8 + 2 * mIconsize, mPaint);
    	canvas.drawBitmap(mSD, mRectIcon, mRectSD, mPaint);
        //canvas.drawBitmap(mSD, PADDING * 2, PADDING * 6 + mIconsize, mPaint);
	}

	private void drawMultiText(Canvas canvas, float x, float y, int spaceX) {
		int textlength = mResult.length();
		int curlength = 0;
		int lastword = 0;
		int beginword = 0;
		int curline = 0;
		float[] widths = new float[textlength]; 
		mTextPaint.getTextWidths(mResult, widths);
		for(int i = 0; i < textlength; i++) {
			char ch = mResult.charAt(i);
			curlength += widths[i];
			if(!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))) {
				if(curlength == widths[i]) {
					beginword ++;
					curlength = 0;
				}
				
				if(ch == '\n') {
					canvas.drawText(mResult, 
							beginword,
							i,
							PADDING, 
							PADDING * 10 + 2 * mIconsize + mTextPaint.getTextSize() * (curline + 1),
							mTextPaint);
					curlength = 0;
				    beginword = i + 1;
				    lastword = i + 1;
				    curline++;
				}
				else {
					if(curlength > spaceX) {
						canvas.drawText(mResult, 
								beginword,
								lastword,
								PADDING, 
								PADDING * 10 + 2 * mIconsize + mTextPaint.getTextSize() * (curline + 1),
								mTextPaint);
						curline++;
						curlength = 0;
						beginword = lastword;
					}
					else {
						lastword = i + 1;
					}
				}
			}
			
		}		
	}

	private void drawBackground(Canvas canvas) {
		float scaleX = this.getWidth() / ((float) mBackground.getWidth());
    	float scaleY = this.getHeight() / ((float) mBackground.getHeight());
    	canvas.save();
    	canvas.scale(scaleX, scaleY);
    	canvas.drawBitmap(mBackground, 0, 0, mPaint);
    	canvas.restore();
	}	
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

	}

}
