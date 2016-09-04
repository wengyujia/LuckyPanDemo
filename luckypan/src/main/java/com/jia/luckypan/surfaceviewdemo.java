package com.jia.luckypan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by W on 2016/9/4.
 * SurfaceView的一般编写形式
 */
public class surfaceviewdemo extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    //1.声明变量
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread mThread;
    private boolean isRunning;



    public surfaceviewdemo(Context context) {
        this(context, null);
    }

    public surfaceviewdemo(Context context, AttributeSet attrs) {
        super(context, attrs);
        //2.在构造方法中通过getHolder（）获得surfaceHolder,并通过addcallback()创建生命周期(Holder管理生命周期)
        mHolder=getHolder();
        mHolder.addCallback(this);
        //3.设置一些属性 如：,可点击，可获得焦点，常量等
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //4.启动线程开关，创建run()方法，开启子线程
        isRunning=true;
        mThread=new Thread(this);
        mThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //5.关闭线程
        isRunning=false;

    }

    @Override
    public void run() {
        //6.判断线程是否已经开启，创建draw()方法不断绘制
        while (isRunning){
            draw();
        }

    }

    private void draw() {
        //7.获得canvas,并判断是否canvas为空，抛出异常，最后通过unlockcanvasandpost()释放
        mCanvas=mHolder.lockCanvas();
        try {
            if (mCanvas!=null){             //进行try/catch和判空是因为：1.在退出时surface可能会被销毁所以进行判空。
                                            //2.退出后线程是很难销毁的所以抛异常
                //8.绘制 SonmeThing
            }
        } catch (Exception e) {
        }finally {
            if (mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
