# LuckyPanDemo
这是一个转盘案例

使用surfaceview实现

1.自定义一个viewgroup，重写构造方法，声明变量

2.在构造方法中设置常用属性，获得getHolder()并实现addCallback（）三个方法，并调用Runnable接口
在surfaceCreated（）中开启子线程，在线程的run（）方法中进行 draw()方法绘制（记得在surfaceDestroyed（）
中关闭子线程），先把视图通过onMeasure（）转化为正方形。在绘制中，由于转盘是圆形，要设置它的
起始角度和块数。

3.在draw（）中:
a.绘制背景
b.绘制不同颜色的几个盘块
c.绘制文字，Canvas有drawTextOnPath（）方法利用水平偏移量绘制文字位置
d.绘制图片，小图片的绘制需要取得它的中心坐标
e.添加点击事件，让转盘旋转或者停止

4.可设置转盘选项的概率（可选）
