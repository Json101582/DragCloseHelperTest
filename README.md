# DragCloseHelper

[![](https://jitpack.io/v/bauer-bao/dragclosehelper.svg)](https://jitpack.io/#bauer-bao/dragclosehelper)

## 效果图：

1.demo效果

   ![image](https://github.com/bauer-bao/DragCloseHelper/blob/master/screenshoots/3.gif)

2.视频效果，非demo

   ![image](https://github.com/bauer-bao/DragCloseHelper/blob/master/screenshoots/1.gif)

## 使用步骤：

0.依赖

        allprojects {
        		repositories {
        			...
        			maven { url 'https://jitpack.io' }
        		}
        	}
        implementation 'com.github.bauer-bao:dragclosehelper:1.0.1'

1.activity主题设为透明

        <item name="android:windowIsTranslucent">true</item>
2.初始化

        DragCloseHelper dragCloseHelper = new DragCloseHelper(this);
3.如果是共享元素启动的页面，需要如下设置（强烈建议和共享元素一起使用，否则是没有灵魂的）

        dragCloseHelper.setShareElementMode(true);
4.设置需要进行拖拽的View/ViewGroup，以及背景ViewGroup（必须要设置背景色）

        dragCloseHelper.setDragCloseViews(parentV, childV);
5.设置监听

        dragCloseHelper.setDragCloseListener(new DragCloseHelper.DragCloseListener() {
            @Override
            public boolean intercept() {
                //默认false 不拦截。比如图片在放大状态，是不需要执行拖拽动画的等等。
                return false;
            }

            @Override
            public void dragStart() {
                //拖拽开始。可以在此额外处理一些逻辑
            }

            @Override
            public void dragging(float percent) {
                //拖拽中。percent当前的进度，取值0-1，可以在此额外处理一些逻辑
            }

            @Override
            public void dragCancel() {
                //拖拽取消，会自动复原。可以在此额外处理一些逻辑
            }

            @Override
            public void dragClose(boolean isShareElementMode) {
                //拖拽关闭，如果是共享元素的页面，需要执行activity的onBackPressed方法，注意如果使用finish方法，则返回的时候没有共享元素的返回动画
                if (isShareElementMode) {
                    onBackPressed();
                }
            }
        });

        dragCloseHelper.setClickListener(new DragCloseHelper.ClickListener() {
            @Override
            public void onClick(View view, boolean isLongClick) {
                 //isLongClick 是否为长按事件。建议长按使用库中此方法，单击不建议使用（建议直接写在宿主代码中）。
            }
        });
6.处理touch事件

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (dragCloseHelper.handleEvent(event)) {
                return true;
            } else {
                return super.dispatchTouchEvent(event);
            }
        }
7.可以自定义最大拖拽距离和最小缩放尺寸

        setMaxExitY(int maxExitY)
        setMinScale(@FloatRange(from = 0.1f, to = 1.0f) float minScale)

## 常见问题：
0.如果发现问题，欢迎提issue。如果issue没法及时解决，欢迎直接复制 + 黏贴 + 修改。

1.是否支持在fragment中使用

    不支持，可以将fragment层当做childview来处理

2.滑动关闭的过程中（手指脱离屏幕），view会显示在虚拟导航栏上

    使用如下代码，效果参考微信
    //隐藏状态栏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //透明导航栏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

3.滑动关闭的过程中（手机没有脱离屏幕），上一个页面设置共享动画的view显示空白

    方法a.在滑动开始的事件回调中（dragStart方法），使用rxbus/eventbus通知上一个页面，将目标的view的alpha设为1（view.setAlpha(1f)）
    方法b.见常见问题7

4.滑动关闭的过程中（手机脱离屏幕，view开始返回到上个页面），上一个页面设置共享动画的view显示空白

    方法a.在上一页面设置setExitSharedElementCallback监听，并在onCaptureSharedElementSnapshot回调中将sharedElement的alpha设为1，代码如下
    @Override
    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
        sharedElement.setAlpha(1f);
        return super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
    }
    方法b.见常见问题7

5.虚拟键盘显示或者隐藏之后，共享动画有异常，其中肯定有段黑屏的过程

    因为键盘显示或者隐藏，页面会重新创建，需要在manifest中的activity添加android:configChanges="screenLayout"即可

6.类似微信朋友圈，点击图片预览，左右切换图片之后返回，动画不匹配

    步骤1.在图片预览，切换图片之后，需要将最新的索引值通知上一页面
    步骤2.上一页面接受到通知之后，更新索引值
    步骤3.在onMapSharedElements回调中更新Map，代码如下
    @Override
    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
        super.onMapSharedElements(names, sharedElements);
        //更新共享元素键值对
        View view = adapter.getViewByPosition(photosRv, updateIndex, R.id.rv_item_fake_iv);
        if (view != null) {
            sharedElements.put("share_photo", view);
        }
    }

7.在滑动返回的过程中，出现种种的view显示空白的问题，可以统统使用此答案

    步骤1.在布局文件中，在目标view的底下，新建一个同样的fakeView
    步骤2.将目标view的共享动画，全部转移设置到fakeView上
    效果和微信朋友圈类似，不需要设置上面问题3和问题4的代码

8.如果用viewpager，可能会出现pointerIndex out of range pointerIndex=-1 pointerCount=1，需要try-catch，或者使用HackyViewPager

    try {
        if (dragCloseHelper.handleEvent(event)) {
            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return true;
    }

9.使用ViewPager + com.github.chrisbanes.photoview.PhotoView，vp的左右滑动和pv的缩放存在冲突

    这个属于vp和pv的bug，和本库没有任何关系，可以发现将滑动关闭注释掉之后，依然存在这问题。
    如果不是com.github.chrisbanes.photoview.PhotoView，则需要自己实现如下对应的方法。
    处理方法：
    步骤1.使用自定义HackyViewPager
    步骤2.photoview.setOnMatrixChangeListener，具体代码如下
    imageView.setOnMatrixChangeListener(rect -> {
        //因为demo中此页面是全屏处理，所以需要获取整个屏幕的宽度（包括横屏的时候虚拟按键的宽度），如果非全屏，自己更改此处的宽度
        int screenW = ScreenUtil.getWidthDpi(this);
        /**
         * 因为发现加载完成之后，matrix会和正常情况有误差，所以需要获取误差值，此处取系统误差值getScaledTouchSlop
         * 但是getScaledTouchSlop默认是8*系统密度，所以可以根据自己需求去设定
         *
         * 同时scale也会存在一定的误差，正常情况下可能在0.99-1.01之间
         */
        int delta = ViewConfiguration.get(this).getScaledTouchSlop() / 2;
        if (rect.width() < screenW) {
            //预览的时候，图片左右边界小于屏幕宽度 --> 此处存在三种情况，正常+缩小+放大（图片宽度还处于小于屏幕宽度的时候）
            if (imageView.getScale() < 0.99 || imageView.getScale() > 1.01) {
                //getscale会存在误差，如果是缩小或者放大状态，拦截vp的事件
                viewPager.setLocked(true);
            } else {
                //属于正常状态，可以左右滑动
                viewPager.setLocked(false);
            }
        } else {
            //预览的时候，大于或者等于屏幕宽度 --> 对应的情况，正常 + 放大
            if (Math.abs(rect.left - 0) <= delta || Math.abs(rect.right - screenW) <= delta) {
                //图片的左右边界，在误差范围内 --> 对应情况，正常 + 放大（图片拖拽到边界）
                viewPager.setLocked(false);
            } else {
                //放大，没拖拽到边界
                viewPager.setLocked(true);
            }
        }
    });

## 更新日志：

V1.0.1

    1.可放大图片的场景下，解决在放大状态下点击和长按事件失效的问题
    2.可放大图片的场景下，解决没法双指手势放大的问题

V1.0.0

    1.解决全屏的情况下（状态栏隐藏和虚拟导航栏隐藏的情况），显示状态栏和显示虚拟导航栏的手势冲突的问题
    2.优化滑动过程中的缩放比例

V0.0.10

    1.添加单击和长按事件监听

V0.0.9

    1.优化在viewpager中使用的体验

V0.0.8

    1.添加对ACTION_CANCEL的处理
    2.appcompat依赖修改为compileOnly

V0.0.7

    1.新建项目