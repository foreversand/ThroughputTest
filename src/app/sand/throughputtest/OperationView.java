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
import android.widget.Toast;


public class OperationView extends View implements
		OnSharedPreferenceChangeListener {

	private static final String VIEW_TAG = "View";
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

	private int mSource;
	private String mSourceName;
	private int mDest;
	private String mDestName;
	private int mSourceSelected;
	private Rect mRectMemSelect;
	private Rect mRectCpuSelect;
	private Rect mRectSDSelect;
	private String mBuffersize;
	private String mPattern;
	
	private boolean mOperationStart = false;
	
	private stateListener mListener = null;
	
	
	
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
        mRectMemSelect = new Rect(mRectMem);
        mRectMemSelect.set(mRectMem.centerX() - mRectMem.width() / 4,
        				   mRectMem.centerY() - mRectMem.height() / 4,
        				   mRectMem.centerX() + mRectMem.width() / 4,
        				   mRectMem.centerY() + mRectMem.height() / 4);
        mRectCpuSelect = new Rect(mRectCpu);
        mRectCpuSelect.set(mRectCpuSelect.centerX() - mRectCpuSelect.width() / 4,
        				   mRectCpuSelect.centerY() - mRectCpuSelect.height() / 4,
        				   mRectCpuSelect.centerX() + mRectCpuSelect.width() / 4,
        				   mRectCpuSelect.centerY() + mRectCpuSelect.height() / 4);
        mRectSDSelect = new Rect(mRectSD);
        mRectSDSelect.set(mRectSDSelect.centerX() - mRectSDSelect.width() / 4,
        		   		  mRectSDSelect.centerY() - mRectSDSelect.height() / 4,
        		   		  mRectSDSelect.centerX() + mRectSDSelect.width() / 4,
        		   		  mRectSDSelect.centerY() + mRectSDSelect.height() / 4);
        mSourceSelected = 0;
        
        
        // perform the drag action
        OnTouchListener onTouch = new OnTouchListener() {


			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				boolean isactioncomplete = false;
				int srcX = 0;
				int srcY = 0; 
				int destX = 0;
				int destY = 0;

				
				if(action == MotionEvent.ACTION_DOWN) {
					
					srcX = (int) event.getX();
					srcY = (int) event.getY();

					if(mRectMem.contains(srcX, srcY)) {
						mSource = 1;
						mSourceSelected = 1;
						
					}
					else if(mRectCpu.contains(srcX, srcY)) {
						mSource = 2;
						mSourceSelected = 2;
					}
					else if(mRectSD.contains(srcX, srcY)) {
						mSource = 3;
						mSourceSelected = 3;
					}
					else {
						mSource = 0;
						mSourceSelected = 0;
						
					}
					Log.d(VIEW_TAG, "event: " + event.getAction());
					Log.d(VIEW_TAG, "srcX: " + srcX + " srcY:" + srcY + " source: "+ mSource + "\n");
					
					switch(mSource) {
					case 0:
						mSourceName = "UNKOWN DISK";
						break;
					case 1:
						mSourceName = "Memory";
						break;
					case 2:
						mSourceName = "Register";
						break;
					case 3:
						mSourceName = "SD Card";
						break;
					default:
						mSourceName = "UNKOWN DISK";
						break;
					}
					postInvalidate();
				}
				else if(action == MotionEvent.ACTION_MOVE) {
					switch(mSourceSelected) {
					case 0:
						break;
					case 1:
						mRectMemSelect.offset((int)event.getX() - mRectMemSelect.centerX(), 
									          (int)event.getY() - mRectMemSelect.centerY());
						break;
					case 2:
						mRectCpuSelect.offset((int)event.getX() - mRectCpuSelect.centerX(), 
						          (int)event.getY() - mRectCpuSelect.centerY());
						break;
					case 3:
						mRectSDSelect.offset((int)event.getX() - mRectSDSelect.centerX(), 
						          (int)event.getY() - mRectSDSelect.centerY());
						break;
					default:
						break;
					}
					postInvalidate();
				}
				else if(action == MotionEvent.ACTION_UP) {
					mSourceSelected = 0;
					destX = (int) event.getX();
					destY = (int) event.getY();
					if(mRectMem.contains(destX, destY)) {
						mDest = 1;
					}
					else if(mRectCpu.contains(destX, destY)) {
						mDest = 2;
					}
					else if(mRectSD.contains(destX, destY)) {
						mDest = 3;
					}
					else {
						mDest = 0;
					}
					
					switch(mDest) {
					case 0:
						mDestName = "UNKOWN DISK";
						break;
					case 1:
						mDestName = "Memory";
						break;
					case 2:
						mDestName = "Register";
						break;
					case 3:
						mDestName = "SD Card";
						break;
					default:
						mDestName = "UNKOWN DISK";
						break;
					}		
					Log.d(VIEW_TAG, "event: " + event.getAction());
					Log.d(VIEW_TAG, "destX: " + destX + " destY:" + destY + " dest: "+ mDest + "\n");
					isactioncomplete = true;
				}
				if(isactioncomplete) {
					isactioncomplete = false;
					Log.d(VIEW_TAG, "finished srcX: " + srcX + " srcY:" + srcY + " source: "+ mSource + "\n");
					Log.d(VIEW_TAG, "finished destX: " + destX + " destY:" + destY + " dest: "+ mDest + "\n");
					if(!mOperationStart) {
						mResult = "copy form " + mSourceName + " to " + mDestName + "\n"; 
						if(mSource > 0 && mDest > 0 && mSource != mDest) {
							mOperationStart = true;
							if (mListener != null) {
								mListener.onOperationStart(mOperationStart);
							}
						}
						else {
							mResult = "Drag from one icon to another, Please Retry! \n";
						}
					}
					else {
						Toast.makeText(getContext(), "Test Operation is running, please wait...", Toast.LENGTH_LONG).show();
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
		mPaint.setColor(0xffffffff);
    	mPaint.setStyle(Style.FILL_AND_STROKE);
    	mPaint.setFilterBitmap(false);
    	
    	canvas.drawRect(PADDING, PADDING, this.getWidth() - PADDING, PADDING * 3 + mIconsize, mPaint);
    	canvas.drawRect(PADDING, PADDING * 5 + mIconsize, this.getWidth() - PADDING, PADDING * 8 + 2 * mIconsize, mPaint);

    	switch(mSourceSelected){
    	case 0: 
    		canvas.drawBitmap(mMem, mRectIcon, mRectMem, mPaint);
    		canvas.drawBitmap(mCpu, mRectIcon, mRectCpu, mPaint);  	
    		canvas.drawBitmap(mSD, mRectIcon, mRectSD, mPaint);
    		break;
    	case 1:
    		
    		canvas.drawBitmap(mCpu, mRectIcon, mRectCpu, mPaint);  	
    		canvas.drawBitmap(mSD, mRectIcon, mRectSD, mPaint);
    		canvas.drawBitmap(mMem, mRectIcon, mRectMemSelect, mPaint);
    		break;
    	case 2:
    		canvas.drawBitmap(mMem, mRectIcon, mRectMem, mPaint);
    			
    		canvas.drawBitmap(mSD, mRectIcon, mRectSD, mPaint);
    		canvas.drawBitmap(mCpu, mRectIcon, mRectCpuSelect, mPaint);  
    		break;
    	case 3:
    		canvas.drawBitmap(mMem, mRectIcon, mRectMem, mPaint);
    		canvas.drawBitmap(mCpu, mRectIcon, mRectCpu, mPaint);  	
    		canvas.drawBitmap(mSD, mRectIcon, mRectSDSelect, mPaint);
    		break;
    	default:
    		canvas.drawBitmap(mMem, mRectIcon, mRectMem, mPaint);
    		canvas.drawBitmap(mCpu, mRectIcon, mRectCpu, mPaint);  	
    		canvas.drawBitmap(mSD, mRectIcon, mRectSD, mPaint);
    		break;
    	}

	}

	//draw the multi-line text. The space and symbol can not be the beginning char of the line.
	//do not support the scroll view
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
			//Log.d(VIEW_TAG, ch + "\n");
			curlength += widths[i];
			if(!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) 
			   && (curlength == widths[i])) {
				beginword ++;
				lastword ++;
				curlength = 0;

			}
			else {
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
				if (!(ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
					lastword = i + 1;
				}
			}
			
		}	
		if(beginword < mResult.length()) {
			canvas.drawText(mResult, 
					beginword,
					mResult.length(),
					PADDING, 
					PADDING * 10 + 2 * mIconsize + mTextPaint.getTextSize() * (curline + 1),
					mTextPaint);
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
		mBuffersize = mPref.getString("BufferSize", "65536");
		mPattern = mPref.getString("DataPattern", "0");
		Log.d(VIEW_TAG, "DataLength: " + mBuffersize + " Pattern: " + mPattern + "\n");
	}

	public interface stateListener {
		public void onOperationStart(boolean state);
	}
	
	public void registerStatelistener (stateListener listener) {
        mListener  = listener;
    }
	
	public boolean getOperationState() {
		return mOperationStart;
	}
	
	public void setOperationState(boolean state) {
		mOperationStart = state;
	}
	
	public void setOperationResult(String str) {
		mResult = str;
	}
	
	public String getOperationResult() {
		return mResult;
	}
	public int getSourceDisk() {
		return mSource;
	}
	public int getDestDisk() {
		return mDest;
	}
	public String getBufferSize() {
		return mBuffersize;
	}
	public String getPattern() {
		return mPattern;
	}
}
