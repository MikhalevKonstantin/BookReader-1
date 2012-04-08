package com.bookreader.views.main;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Paint;
import android.util.Log;

import com.android.object.drawable.BaseDrawableObject;
import com.android.opengl.BaseGLSurfaceView;
import com.bookreader.config.Settings;
import com.bookreader.file.FileHelper;

public class PageObj extends BaseDrawableObject {
    List<LineObj> linesHolder;
    private final int padSize = 10;
    private static Paint paint = new Paint();

    public PageObj(BaseGLSurfaceView pView, float pWidth, float pHeight) {
        super(pView, pWidth, pHeight);
        this.posX = padSize;
        this.posY = padSize;
    }

    @Override
    protected void onDraw(GL10 gl) {

        for (LineObj lineObj : linesHolder) {
            lineObj.draw(gl);
        }

    }

    @Override
    protected void doInitDrawable(GL10 gl) {
        Log.d("[PageObj]", "doInitDrawable");
        height = mView.viewHeight - padSize * 2;
        width = mView.viewWidth - padSize * 2;

        addLines(gl);

        for (LineObj lineObj : linesHolder) {
            lineObj.initDrawable(gl);
        }
    }

    private void addLines(GL10 gl) {
        linesHolder = new LinkedList<LineObj>();
        char[] buffer = new char[Settings.BUFFERSIZE];
        int offset = Settings.OFFSET;
        int charsRead = FileHelper.readFile(mView.mContext, Settings.FILENAME, buffer, offset);
        String src = "";
        if (charsRead > 0) {
            src = FileHelper.unicodesToStr(buffer, charsRead);
            // Log.d("[PageObj]", "charsRead=" + String.valueOf(charsRead));
        }
        int lineNum = ((int) height) / Settings.LINEHEIGHT;
        int startAt = 0;
        for (int i = 0; i < lineNum; i++) {
            startAt += addLine(gl, src, startAt, i);
        }
        Settings.NEXTPAGEOFFSET = offset + startAt;
    }

    private int addLine(GL10 gl, final String src, final int startAt, final int lineNum) {
        // Log.d("[PageObj]", "addLine, startAt=" + String.valueOf(startAt));

        paint.setAntiAlias(true);
        paint.setTextSize(Settings.FONTSIZE);
        int charsToLine = 0;
        int newLineFlag = 0;
        for (int i = 1; startAt + i < src.length(); i++) {
            if (paint.measureText(src.substring(startAt, startAt + i)) > width) {
                break;
            } else if (src.substring(startAt, startAt + i).endsWith("\n")) {
                newLineFlag = 1;
                break;
            } else {
                charsToLine = i;
            }
        }

        // Log.d("[PageObj]", "StrWid=" + String.valueOf(paint.measureText(src.substring(startAt, startAt + charsToLine))));
        // Log.d("[PageObj]", "charsToLine=" + String.valueOf(charsToLine) + ":" + src.substring(startAt, startAt + charsToLine));
        if (charsToLine > 0) {
            String line = src.substring(startAt, startAt + charsToLine);

            int linePosY = (int) (posY + height - (lineNum + 1) * Settings.LINEHEIGHT);
            LineObj aLine = new LineObj(mView, posX, linePosY);
            aLine.makeLine(gl, line);
            linesHolder.add(aLine);
        }

        if (newLineFlag == 1) {
            charsToLine++;
        }
        return charsToLine;
    }
}
