# Mirai点歌插件
用mirai搜索音乐平台并分享音乐卡片。  
如果没有你常用的音乐平台或者你喜欢的音乐外观，欢迎发issue或者pr。    
如果有什么新的功能建议或者bug，也可以发issue，我会尽快查看更新。  
0.5.2已经不再受到支持。  
目前仅支持1.0+的API，如需使用旧版，可以从0.1.5版本前的release下载。  
![GitHub All Releases](https://img.shields.io/github/downloads/khjxiaogu/MiraiSongPlugin/total?label=%E4%B8%8B%E8%BD%BD%E9%87%8F&style=social)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/khjxiaogu/MiraiSongPlugin?label=%E5%BD%93%E5%89%8D%E7%89%88%E6%9C%AC)
![GitHub stars](https://img.shields.io/github/stars/khjxiaogu/MiraiSongPlugin?style=social)
# 特色
- 支持各大国内音乐平台  
- 在非语音模式下，网络和性能占用极低  
- 支持通过配置文件添加自定义的指令和覆盖现有指令 [传送门](#自定义指令)  
- 支持高度自定义，采用MVC模式，添加音乐源和外观成本极低 [javadoc](https://khjxiaogu.github.io/MiraiSongPlugin/)  
# 基本使用方法
0. 从Release下载
1. 放置于plugins文件夹
2. 安装ffmpeg(如果不需要用语音功能可以跳过这步):<br>Windows:下载[ffmpeg](https://github.com/khjxiaogu/MiraiSongPlugin/tree/master/ffmpeg)的两个文件，放置于mirai同一目录<br>Linux: 配置data/MiraiSongPlugin/config.yml的ffmpeg路径为ffmpeg路径
3. 运行mirai，登录机器人
4. 在机器人所在群聊发送“#音乐 test”，机器人返回分享标签即为安装成功。
# 默认指令列表  
 
“#音乐 关键词”
-----
自动搜索所有源以找出来找最佳音频来源    

“#语音 关键词”  
-----
自动搜索所有源，以语音信息的形式发出

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
|`amrqualityshift`|如果语音文件过大时进行的处理，缺省默认为false。<br>设置值：true/不断降低码率直到刚好能够发送，比较消耗性能 false/直接裁剪音频文件大小为1M|
|`amrwb`|是否启用amr_wb模式，缺省默认为true。<br>设置值：true/启用amr_wb，音质会比较好，但是电脑qq可能不能正常播放，手机qq进度条显示异常。 false/关闭amr_wb，此时`amrqualityshift`强制为false，音质会比较差，但是显示和播放都正常。|
|`adddefault`|是否添加默认指令，缺省默认为true。<br>设置值：true/添加readme所述的指令列表 false/不添加任何指令|
|`extracommands`|通过配置添加新指令的列表，可以完全自定义指令。详见[后文](#自定义指令)|
# 自定义指令
范例：
```
extracommands: 
  "#语音": #指令名称
    source: all #搜索来源
    card: AMR #分享外观
  "#分享": 
    source: QQ音乐 #搜索来源
    card: Share #分享外观
```
|参数|值范围|用途|
|------|------|------|
|source|QQ音乐<br>酷狗<br>千千<br>网易<br>all|设定搜索歌曲的来源<br>注意：all为搜索全部平台。|
|card|LightApp:小程序分享<br>XML:卡片分享<br>Share:普通分享(不能播放)<br>Message:以纯信息形式分享，可以很方便取得音乐的各种链接。<br>AMR:AMR语音，需要配置好`ffmpeg_path`，由于tx限流，质量可能很差<br>Silk:SILK语音，需要同时配置好`silkenc_path`和`ffmpeg_path`，由于tx限流，质量可能很差（不推荐使用）|设定分享出来的音乐的外观|  

如果不需要原版的指令，可以设置配置项`adddefault`为false。  
原版的指令设置，仅供参考。__#点歌是特殊程序实现的，无法通过配置实现！__  
```
  #音乐": 
    source: all
    card: LightApp
  "#外链": 
    source: all
    card: Message
  "#QQ": 
    source: QQ音乐
    card: XML
  "#网易": 
    source: 网易
    card: LightApp
  "#酷狗": 
    source: 酷狗
    card: LightApp
  "#千千": 
    source: 千千
    card: LightApp
```
