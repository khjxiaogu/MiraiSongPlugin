# Mirai点歌插件
用mirai搜索音乐平台并分享音乐卡片。  
如果没有你常用的音乐平台或者你喜欢的音乐外观，欢迎发issue或者pr。    
如果有什么新的功能建议或者bug，也可以发issue，我会尽快查看更新。  
主要适配mirai-console-0.5.2，应该目前最多人用的mirai console版本。  
~~主要是因为mirai-console-1.0版本似乎还并不支持自定义事件监听器，所以暂且搁置。~~  
~~不过据称也是可以用的，具体也没测试，反正我用的是0.5.2~~
# 特色
- 支持各大国内音乐平台  
- 在非语音模式下，网络和性能占用极低  
- 支持高度自定义，采用MVC模式，添加音乐源和外观成本极低[javadoc](https://khjxiaogu.github.io/MiraiSongPlugin/)
# 指令列表  
 
“#音乐 关键词”
-----
自动搜索所有源以找出来找最佳音频来源    

“#外链 关键词”  
-----
自动搜索所有源，以外链信息的形式发出

“#QQ 关键词”  
-----
搜索QQ音乐  

“#网易 关键词”  
-----
搜索网易云音乐  

“#酷狗 关键词”
-----
搜索酷狗音乐  

“#千千 关键词”
-----
搜索千千音乐（百度音乐）  

“#点歌 来源 外观 关键词”
-----
实验性API  
高度自定义的点歌方法  
|参数|值范围|用途|
|------|------|------|
|来源|QQ音乐<br>酷狗<br>千千<br>网易|设定搜索歌曲的来源|
|外观|LightApp:小程序分享<br>XML:卡片分享<br>Share:普通分享(不能播放)<br>Message:以纯信息形式分享，可以很方便取得音乐的各种链接。<br>AMR:AMR语音，需要配置好`ffmpeg_path`，由于tx限流，质量可能很差<br>Silk:SILK语音，需要同时配置好`silkenc_path`和`ffmpeg_path`，由于tx限流，质量可能很差（不推荐使用）|设定分享出来的音乐的外观|

# 配置项
|名称|介绍|
|-----|-----|
|`silkenc_path`|silk编码器文件位置[windows二进制](https://github.com/khjxiaogu/MiraiSongPlugin/blob/master/silk_v3_encoder.exe)|
|`ffmpeg_path`|ffmpeg编码器文件位置[ffmpeg github](https://github.com/FFmpeg/FFmpeg)|
