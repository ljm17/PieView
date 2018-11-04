# PieView
Pie chart, including animation and data percentage, and text display.
# Effect display
  ![image](https://github.com/ljm17/PieView/raw/master/images/img2.png)<br/>
  ![image](https://github.com/ljm17/PieView/raw/master/images/img1.png)<br/>
  ![image](https://github.com/ljm17/PieView/raw/master/images/img3.png)<br/>
# Usages
  //设置数据  <br/>
  mPieView.setData(list);<br/>
  //开启动画,default = false<br/>
  mPieView.setShowAnimator(true);<br/>
  //设置中间透明圈的透明度,default = 0.4f<br/>
  mPieView.setAlpha(0.5f);<br/>
  //设置初始绘制角度,default = 0<br/>
  mPieView.setStartDegree(0);<br/>
  //设置板块凸出距离，default = 30f<br/>
  mPieView.setSpace(30f);<br/>
  //是否显示百分比,default = true<br/>
  mPieView.setDisPlayPercent(false);<br/>
  //设置板块字体大小,default = 30<br/>
  mPieView.setBlockTextSize(30);<br/>
  //设置板块字体颜色,default = Color.WHITE<br/>
  mPieView.setBlockTextColor(Color.WHITE);<br/>
  //是否显示中心文字,default = false<br/>
  mPieView.setShowCenterText(true);<br/>
  //设置中心文字，default = "PieView"<br/>
  mPieView.setCenterText("FRUITS");<br/>
  //设置中心文字大小,default = 50<br/>
  mPieView.setCenterTextSize(60);<br/>
  //设置中心文字颜色，default = Color.GRAY<br/>
  mPieView.setCenterTextColor(Color.GRAY);<br/>
  //设置白色内孔占外圆比例，default = 0.5f,设置值应在（0,1）之间<br/>
  // 并不是很建议设置，设置不合理可能引起图形错乱<br/>
  mPieView.setHoleRadiusPercent(0.5f);<br/>
  //设置透明圆占外圆比例，default = 0.6f,设置值应在（0,1）之间，并大于等于白色内孔<br/>
  // 并不是很建议设置，设置不合理可能引起图形错乱<br/>
  mPieView.setAlphaRadiusPercent(0.6f);<br/>
