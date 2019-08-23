# PieView
Pie chart, including animation and data percentage, and text display.
# Effect display
  <img width="320" height="564" src="https://github.com/ljm17/PieView/raw/master/images/animator.gif"/><br/>
  <img width="320" height="564" src="https://github.com/ljm17/PieView/raw/master/images/img1.jpg"/>
  <img width="320" height="564" src="https://github.com/ljm17/PieView/raw/master/images/img2.jpg"/>
  <img width="320" height="564" src="https://github.com/ljm17/PieView/raw/master/images/img3.jpg"/>
  <img width="320" height="564" src="https://github.com/ljm17/PieView/raw/master/images/img4.jpg"/>
  <img width="320" height="564" src="https://github.com/ljm17/PieView/raw/master/images/img5.jpg"/><br/>
 
# Usages
  
  方法 | 介绍   
-|-
setData(List<PieEntry> data) | 设置数据 |
setStartDegree(float startDegree)| 设置初始绘制角度,default = 180 |
setCenterAlpha(float alpha) | 设置中间透明圈的透明度,default = 0.4f |
setHoleRadiusPercent(float holeRadiusPercent) | 设置白色内孔占外圆比例，default = 0.5f,设置值应在（0,1）之间 |
setAlphaRadiusPercent(float alphaRadiusPercent) | 设置透明圆占外圆比例，default = 0.6f,设置值应在（0,1）之间，并大于等于白色内孔 |
setSpace(int space) | 设置凸出板块与相邻间距，default = 30 |
setDisPlayPercent(boolean disPlayPercent) | 设置是否显示百分比,default = true |
setShowAnimator(boolean showAnimator) | 开启动画,default = false |
setBlockTextSize(int blockTextSize) | 设置板块字体大小,default = 30 |
setBlockTextColor(int blockTextColor) | 设置板块字体颜色,default = Color.WHITE |
setCenterTextSize(int centerTextSize) | 设置中心文字大小,default = 50 |
setCenterTextColor(int centerTextColor) | 设置中心文字颜色，default = Color.GRAY |
setCenterText(String centerText) | 设置中心文字，default = "PieView" |
setShowCenterText(boolean showCenterText) | 是否显示中心文字,default = false |
refresh() | 刷新视图 |
  
### example：
  ```
  //链式设置
mPieView.setData(list)
                .setCenterText("TIME")
                .setCenterTextSize(60)
                .setCenterTextColor(Color.BLUE)
                .setShowAnimator(true)
                .refresh();
  ```
# About Me
CSDN :[https://blog.csdn.net/qq_40861368](https://blog.csdn.net/qq_40861368)
