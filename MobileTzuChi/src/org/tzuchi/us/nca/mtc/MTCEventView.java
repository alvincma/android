package org.tzuchi.us.nca.mtc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MTCEventView extends TextView {
	
	private Paint marginPaint;
	private Paint linePaint;
	private int backgroundColor;
	private float margin;
	
	public MTCEventView(Context context, AttributeSet attrSet, int ds) {
		super(context, attrSet, ds);
		init();
	}
	
	public MTCEventView(Context context) {
		super(context);
		init();
	}
	
	public MTCEventView(Context context, AttributeSet attrSet) {
		super(context, attrSet);
		init();
	}
	
	private void init() {
		// Get a reference to resource table
		Resources resources = getResources();
		
		// Create the paint brush
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		marginPaint.setColor(resources.getColor(R.color.event_margin));
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(resources.getColor(R.color.event_lines));
		
		// Get background color and margin width
		backgroundColor = resources.getColor(R.color.event_background);
		margin = resources.getDimension(R.dimen.event_margin);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		// Set background color as paper color
		canvas.drawColor(backgroundColor);
		
		// Draw ruled lines
		canvas.drawLine(0, 0, getMeasuredHeight(), 0, linePaint);
		canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), linePaint);
		
		// Draw margin
		canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
		
		// Move the text across from the margin
		canvas.save();
		canvas.translate(margin, 0);
		
		// Use the TextView to render the text
		super.onDraw(canvas);
		canvas.restore();
	}
}
