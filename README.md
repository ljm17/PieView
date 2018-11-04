# PieView
Pie chart, including animation and data percentage, and text display.
# Effect display
  
# Usages
  //设置数据
  mPieView.setData(list);
  //开启动画,default = false
  mPieView.setShowAnimator(true);
  //设置中间透明圈的透明度,default = 0.4f
  mPieView.setAlpha(0.5f);
  //设置初始绘制角度,default = 0
  mPieView.setStartDegree(0);
  //设置板块凸出距离，default = 30f
  mPieView.setSpace(30f);
  //是否显示百分比,default = true
  mPieView.setDisPlayPercent(false);
  //设置板块字体大小,default = 30
  mPieView.setBlockTextSize(30);
  //设置板块字体颜色,default = Color.WHITE
  mPieView.setBlockTextColor(Color.WHITE);
  //是否显示中心文字,default = false
  mPieView.setShowCenterText(true);
  //设置中心文字，default = "PieView"
  mPieView.setCenterText("FRUITS");
  //设置中心文字大小,default = 50
  mPieView.setCenterTextSize(60);
  //设置中心文字颜色，default = Color.GRAY
  mPieView.setCenterTextColor(Color.GRAY);
  //设置白色内孔占外圆比例，default = 0.5f,设置值应在（0,1）之间
  // 并不是很建议设置，设置不合理可能引起图形错乱
  mPieView.setHoleRadiusPercent(0.5f);
  //设置透明圆占外圆比例，default = 0.6f,设置值应在（0,1）之间，并大于等于白色内孔
  // 并不是很建议设置，设置不合理可能引起图形错乱
  mPieView.setAlphaRadiusPercent(0.6f);
