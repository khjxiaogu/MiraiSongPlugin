# Mirai点歌插件
用mirai搜索音乐平台并分享音乐卡片。  
如果没有你常用的音乐平台或者你喜欢的音乐外观，欢迎发issue或者pr。    
如果有什么新的功能建议或者bug，也可以发issue，我会尽快查看更新。  
目前支持1.0+和0.5.2的API。旧版源代码在分支[console-0.5.2](https://github.com/khjxiaogu/MiraiSongPlugin/tree/console-0.5.2)   
![GitHub All Releases](https://img.shields.io/github/downloads/khjxiaogu/MiraiSongPlugin/total?label=%E4%B8%8B%E8%BD%BD%E9%87%8F&style=social)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/khjxiaogu/MiraiSongPlugin?label=%E5%BD%93%E5%89%8D%E7%89%88%E6%9C%AC)
![GitHub stars](https://img.shields.io/github/stars/khjxiaogu/MiraiSongPlugin?style=social)
# 注意
- 由于tx改了小程序机制，疑似音乐卡片采用了某种反篡改验证机制，导致所有的不正确小程序（影响LightApp外观）无法发出，暂时无法修复。欢迎各位大佬前来issue探讨机制或者Pr。  
- 由于tx有某种风控机制，新注册的账号发送XML卡片可能出现发不出去的情况，请用电脑QQ之类的登录几天再发，具体时间取决于TX机制，与程序无关，望周知。
- 本插件均用新旧版本的Mirai测试过，如果不能运行请先确认你mirai为最新版本。   
- 语音功能需要调用命令行，可能有一些问题。
### 测试可用的mirai-console版本
#### 0.5.2版本
- 0.5.2
#### 1.0版本
- 1.0-M3
- 1.0-M4
- 1.0-M4-dev3
- 1.1.0
#### 2.0版本    
- 2.0-M2-1-dev-1
### 测试可用的mirai-core版本
- 1.2.1-1.3.3(每个发行版本都测试过)
# 特色
- 支持各大国内音乐平台  
- 在非语音模式下，网络和性能占用极低  
- 支持通过配置文件添加自定义的指令和覆盖现有指令 [传送门](#自定义指令)  
- 支持高度自定义，采用MVC模式，添加音乐源和外观成本极低 [javadoc](https://khjxiaogu.github.io/MiraiSongPlugin/)  
# 基本使用方法
0. 从[Release](https://github.com/khjxiaogu/MiraiSongPlugin/releases)下载
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
|来源|QQ音乐<br>酷狗<br>千千<br>网易<br>网易HQ|设定搜索歌曲的来源|
|外观|LightApp:小程序分享<br>XML:卡片分享<br>Share:普通分享(不能播放)<br>Message:以纯信息形式分享，可以很方便取得音乐的各种链接。<br>AMR:AMR语音，需要配置好`ffmpeg_path`<br>Silk:SILK语音，需要同时配置好`silkenc_path`和`ffmpeg_path`，由于tx限流，质量可能很差（不推荐使用）|设定分享出来的音乐的外观|

# 配置项
|名称|介绍|
|-----|-----|
|`silkenc_path`|silk编码器文件位置[windows二进制](https://github.com/khjxiaogu/MiraiSongPlugin/blob/master/silk_v3_encoder.exe)|
|`ffmpeg_path`|ffmpeg编码器文件位置[ffmpeg github](https://github.com/FFmpeg/FFmpeg)|
|`amrqualityshift`|如果语音文件过大时进行的处理，缺省默认为false。<br>设置值：true/不断降低码率直到刚好能够发送，比较消耗性能 false/直接裁剪音频文件大小为1M|
|`amrwb`|是否启用amr_wb模式，缺省默认为true。<br>设置值：true/启用amr_wb，音质会比较好，但是电脑qq可能不能正常播放，手机qq进度条显示异常。 false/关闭amr_wb，此时`amrqualityshift`强制为false，音质会比较差，但是显示和播放都正常。|
|`verbose`|是否启用命令执行输出，缺省默认为true。<br>设置值：true/执行语音操作时在控制台输出详细信息 false/不输出信息，直接执行|
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
|source|QQ音乐<br>网易<br>网易HQ<br>酷狗<br>千千<br>all|设定搜索歌曲的来源<br>注意：all为搜索全部平台。|
|card|LightApp:小程序分享<br>XML:卡片分享<br>Share:普通分享(不能播放)<br>Message:以纯信息形式分享，可以很方便取得音乐的各种链接。<br>AMR:AMR语音，需要配置好`ffmpeg_path`，由于tx限流，质量可能很差<br>Silk:SILK语音，需要同时配置好`silkenc_path`和`ffmpeg_path`，由于tx限流，质量可能很差（不推荐使用）|设定分享出来的音乐的外观|  

如果不需要原版的指令，可以设置配置项`adddefault`为false。  
原版的指令设置，仅供参考。__#点歌是特殊程序实现的，无法通过配置实现！__  
```
  "#音乐": 
    source: all
    card: LightApp
  "#语音": #指令名称
    source: all #搜索来源
    card: AMR #分享外观
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
