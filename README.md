# Mirai点歌插件
用mirai搜索音乐平台并分享音乐卡片。  
如果没有你常用的音乐平台或者你喜欢的音乐外观，欢迎发issue或者pr。    
如果有什么新的功能建议或者bug，也可以发issue，我会尽快查看更新。  
目前支持1.0+和0.5.2的API。旧版源代码在分支[console-0.5.2](https://github.com/khjxiaogu/MiraiSongPlugin/tree/console-0.5.2)   
![GitHub All Releases](https://img.shields.io/github/downloads/khjxiaogu/MiraiSongPlugin/total?label=%E4%B8%8B%E8%BD%BD%E9%87%8F&style=social)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/khjxiaogu/MiraiSongPlugin?label=%E5%BD%93%E5%89%8D%E7%89%88%E6%9C%AC)
![GitHub stars](https://img.shields.io/github/stars/khjxiaogu/MiraiSongPlugin?style=social)
# 注意
- 由于tx有某种风控机制，新注册的账号发送XML卡片可能出现发不出去的情况，请用电脑QQ之类的登录几天再发，具体时间取决于TX机制，与程序无关，望周知。
- 本插件均用新旧版本的Mirai测试过，如果不能运行请先确认你mirai为最新版本。   
- 语音功能需要调用命令行，可能有一些问题。  
- 由于各大音乐平台随时可能更新API，导致API失效，所以不保证所有API拿到手的时候均能正常使用。
- 本插件不会提供账户登录Cookie的功能，也不会设法跳过音乐平台的VIP限制。
# 声明
- 本插件仅作为学习交流等使用，请勿用于盈利，否则法律后果自负。
- 本插件提供的所有API均来源于公开资料，如有侵权请发邮件或者issue联系进行删除。
- 使用该插件带来的任何后果均由使用者承担，作者概不负责。
- 如果遇到本插件任何问题，请发issue询问，其他平台概不受理。
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
- 2.7-M1-dev-5
### 测试可用的mirai-core版本
- 1.2.1-2.7(每个发行版本都测试过)
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

“#网易电台 关键词”
-----
搜索网易云电台，一般来说是直接选择找到的第一个节目，但是关键词可以以 “电台名称|节目名称”的格式指定电台节目

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
|来源|QQ音乐<br>酷狗<br>千千<br>网易<br>网易HQ<br>网易电台<br>网易电台节目<br>Bilibili<br>本地|设定搜索歌曲的来源|
|外观|LightApp:小程序分享<br>Mirai:采用Mirai的MusicShare卡片，如果不存在则fallback为XML卡片<br>XML:卡片分享<br>Share:普通分享(不能播放)<br>Message:以纯信息形式分享，可以很方便取得音乐的各种链接。<br>AMR:AMR语音，需要配置好`ffmpeg_path`<br>Silk:SILK语音，需要同时配置好`silkenc_path`和`ffmpeg_path`，由于tx限流，质量可能很差（不推荐使用）|设定分享出来的音乐的外观|
# 歌曲API说明
- 网易电台和网易电台节目的区别是网易电台不能用“电台名称&#124;节目名称”的格式。
- 网易HQ的API和QQ音乐API均可能有时间限制，长时间后播放链接会失效。
- bilibili的API由于有请求校验，所以只支持语音播放，卡片分享只支持点击卡片打开网页进行播放。
所有API均由网络提供，本项目
# 配置项
|名称|介绍|
|-----|-----|
|`silkenc_path`|silk编码器文件位置[windows二进制](https://github.com/khjxiaogu/MiraiSongPlugin/blob/master/silk_v3_encoder.exe)|
|`ffmpeg_path`|ffmpeg编码器文件位置[ffmpeg github](https://github.com/FFmpeg/FFmpeg)|
|`amrqualityshift`|如果语音文件过大时进行的处理，缺省默认为false。<br>设置值：true/不断降低码率直到刚好能够发送，比较消耗性能 false/直接裁剪音频文件大小为1M|
|`amrwb`|是否启用amr_wb模式，缺省默认为true。<br>设置值：true/启用amr_wb，音质会比较好，但是电脑qq可能不能正常播放，手机qq进度条显示异常。 false/关闭amr_wb，此时`amrqualityshift`强制为false，音质会比较差，但是显示和播放都正常。|
|`use_custom_ffmpeg_command`|是否启用自定义ffmpeg指令，如果启用，上述的amr配置和ffmpeg路径配置将被忽略。|
|`custom_ffmpeg_command`|自定义ffmpeg命令，会把%input%替换为输入文件绝对路径，%output%替换为输出文件绝对路径。|
|`enable_local`|全部搜索时是否包含本地搜索，默认不包含。|
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
|source|QQ音乐<br>网易<br>网易HQ<br>酷狗<br>千千<br>本地<br>Bilibili<br>all|设定搜索歌曲的来源<br>注意：all为搜索全部平台。|
|card|LightApp:小程序分享<br>Mirai:采用Mirai的MusicShare卡片，如果不存在则fallback为XML卡片<br>XML:卡片分享<br>Share:普通分享(不能播放)<br>Message:以纯信息形式分享，可以很方便取得音乐的各种链接。<br>AMR:AMR语音，需要配置好`ffmpeg_path`<br>Silk:SILK语音，需要同时配置好`silkenc_path`和`ffmpeg_path`，由于tx限流，质量可能很差（不推荐使用）|设定分享出来的音乐的外观|  

如果不需要原版的指令，可以设置配置项`adddefault`为false。  
原版的指令设置，仅供参考。__#点歌是特殊程序实现的，无法通过配置实现！__  
```
  "#音乐": 
    source: all
    card: Mirai
  "#语音": #指令名称
    source: all #搜索来源
    card: AMR #分享外观
  "#外链": 
    source: all
    card: Message
  "#QQ": 
    source: QQ音乐
    card: Mirai
  "#网易": 
    source: 网易
    card: Mirai
  "#酷狗": 
    source: 酷狗
    card: Mirai
  "#千千": 
    source: 千千
    card: XML
  "#网易电台": 
    source: 网易电台节目
    card: Mirai
```
# 关于本地搜索(实验性)
本地搜索要在配置里面启用，否则搜索全部源不会搜索此项！  
插件运行之后会在mirai目录下产生`SongPluginLocal.json`文件，打开它就可以看到编辑范例为：  
```
[
{"title":"标题",
"desc":"副标题",
"previewUrl":"专辑图片url",
"musicUrl":"音乐播放url",
"jumpUrl":"点击跳转url",
"source":"本地"
}
]
```
格式为json数组，每个成员都必须是对象，最后不能有多余的,。如果指定了本地搜索或者全部搜索，那么就会通过标题的相似度筛选出要搜索的歌，并把所有参数填入分享卡片中分享。  
__#注意！这里的url必须是可以从外部访问的，请自备服务器或者云盘等，不能直接指定本地文件路径！__
