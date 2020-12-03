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
### Dependency：
Step 1. Add the JitPack repository to your build file
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
<br/>
Step 2. Add the dependency

```
dependencies {
	        implementation 'com.github.ljm17:PieView:1.2'
	}
```

### Method：

 返回 | 方法 | 介绍   
-|-|-
PieView | setData(List<? extends IPieEntry> data) | 设置数据 |
PieView | setStartDegree(float startDegree)| 设置初始绘制角度，default = 180 °|
PieView | setCenterAlpha(float alpha) | 设置中间透明圈的透明度，default = 0.4f |
PieView | setHoleRadiusPercent(float holeRadiusPercent) | 设置白色内孔占外圆比例，default = 0.5f,设置值应在（0,1）之间 （内孔半径应小于或等于透明圆半径，因板块文字绘制在外圆和透明圆之间）|
PieView | setAlphaRadiusPercent(float alphaRadiusPercent) | 设置透明圆占外圆比例，default = 0.6f,设置值应在（0,1）之间，并大于等于白色内孔 |
PieView | setSpace(int space) | 设置凸出板块与相邻间距，default = 30 px|
PieView | setDisPlayPercent(boolean disPlayPercent) | 设置是否显示百分比，default = true |
PieView | setAnimatorDuration(int duration) | 设置动画时长，default = 2000 ms|
PieView | setBlockTextSize(int blockTextSize) | 设置板块字体大小，default = 30 px|
PieView | setBlockTextColor(int blockTextColor) | 设置板块字体颜色，default = Color.WHITE （若文字过长超出板块区域建议设置白色以外的颜色，以防文字看不清）|
PieView | setCenterTextSize(int centerTextSize) | 设置中心文字大小，default = 50 px|
PieView | setCenterTextColor(int centerTextColor) | 设置中心文字颜色，default = Color.GRAY |
PieView | setCenterText(String centerText) | 设置中心文字，default = "PieView"，设置文字即开启显示中心文字|
PieView | setShowCenterText(boolean showCenterText) | 是否显示中心文字，default = false |
void | refresh() | 刷新视图 |
void | startAnimator() | 开始动画 |
  
### Example：
  ```
  //链式设置
mPieView.setData(list)
                .setCenterText("TIME")
                .setCenterTextSize(60)
                .setCenterTextColor(Color.BLUE)
                .refresh();
  ```
# Code Explain
CSDN :[源码解析](https://blog.csdn.net/qq_40861368/article/details/83716485)
