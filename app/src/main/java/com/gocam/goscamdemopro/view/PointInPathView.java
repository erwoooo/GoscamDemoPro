package com.gocam.goscamdemopro.view;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.entity.BoundarySettingParam;
import com.gocam.goscamdemopro.entity.PrivacySettingParam;
import com.gos.platform.api.devparam.BoundarySettingParamElement;
import com.gos.platform.api.devparam.SmdAlarmSettingParam;
import com.gos.platform.api.domain.DetectionArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ulife.goscam.com.loglib.dbg;

/**
 * Created by longtengfei on 2021/5/6.
 */
public class PointInPathView extends androidx.appcompat.widget.AppCompatImageView {
    public final static int MOTION_DETECT = 1;//运动检测设置
    public final static int HUMAN_DETECT = 2;//人形检测设置
    public final static int BOUNDARY_DETECT = 3;//周界检测设置
    public final static int PRIVACY_OCCLUSION = 4;//隐私遮挡设置
    //----绘制轨迹----
    private float mX;
    private float mY;
    private final Paint mGesturePaint = new Paint();
    private final Paint mGesturePaintFill = new Paint();
    private final Paint fingerPaint = new Paint();
    private final Paint selectedPaint = new Paint();
    private Path mPath = new Path();
    private Path gridPath = new Path();
    private List<Path> mPaths = new ArrayList<>();
    private List<List<PointF>> savePaths = new ArrayList<>(); // //区域的集合
    private List<PointF> savePath = new ArrayList<>(); // 坐标点集合
    //------检测点是否在path内
    private boolean isSelect = true;
    Region re = new Region();

    public int areaX = 4;
    public int areaY = 4;

    boolean[] itemStatus = new boolean[areaX * areaY];
    List<Integer> itemStatusList = new ArrayList<>();

    float width;
    float height;

    boolean isChange = false;

    int type = MOTION_DETECT;//1移动侦测，2人形侦测

    boolean isFirstTouch = false;
    boolean isInit = true;

    String devId;
    SmdAlarmSettingParam smdAlarmResult;

    BoundarySettingParam boundarySettingParam;

    private PrivacySettingParam mPrivacyParam;

    boolean notDraw = false;//最多只能传4个区域，超过不能再画

    boolean isShowGrid = true;//是否显示格子

    int mClickPosition;

    public PointInPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PointInPathView(Context context) {
        super(context);
        init(context);
    }

    public void init(Context context) {
        mGesturePaint.setColor(context.getResources().getColor(android.R.color.holo_green_dark));
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mGesturePaint.setStrokeWidth(4.0f);
        mGesturePaintFill.setColor(context.getResources().getColor(android.R.color.holo_green_dark));
        mGesturePaintFill.setStyle(Paint.Style.FILL);
        mGesturePaintFill.setAlpha(150);

        fingerPaint.setColor(context.getResources().getColor(R.color._B337FF79));
        fingerPaint.setStyle(Paint.Style.STROKE);
        fingerPaint.setStrokeWidth(4.0f);
        selectedPaint.setColor(context.getResources().getColor(R.color._B337FF79));
        selectedPaint.setAlpha(100);

        for (int i = 0; i < areaX * areaY; i++) {
            itemStatusList.add(0);
        }
    }

    public void setPaintColor(int gesture, int finger, int selected) {
        mGesturePaint.setColor(gesture);
        mGesturePaint.setAlpha(150);
        mGesturePaint.setStyle(Paint.Style.FILL);
        mGesturePaintFill.setColor(gesture);
        mGesturePaintFill.setAlpha(150);
        mGesturePaintFill.setStyle(Paint.Style.FILL);

        fingerPaint.setColor(finger);
        selectedPaint.setColor(selected);
        selectedPaint.setAlpha(150);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        if (type == BOUNDARY_DETECT) {
            drawPath(devId, boundarySettingParam);
        } else if (type == PRIVACY_OCCLUSION) {
            drawPath(mPrivacyParam);
        } else {
            drawPath(devId, smdAlarmResult);
        }
//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //运动检测不画手势线
        if (type == MOTION_DETECT && isShowGrid) {
            dbg.d("PointInPathView", "drawGrid");
            drawGrid(canvas);
            dbg.d("PointInPathView", "drawSelectArea");
            drawSelectArea(canvas);
            dbg.d("PointInPathView", "onDraw finish");
        }

        for (int i = 0; i < mPaths.size(); i++) {
            Path path = mPaths.get(i);
            if (type == PRIVACY_OCCLUSION) {
                canvas.drawPath(path, mGesturePaintFill);
            } else if (i < mPaths.size() - 1 && (type == HUMAN_DETECT || type == BOUNDARY_DETECT)) {
                canvas.drawPath(path, mGesturePaintFill);
            } else {
                canvas.drawPath(path, mGesturePaint);
            }
        }

//        canvas.drawPath(mPath, mGesturePaint);
    }

    //画被选中区域
    public void drawSelectArea(Canvas canvas) {
        if (itemStatusList == null) {
            return;
        }
        float gridHeight = height / areaY;
        float gridWidth = width / areaX;
        for (int i = 0; i < itemStatusList.size(); i++) {
            //boolean itemStatus = this.itemStatusList.get(i) == 1;
            if (this.itemStatusList.get(i) == 1) {
                int gridX = i % areaX;
                int gridY = i / areaY;
                canvas.drawRect(gridWidth * gridX,gridHeight * gridY,gridWidth * (gridX + 1),gridHeight * (gridY + 1),selectedPaint);
            }
        }
    }


    //画格子
    public void drawGrid(Canvas canvas) {
        float gridHeight = height / areaY;
        float gridWidth = width / areaX;

        for (int i = 0; i < areaX + 1; i++) {
            if (i == areaX) {
                canvas.drawLine(width, 0, width, height, mGesturePaint);
            } else {
                canvas.drawLine(gridWidth * i, 0, gridWidth * i, height, mGesturePaint);
            }
        }

        for (int i = 0; i < areaY + 1; i++) {
            if (i == areaY) {
                canvas.drawLine(0, height, width, height, mGesturePaint);
            } else {
                canvas.drawLine(0, gridHeight * i, width, gridHeight * i, mGesturePaint);
            }

        }

    }

    //计算出被选中的格子
    public void checkGrid() {
        if (itemStatusList == null) {
            return;
        }
        boolean isDelete = true;
        float gridHeight = height / areaY;
        float gridWidth = width / areaX;
        RectF r = new RectF();
        //计算控制点的边界
        mPath.computeBounds(r, true);
        //设置区域路径和剪辑描述的区域
        re.setPath(mPath, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        for (int i = 0; i < itemStatusList.size(); i++) {
            if (isSelect && itemStatusList.get(i) == 1) {
                continue;
            }
            int gridX = i % areaX;
            int gridY = i / areaX;
            float pointX = gridWidth * gridX + gridWidth / 2;
            float pointY = gridHeight * gridY + gridHeight / 2;

            //计算一个格子的4个角是否包含在图形内
            float pointX1 = gridWidth * gridX;
            float pointY1 = gridHeight * gridY;
            float pointX2 = gridWidth * (gridX + 1);
            float pointY2 = gridHeight * gridY;
            float pointX3 = gridWidth * (gridX + 1);
            float pointY3 = gridHeight * (gridY + 1);
            float pointX4 = gridWidth * gridX;
            float pointY4 = gridHeight * (gridY + 1);
            boolean contains = false;

            //在封闭的path内返回true 不在返回false
            if (re.contains((int) pointX1, (int) pointY1) || re.contains((int) pointX2, (int) pointY2)
                    || re.contains((int) pointX3, (int) pointY3) || re.contains((int) pointX4, (int) pointY4)
                    || re.contains((int) pointX, (int) pointY)) {
                contains = true;
            }
            if (contains) {
                isDelete = false;
                itemStatusList.set(i, contains ? 1 : 0);
                itemStatus[i] = contains;
            }
        }

        if (isDelete) {
            for (int i = 0; i < itemStatusList.size(); i++) {
                if (mClickPosition == i) {
                    boolean contains = itemStatusList.get(mClickPosition) == 1;
                    itemStatusList.set(mClickPosition, contains ? 0 : 1);
                    itemStatus[mClickPosition] = !contains;
                    continue;
                }
                int gridX = i % areaX;
                int gridY = i / areaX;
                float pointX = gridWidth * gridX + gridWidth / 2;
                float pointY = gridHeight * gridY + gridHeight / 2;

                //计算一个格子的4个角是否包含在图形内
                float pointX1 = gridWidth * gridX;
                float pointY1 = gridHeight * gridY;
                float pointX2 = gridWidth * (gridX + 1);
                float pointY2 = gridHeight * gridY;
                float pointX3 = gridWidth * (gridX + 1);
                float pointY3 = gridHeight * (gridY + 1);
                float pointX4 = gridWidth * gridX;
                float pointY4 = gridHeight * (gridY + 1);

                if (re.contains((int) pointX1, (int) pointY1) || re.contains((int) pointX2, (int) pointY2)
                    || re.contains((int) pointX3, (int) pointY3) || re.contains((int) pointX4, (int) pointY4)
                    || re.contains((int) pointX, (int) pointY)) {
                    itemStatusList.set(i, 0);
                    itemStatus[i] = false;
                }
            }
        } else {
            itemStatusList.set(mClickPosition, 1);
            itemStatus[mClickPosition] = true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        //请求父控件不拦截子空间的触摸事件
        //就可以阻止父控件对子空间点击事件的拦击，可以为子控件单独设置点击事件的响应
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (notDraw) {
            return true;
        }
        if (isFirstTouch) {
            clear();
            isFirstTouch = false;
        }

        isChange = true;
        float x = event.getX();
        float y = event.getY();

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > width) {
            x = width - 1;
        }
        if (y > height) {
            y = height;
        }

        if (type == MOTION_DETECT) {
            dbg.d("PointInPathView", "checkPointInGrid");
            checkPointInGrid((int) x, (int) y);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (type == BOUNDARY_DETECT || type == PRIVACY_OCCLUSION) {
                    clear();
                }

                if (type == HUMAN_DETECT || type == BOUNDARY_DETECT) {
                    mGesturePaint.setStyle(Paint.Style.STROKE);
                    mGesturePaint.setAlpha(255);
                }
                dbg.d("PointInPathView", "touchDown");
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                dbg.d("PointInPathView", "touchMove");
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mPath.close();

                if (type == PRIVACY_OCCLUSION) {
                    touchUp(event);
                }

                if (type == HUMAN_DETECT || type == BOUNDARY_DETECT) {
                    mGesturePaint.setStyle(Paint.Style.FILL);
                    mGesturePaint.setAlpha(150);
                    if (isCross()) {
                        mPaths.remove(mPath);
                        mPath.reset();
                        invalidate();
                        return true;
                    }
                    if (mPaths.size() >= 4) {
                        notDraw = true;
                    }
                    if(listener != null){
                        List<DetectionArea> humanPoint = getHumanPoint();
                        listener.onTouchAreaChanged(humanPoint);
                    }
                }

                if (type == MOTION_DETECT) {
                    dbg.d("PointInPathView", "checkGrid");
//                    checkPointInGrid((int) x, (int) y);
                    checkGrid();
                    dbg.d("PointInPathView", "checkGrid finished");
                    //运动检测每次画线只保留一个path用于选格，不画线
//                    savePaths.clear();
//                    mPaths.clear();
                    mPath.reset();
//                    mPath = null;
                }


//                mPath.isConvex()

//                isDraw = true;
                break;
        }
        //更新绘制
        invalidate();
        return true;
    }

    OnTouchAreaChangedListener listener;
    public void setOnTouchAreaChangedListener(OnTouchAreaChangedListener listener){
        this.listener = listener;
    }

    public interface OnTouchAreaChangedListener{
        void onTouchAreaChanged(List<DetectionArea> humanPoint);
    }

    //判断线段是否相交
    public boolean isCross() {
        boolean intersect = false;
        int cut = savePath.size() / 6;
        if (cut == 0) {
            cut = 1;
        }
        int width = getWidth();
        int height = getHeight();
        float scalew = 10000f / width;
        float scaleh = 10000f / height;
        List<PointF> points = new ArrayList<>();
        for (int j = 0; j < savePath.size(); j++) {
            boolean isAdd = false;
            if (j == 0) {
                isAdd = true;
            } else if (j % cut == 0) {
                isAdd = true;
            } else if (j == savePath.size() - 1) {
                isAdd = true;
            }
            if (isAdd) {
                PointF pointF = savePath.get(j);
                int x = (int) (pointF.x * scalew);
                int y = (int) (pointF.y * scaleh);
                points.add(new PointF(x, y));
            }
        }
        for (int i = 0; i < points.size() - 2; i++) {
            int cal = i;
            PointF pointF = points.get(cal++);
            PointF pointF1 = points.get(cal++);
            for (int j = 0; j < points.size() - 3; j++) {
                PointF pointF2 = points.get(cal++);
                PointF pointF3;
                if (cal < points.size()) {
                    pointF3 = points.get(cal);
                } else {
                    cal = 0;
                    pointF3 = points.get(0);
                }
                boolean intersect2 = intersect(pointF, pointF1, pointF2, pointF3);
                if (intersect2) {
                    return intersect2;
                }
            }
        }
        return intersect;
    }

    //aa, bb为一条线段两端点 cc, dd为另一条线段的两端点 相交返回true, 不相交返回false
    public boolean intersect(PointF aa, PointF bb, PointF cc, PointF dd) {
        if (max(aa.x, bb.x) < min(cc.x, dd.x)) {
            return false;
        }
        if (max(aa.y, bb.y) < min(cc.y, dd.y)) {
            return false;
        }
        if (max(cc.x, dd.x) < min(aa.x, bb.x)) {
            return false;
        }
        if (max(cc.y, dd.y) < min(aa.y, bb.y)) {
            return false;
        }
        if (mult(cc, bb, aa) * mult(bb, dd, aa) < 0) {
            return false;
        }
        if (mult(aa, dd, cc) * mult(dd, bb, cc) < 0) {
            return false;
        }
        return true;
    }

    //叉积
    double mult(PointF a, PointF b, PointF c) {
        return (a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void initPath(String devId, SmdAlarmSettingParam smdAlarmResult) {
        this.devId = devId;
        this.smdAlarmResult = smdAlarmResult;
        isFirstTouch = true;
    }

    public void initPath(String devId, BoundarySettingParam boundarySettingParam) {
        this.devId = devId;
        this.boundarySettingParam = boundarySettingParam;
        isFirstTouch = true;
        requestLayout();
    }

    public void initPath(String devId, PrivacySettingParam privacyParam) {
        this.devId = devId;
        this.mPrivacyParam = privacyParam;
        isFirstTouch = true;
        requestLayout();
    }

    private void drawPath(PrivacySettingParam privacyParam) {
        if (privacyParam == null) {
            return;
        }

        float scalex = 10000f / width;
        float scaley = 10000f / height;
        float x0 = privacyParam.getX0() / scalex;
        float x1 = privacyParam.getX1() / scalex;
        float y0 = privacyParam.getY0() / scaley;
        float y1 = privacyParam.getY1() / scaley;
        savePaths.clear();
        mPaths.clear();
        Path path = new Path();
        path.addRect(new RectF(x0, y0, x1, y1), Path.Direction.CW);
        mPaths.add(path);
    }

    //读取缓存的画线
    public void drawPath(String devId, SmdAlarmSettingParam smdAlarmResult) {

        this.devId = devId;
        this.smdAlarmResult = smdAlarmResult;
        savePaths.clear();
        mPaths.clear();

        if (smdAlarmResult != null) {
            List<DetectionArea> areas = smdAlarmResult.detectionAreas;
            float scalex = 10000f / width;
            float scaley = 10000f / height;
            for (int i = 0; i < areas.size(); i++) {
                DetectionArea detectionArea = areas.get(i);
                mPath = new Path();
                savePath = new ArrayList<>();
                for (int j = 0; j < detectionArea.points.size(); j++) {
                    Point point = detectionArea.points.get(j);
                    float x = point.x / scalex;
                    float y = point.y / scaley;
                    if (j == 0) {
                        mPath.moveTo(x, y);
                    } else {
                        mPath.lineTo(x, y);
                    }
                    savePath.add(new PointF(x, y));
                }
                savePaths.add(savePath);
                mPath.close();
                mPaths.add(mPath);
            }

            if(savePaths.size() == 4){
                notDraw = true;
            }
//            invalidate();
        }
    }

    //读取缓存的画线
    public void drawPath(String devId, BoundarySettingParam boundarySettingParam) {
        this.devId = devId;
        this.boundarySettingParam = boundarySettingParam;
        savePaths.clear();
        mPaths.clear();

        if (boundarySettingParam != null) {
            float scalex = 10000f / width;
            float scaley = 10000f / height;
            for (int i = 0; i < boundarySettingParam.getPerms().size(); i++) {
                BoundarySettingParamElement.Perms perms = boundarySettingParam.getPerms().get(i);
                mPath = new Path();
                savePath = new ArrayList<>();
                for (int j = 0; j < perms.rect.size(); j++) {
                    BoundarySettingParamElement.Rect point = perms.rect.get(j);
                    float x = point.x / scalex;
                    float y = point.y / scaley;
                    if (j == 0) {
                        mPath.moveTo(x, y);
                    } else {
                        mPath.lineTo(x, y);
                    }
                    savePath.add(new PointF(x, y));
                }
                savePaths.add(savePath);
                mPath.close();
                mPaths.add(mPath);
            }

            if(savePaths.size() == 4){
                notDraw = true;
            }
        }
    }


    //---------------下边是划线部分----------------------------//
    //手指点下屏幕时调用
    private void touchDown(MotionEvent event) {
        //重置绘制路线，即隐藏之前绘制的轨迹
//        mPath.reset();


        float x = event.getX();
        float y = event.getY();
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        mX = x;
        mY = y;

        if (type == MOTION_DETECT) {
//            mPath.reset();
            mPath.moveTo(x, y);
            if (mPaths.size() == 0) {
                mPaths.add(mPath);
            }
        } else {
            mPath = new Path();
            mPath.moveTo(x, y);

            if (isSelect) {
                mPaths.add(mPath);
                savePath = new ArrayList<>();
                savePath.add(new PointF(x, y));
                savePaths.add(savePath);
            }
        }


    }

    //手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > width) {
            x = width - 1;
        }
        if (y > height) {
            y = height;
        }
        final float previousX = mX;
        final float previousY = mY;
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        //两点之间的距离大于等于3时，连接连接两点形成直线
        if (dx >= 3 || dy >= 3) {
            //两点连成直线
            if (type == PRIVACY_OCCLUSION) {
                mPath.reset();
                mPath.addRect(new RectF(mX, mY, x, y), Path.Direction.CW);
            } else {
                mPath.lineTo(x, y);
                //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
                mX = x;
                mY = y;
            }

            if (type == HUMAN_DETECT || type == BOUNDARY_DETECT) {
                savePath.add(new PointF(x, y));
            }
        }
    }

    //手指从屏幕上离开时调用
    private void touchUp(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > width) {
            x = width - 1;
        }
        if (y > height) {
            y = height;
        }

        final float previousX = mX;
        final float previousY = mY;
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        //两点之间的距离大于等于3时，记录坐标
        if (dx >= 3 || dy >= 3) {
            savePath.add(new PointF(x, y));
        }
    }

    //去除选中的区域
    public void eraser() {
        isSelect = !isSelect;
        for (int i = 0; i < mPaths.size(); i++) {
            mPaths.get(i).reset();
        }
//        mPath.reset();
        savePaths.clear();
        invalidate();
    }

    public boolean[] getItemStatus() {
        for (int i = 0; i < itemStatusList.size(); i++) {
            Integer integer = itemStatusList.get(i);
            itemStatus[i] = integer == 1;
        }
        return itemStatus;
    }

    public boolean isChange() {
        return isChange;
    }

    //检查点在哪个格子上
    public void checkPointInGrid(int x, int y) {
        if (itemStatusList == null) {
            return;
        }

        //如果x等于宽度，会导致计算的结果不对
        if (x >= width) {
            x = (int) width - 1;
        }

        float gridHeight = height / areaY;
        float gridWidth = width / areaX;

        int xPos = (int) (x / gridWidth);
        int yPos = (int) (y / gridHeight);
        int position = yPos * areaX + xPos;
        if (position < itemStatusList.size()) {
            mClickPosition = position;
//            itemStatusList.set(position, 1);
//            itemStatus[position] = true;
        }
    }

    public void clear() {
        isChange = true;
        for (int i = 0; i < itemStatus.length; i++) {
            itemStatus[i] = false;
        }
        if (itemStatusList != null) {
            for (int i = 0; i < itemStatusList.size(); i++) {
                itemStatusList.set(i, 0);
            }
        }
        savePaths.clear();
        mPaths.clear();
        mPath.reset();
        notDraw = false;
        invalidate();
    }

    public void setItemStatusList(boolean[] itemStatus) {
        if (itemStatus.length > 0) {
//            this.itemStatus = new boolean[itemStatus.length];
            this.itemStatus = Arrays.copyOf(itemStatus, itemStatus.length);
//            this.itemStatus = itemStatus;
            itemStatusList = new ArrayList<>();
            for (int i = 0; i < itemStatus.length; i++) {
                itemStatusList.add(itemStatus[i] ? 1 : 0);
            }
            invalidate();
        }
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    //获取un_enable_str，json格式：[0,1,0,1,0,1,0,1,0,1...]  1选中，0未选中
    public List<Integer> getSelectedList() {
//        List<Integer> seletList = new ArrayList<>();
//        for (int i = 0; i < itemStatus.length; i++) {
//            seletList.add(itemStatus[i] ? 1 : 0);
//        }
//        return seletList;
        return itemStatusList;
    }

    public void setArea(int x, int y) {
        if (x > 0) {
            areaX = x;
            areaY = y;
        }
    }

    public void setItemStatusList(List<Integer> list) {
        if (list != null && list.size() > 0) {
            this.itemStatusList = list;
            itemStatus = new boolean[list.size()];
            for (int i = 0; i < list.size(); i++) {
                itemStatus[i] = list.get(i) == 1;
            }
            invalidate();
        }
    }

    //获取人形侦测的坐标点
    public List<DetectionArea> getHumanPoint() {
        List<DetectionArea> areas = new ArrayList<>();
        int areaCount = savePaths.size();
        //最多传4个区域的坐标点
        if (areaCount > 4) {
            areaCount = 4;
        }
        for (int i = 0; i < areaCount; i++) {
            List<PointF> pointFS = savePaths.get(i);
            DetectionArea detectionArea = new DetectionArea();
            detectionArea.points = new ArrayList<>();
            int width = getWidth();
            int height = getHeight();
            float scalew = 10000f / width;
            float scaleh = 10000f / height;
            int cut = pointFS.size() / 6;
            if (cut == 0) {
                cut = 1;
            }
            for (int j = 0; j < pointFS.size(); j++) {
                boolean isAdd = false;
                if (j == 0) {
                    isAdd = true;
                } else if (j % cut == 0) {
                    isAdd = true;
                } else if (j == pointFS.size() - 1) {
                    isAdd = true;
                }
                if (isAdd && detectionArea.points.size() < 8) {
                    PointF pointF = pointFS.get(j);
                    int x = (int) (pointF.x * scalew);
                    int y = (int) (pointF.y * scaleh);
                    detectionArea.points.add(new Point(x, y));
                }
            }
            detectionArea.pointCount = detectionArea.points.size();
            areas.add(detectionArea);
        }
        return areas;
    }

    //获取周界检测的坐标点
    public List<BoundarySettingParamElement.Perms> getBoundaryPoint() {
        List<BoundarySettingParamElement.Perms> perms = new ArrayList<>();
        int areaCount = savePaths.size();
        //最多传4个区域的坐标点
        if (areaCount > 1) {
            areaCount = 1;
        }
        for (int i = 0; i < areaCount; i++) {
            List<PointF> pointFS = savePaths.get(i);
            BoundarySettingParamElement.Perms perms1 = new BoundarySettingParamElement.Perms();
            perms1.rect = new ArrayList<>();
            int width = getWidth();
            int height = getHeight();
            float scalew = 10000f / width;
            float scaleh = 10000f / height;
            int cut = pointFS.size() / 6;
            if (cut == 0) {
                cut = 1;
            }
            for (int j = 0; j < pointFS.size(); j++) {
                boolean isAdd = false;
                if (j == 0) {
                    isAdd = true;
                } else if (j % cut == 0) {
                    isAdd = true;
                } else if (j == pointFS.size() - 1) {
                    isAdd = true;
                }
                if (isAdd && perms1.rect.size() < 6) {
                    PointF pointF = pointFS.get(j);
                    int x = (int) (pointF.x * scalew);
                    int y = (int) (pointF.y * scaleh);
                    BoundarySettingParamElement.Rect rect = new BoundarySettingParamElement.Rect();
                    rect.x = x;
                    rect.y = y;
                    perms1.rect.add(rect);
                }
            }
            perms1.pcnt = perms1.rect.size();
            perms.add(perms1);
        }
        return perms;
    }

    public PrivacySettingParam getPrivacyPoint(PrivacySettingParam param) {
        if (savePath != null && savePath.size() > 0) {
            int width = getWidth();
            int height = getHeight();
            float scalew = 10000f / width;
            float scaleh = 10000f / height;
            for (int i = 0; i < savePath.size(); i++) {
                PointF pointF = savePath.get(i);
                pointF.x = pointF.x * scalew;
                pointF.y = pointF.y * scaleh;
                if (i == 0) {
                    param.setX0((int) pointF.x);
                    param.setY0((int) pointF.y);
                    continue;
                }

                if (param.getX0() > pointF.x) {
                    param.setX1(param.getX0());
                    param.setX0((int) pointF.x);
                } else {
                    param.setX1((int) pointF.x);
                }

                if (param.getY0() > pointF.y) {
                    param.setY1(param.getY0());
                    param.setY0((int) pointF.y);
                } else {
                    param.setY1((int) pointF.y);
                }
            }
        }
        return param;
    }

    //画线全选，左上右下4个点
    public void selectAllPath() {
        isFirstTouch = true;
        clear();
        PointF leftTop = new PointF(0, 0);
        PointF rightTop = new PointF(width, 0);
        PointF rightBottom = new PointF(width, height);
        PointF leftBottom = new PointF(0, height);
        if (type == PRIVACY_OCCLUSION) {
            savePath.add(leftTop);
            savePath.add(rightBottom);
        }
        List<PointF> savePathAll = new ArrayList<>();
        savePathAll.add(leftTop);
        savePathAll.add(rightTop);
        savePathAll.add(rightBottom);
        savePathAll.add(leftBottom);
        savePaths.add(savePathAll);
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(width, 0);
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        mPath.close();
        mPaths.add(mPath);
        invalidate();
    }

    //全选或反选
    public void selectAll(boolean isSelectAll) {
        for (int i = 0; i < itemStatusList.size(); i++) {
            itemStatusList.set(i, isSelectAll ? 1 : 0);
        }
        savePaths.clear();
        mPaths.clear();
        if (mPath != null) {
            mPath.reset();
        }
        invalidate();
    }

    //隐藏格子
    public void setShowGrid(boolean isShow) {
        isShowGrid = isShow;
        invalidate();
    }

}
