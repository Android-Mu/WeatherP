# WeatherP
简单天气查询APP，供练手使用(来自于第一行代码一书)

### 效果图

![](./_image/device-2017-09-17-194832.png)

           ![](./_image/device-2017-09-17-194914.png)

![](./_image/device-2017-09-17-194941.png)          
 ![](./_image/device-2017-09-17-195144.png)

### 数据与技术
#### 技术
- LitePal1.6.0
- Glide4.0
- Gson2.8.1
- okgo3.0.4
- 本地json
#### 数据
- 必应每日一图
- 和风天气个人免费数据

### 整体结构与实现思路
#### 结构
首界面展示所有省级名称，点击进入对应的市级界面，再次点击进入对应的区/县级界面，点击某个区/县则进入天气展示界面。在这个界面可以通过侧滑选择别的城市切换天气展示。
可下拉刷新，实现天气更新。

#### 实现思路
省市级数据是本地的 json，天气数据需要网络获取，获取之后从入本地数据库，下次进入就会优先从数据库读取，图片数据也是一样的。
另外增加了后台更新天气的功能，当打开天气界面时，就会启动一个服务，每隔 8 小时会更新一次天气数据。

### 注意的问题
- 使用 LitePal 存储数据时，由于直接使用 GsonFormat 生成实体类，城市的id使用了 String 类型，导致存入 id 一直失败，修改为 Integer 就好了。
