package com.jia.luckypan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by W on 2016/9/3.
 */
public class LuckyPan extends SurfaceView implements SurfaceHolder.Callback ,Runnable{
    private SurfaceHolder mHoder; //与Surfaceview结合使用 通过getHolder方法获得
    private Canvas mCanvas; // 通过Holder的lockcanvas（）方法获得
    private Thread t;// 在surfacecreated（）方法中开启子线程
    private boolean IsRunning;

    private String[] mStr=new String[]{"单方相机","IPad","恭喜发财","Iphone","服装","谢谢参与"};

    private int[] mImgs=new int[]{R.drawable.danfan,R.drawable.ipad,R.drawable.f015,R.drawable.iphone,R.drawable.meizi,R.drawable.f040,};
    //与图片对应的bitmap数组
    private Bitmap[] mImgsBitmap;  //防止在draw()里面转，以避免过多的回收
    //转盘背景图
    private Bitmap mBgBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.bg2);
    //转盘文字大小
    private float mTextSize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,24,getResources().getDisplayMetrics());

    //6个盘块的颜色
    private int[] mColor =new int[]{0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01};
    //与盘块对应的数量
    private int mitemCount=6;


    /**
     *盘快的范围
     */
    //整个盘块的范围
    private RectF mRange=new RectF();
    //整个盘快的直径
    private int mRadius;
    //转盘的中心位置
    private int mCenter;
    //转盘的边距  以paddingleft为准
    private int mPadding;

    /**
     * 绘制的画笔
     */
    //绘制盘块的画笔
    private Paint mPanPaint;
    //绘制文字的画笔
    private Paint mTextPaint;

    //滚动的速度
    private double mSpeed;
    //滚动的起始角度
    private volatile  float  mStartAngle=0;//volatile 保证线程间的可见性

    //是否点击了中心停止按钮
    private boolean mShowEnd;






    public LuckyPan(Context context) {
        this(context, null);
    }

    public LuckyPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        //1.获得surfaceholder
        mHoder=getHolder();
        //2.通过addcallback()方法获得surfaceview的生命周期
        mHoder.addCallback(this);

        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置surfaceview的常量
        setKeepScreenOn(true);

    }

    //8.强制转换屏幕转盘 从矩形变成正方形
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //边长
        int width=Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPadding=getPaddingLeft();
        //直径
        mRadius=width-mPadding*2;
        //中心点
        mCenter=width/2;

        //强制转换
        setMeasuredDimension(width, width);




    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        /**
         * 9.初始化画笔
         */
        //初始化盘块画笔
        mPanPaint=new Paint();
        mPanPaint.setAntiAlias(true);
        mPanPaint.setDither(true);
        //初始化文字画笔
        mTextPaint=new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        //初始化盘块的绘制范围
        mRange=new RectF(mPadding,mPadding,mPadding+mRadius,mPadding+mRadius);

        //初始化图片
        mImgsBitmap=new Bitmap[mitemCount];
        for (int i=0;i<mitemCount;i++) {
            mImgsBitmap[i]=BitmapFactory.decodeResource(getResources(),mImgs[i]);
        }




        //3.开启新线程
        IsRunning=true;
        t=new Thread(this);
        t.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //5.关闭线程
        IsRunning=false;


    }

    @Override
    public void run() {
        //6.判断是否启动子线程，启动就不断绘制
        while (IsRunning){
            long start=System.currentTimeMillis();
            draw();
            long end=System.currentTimeMillis();

            if (end-start<50) {
                try {
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //绘画的内容
    private void draw() {
        //7.获得canvas进行绘制
        mCanvas=mHoder.lockCanvas();
        try {
            if (mCanvas!=null){

                //10.绘制背景
                  drawBg();
                //11.绘制盘块
                float tmpAngle=mStartAngle;
                float sweepAngle=360/mitemCount;
                for (int i=0;i<mitemCount;i++){
                    //绘制盘块
                    mPanPaint.setColor(mColor[i]);
                    mCanvas.drawArc(mRange,tmpAngle,sweepAngle,true,mPanPaint);

                    //绘制文本
                    drawText(tmpAngle, sweepAngle, mStr[i]);

                    //绘制图片
                    drawIcon(tmpAngle,mImgsBitmap[i]);

                    tmpAngle+=sweepAngle;
                }

                //设置mSpeed使转盘旋转
                mStartAngle+=mSpeed;

                //如果点击了停止按钮
                if(mShowEnd){
                    mSpeed-=1;
                }
                if (mSpeed<=0){
                    mSpeed=0;
                    mShowEnd=false;
                }


            }
        } catch (Exception e) {
        } finally {
            //释放绘图，提交绘图
            if (mCanvas!=null){
                mHoder.unlockCanvasAndPost(mCanvas);
            }
        }

    }


//    //点击旋转让旋转在自己控制的范围
//    public void PanStart(int index){
//        //计算每一项的角度
//        float angle=360/mitemCount;
//        //计算每一项的中奖范围（当前的index）
//        /**
//         * 如果是1-》从初始位置到指针的结束的位置 范围在150~210之间
//         * 如果是0-》范围是180+30~180+30+60=210~270
//         */
//
//        float from=270-(index+1)*angle;
//        float end=from+angle;
//
//        //设置停下来需要旋转的距离
//        float targetFrom=3*360+from;
//        float targeEnd=3*360+end;
//
//
//        //区间速度
//        /**如果最开始为v1,最终速度会为0 而且每次速度是-1的   设v1为起始速度
//         * （v1+0）*(v1+1)=targetFrom （等差数列求和公式 ）
//         * v1*v1+v1-2*targeFrom=0 (一元二次方程)
//         * 解：v1=(-1+Math.sqrt(1+8*targetFrom))/2
//         */
//        float v1= (float) ((-1+Math.sqrt(1+8*targetFrom))/2);
//        float v2= (float) ((-1+Math.sqrt(1+8*targeEnd))/2);
//
//        //实际速度为v1~v2的中间值
//        mSpeed=v1+Math.random()*(v2-v1); //0到1之间 （单反到iPad）
//       // mSpeed=50;
//        mShowEnd=false;
//    }


    //点击开始
    public void PanStart(){
        mSpeed=50;
        mShowEnd=false;
    }

    //点击停止
    public void PanStop(){
      //  mStartAngle=0;//在进行控制之后强制转为0
        mShowEnd=true;
    }
   // 转盘是否在转转
    public boolean isStart(){
        return mSpeed!=0;
    }
    public boolean isShowEnd(){
        return mShowEnd;
    }

    //绘制icon (难点：求图片中心坐标)
    /** r=mRadius/4
     * x坐标=mCenter+r*cos(a)
     * y坐标=mCenter+r*sin(a )
     */
    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        //设置图片的宽度  直径的1/8
        int imgWidth=mRadius/8;

        //度数
        float angel= (float) ((tmpAngle+360/mitemCount/2)*Math.PI/180);

        int x= (int) (mCenter+mRadius/2/2*Math.cos(angel));
        int y= (int) (mCenter+mRadius/2/2*Math.sin(angel));

        //确定图片位置
        Rect rect=new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
        mCanvas.drawBitmap(bitmap,null,rect,null);


    }

    //绘制每个盘快的文本
    private void drawText(float tmpAngle, float sweepAngle, String s) {
        Path path=new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);
        //利用水平偏移量让文字居中
        float textWidth=mTextPaint.measureText(s);
        int hOffset= (int) (mRadius*Math.PI/mitemCount/2-textWidth/2);//水平
        int vOffset=mRadius/2/6;   //垂直
        mCanvas.drawTextOnPath(s,path,hOffset,vOffset,mTextPaint);

    }

    private void drawBg() {
        //mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawColor(0xFFA7D84C);
        mCanvas.drawBitmap(mBgBitmap,null,new RectF(mPadding/2,mPadding/2,getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2),null);


    }
}
