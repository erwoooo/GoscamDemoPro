# Android Sdk

## 门铃强拆告警

```json
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap47",
        "DeviceParam": {
          "a_doorbell_remove_alarm": 1
        }
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

| Field name    | Type    | Describe                                     |
| -------- | ------- | -------------------------------------- |
| CMDType  | String  |cap47                  |
| a_doorbell_remove_alarm   | Integer | 启用设备（门铃设备）防拆卸报警，值：0-OFF 1-ON |

## 省电模式

```json
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap48",
        "DeviceParam": {
          "a_doorbell_lowpower": 1
        }
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

| Field name    | Type    | Describe                                     |
| -------- | ------- | -------------------------------------- |
| CMDType  | String  |cap48                  |
| a_doorbell_lowpower   | Integer | 启用设备（低功耗设备）节能模式，值：0-OFF 1-ON |

## 讲机

内部通信需要调用sdk函数原生int NativeStartTalk（int通道，String psw）

| Field name | Type    | Describe |
| ---------- | ------- | -------- |
| channel    | Integer | 0        |
| psw        | String  | 流密码   |



## 音量设置 （device）

```json
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap53",
        "DeviceParam": {
          "volume": 0
        }
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

| Field name    | Type    | Describe                                     |
| -------- | ------- | -------------------------------------- |
| CMDType  | String  |cap53                  |
| volume   | Integer | 设备音量、值: 0-100 |

## 录制持续时间

```json
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap17",
        "DeviceParam": {
          "un_duration": 12
        }
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- |
| CMDType     | String  | cap17                        |
| un_duration | Integer | 事件记录的单个文件的持续时间；当前值：6、9、12、15 |

## 拍照

* nativeCapture 

  

* | Field name | Type   | Describe                                          |
  | ---------- | ------ | ------------------------------------------------- |
  | filePath   | String | 照片保存路径                                      |
  | port       | long   | GosMediaPlayer.getPort() 为每个解码器创建一个端口 |

## 录制视频

nativeStartRecord

| Field name | Type    | Describe                                           |
| ---------- | ------- | -------------------------------------------------- |
| port       | long    | GosMediaPlayer.getPort()  为每个解码器创建一个端口 |
| filePath   | String  | 文件保存路径                                       |
| flag       | Integer | 标签                                               |





## 视频质量

nativeAVSwsScale 

| Field name | Type    | Describe                                           |
| ---------- | ------- | -------------------------------------------------- |
| port       | long    | GosMediaPlayer.getPort()  为每个解码器创建一个端口 |
| nEnable    | Integer | 0-OFF,1-ON                                         |
| nWidth     | Integer | 修改宽度                                           |
| nHeight    | Integer | 修改高度                                           |

## 回看

* TF file

### 获取当月文件

```json
{
  "Body": {
    "DeviceId": "Z99G82100000049",
    "DeviceParam": {
      "partion_index": 0,
      "CMDType": 971,
      "channel": 0
    },
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "BypassParamRequest"
}
```

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- | 
| partion_index | Integer | 0|

### Rseponse

    * please refer to 'GetFileForMonthResult' 

```json
{
  "fileStatus": 0,
  "totalNum": 1,
  "currNo": 1,
  "mothFile": [
    {
      "monthTime": "",
      "fileNum": 1
    }
  ]
}
```

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- |
| fileStatus | Integer | on use |
| totalNum | Integer | 总数量 |
| currNo | Integer | no use |
| monthTime | Integer | 存在文件的时间 |
| fileNum | Integer |  0- no file|

### 获取当天的文件

    * NativeGetRecDayEventRefresh

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- |
| channel | Integer | 0|
| time | Integer | 当前时间 |
| type | Integer | 0-正常记录，1-图表记录 |
| count | Integer | 0  |
| partionIndex | Integer | 0|

### Response

    * please refer to 'GetRecDayEventRefreshResult'

```json
{
  "result": 0,
  "total_num": 1,
  "page_num": 1,
  "day_event_list": [
    {
      "start_time": 1597116179,
      "end_time": 1597116193,
      "type": 1
    }
  ]
}
```

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- |
| total_num | Integer | 总页数 |
| page_num | Integer | 当前页面 |
| start_time | Integer | 开始时间 |
| end_time | Integer | 结束时间 |
| type | Integer | 0-正常记录，1-图表记录 |

## 相册

* 照片和视频存储在手机的内存中

## 电池状态

```json
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap29",
        "DeviceParam": {
          "battery_level": 12,
          "charging": 1
        }
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- |
| CMDType     | String  | cap29                        |
| battery_level | Integer | 当前电池百分比; value：0-100 |
| charging | Integer | 充电状态1-充电，0-怠速 |

## 麦克风

    * microphone?

```json
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap25",
        "DeviceParam": {
          "device_mic_switch": 1
        }
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- |
| device_mic_switch | int | 0-OFF, 1-ON |

## 蜂窝移动网络（自动）

```json
{
  "MessageType": "AppGetDeviceParamRequest",
  "Body": {
    "DeviceId": "A99UB210PL4V57E",
    "ParamArray": [
      {
        "CMDType": "cap14"
      }
    ]
  }
}
```

```json
{
  "MessageType": "AppGetDeviceParamResponse",
  "Body": {
    "DeviceId": "A99UB210PL4V57E",
    "ParamArray": [
      {
        "CMDType": "cap14",
        "DeviceParam": {
          "a_SSID": "test",
          "a_number": "test",
          "a_imei": "test",
          "a_iccid": 0,
          "a_center": "test",
          "a_passwd": "test",
          "un_signal_level": 80
        }
      }
    ]
  }
}
```

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- |
| a_SSID    | String    | ssid |
| a_center    | String    | Operator Name |
| un_signal_level    | int    | 0-100 |

## 红外探测

### Set Pir

```json
{
  "Body": {
    "ParamArray": [
      {
        "CMDType": "cap6",
        "DeviceParam": {
          "un_switch": 0,
          "un_sensitivy": 100,
          "un_stay": 0,
          "un_cdtime": 10
        }
      }
    ]
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

### Get Pir

```json
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap6"
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppGetDeviceParamRequest"
}
```

### Response

```json
{
  "Body": {
    "ParamArray": [
      {
        "CMDType": "cap6",
        "DeviceParam": {
          "un_switch": 0,
          "un_sensitivy": 100,
          "un_stay": 0,
          "un_cdtime": 10
        }
      }
    ]
  },
  "MessageType": "AppSetDeviceParamResponse"
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap6 & Ulife_HWPir_en|
| un_switch    | Integer | pir开关0-OFF，1-ON          |
| un_sensitivy | Integer | PIR 灵敏度 20-40-60-80-100                      |
| un_stay      | Integer | 逗留模式：level 0-3                                |
| un_cdtime    | Integer | PIR检测持续时间设置：second               |

```C
typedef enum{
	ULIFE_HWPIR_NONE,				// PIR parameter setting not supported
	ULIFE_HWPIR_SWITCH 		= 1,	// PIR parameter setting  supported
	ULIFE_HWPIR_SENSITIVITY	= 2,	// Support for PIR sensitivity settings
	ULIFE_HWPIR_STAY_MODE	= 4,	// Supports PIR un_stay mode settings
    ULIFE_HWPIR_CDTIME		= 8,	// Support for PIR cooling time setting
}Ulife_HWPir_en;
```

# 设备能力集

## 能力集请求结构

* ParamArray可以一次设置多个设置
  </br> 当我们获得设备列表时，我们将获得设备的能力集`DeviceEntity`
  </br>从`DeviceEntity.cap获取所有设备功能。我们使用设备。parse（Cap-Cap）`到
  解析设备能力集，获得的参数都是int类型
* </br> 通过`com.gos.platform.api.devparam设置设备功能。DevParam。DevParamCmdType`
  </br>如果你想将参数设置为设备，你可以参考“DevParamCmdType”，这个类在
  SDK显示设备可以设置的参数
* </br> 我们得到“cap20”是整数，我们将设备参数设置为用户“cap20”只是一个键

```json
{
  "Body": {
    "ParamArray": [
      {
        "CMDType": "capxxx",
        "DeviceParam": {
        }
      },
      {
        "CMDType": "capxxx",
        "DeviceParam": {
        }
      },
      {
        "CMDType": "capxxx",
        "DeviceParam": {
        }
      }
    ]
  },
  "MessageType": "AppSetDeviceParamRequest"
}
```

## cap1

* 设备类型

## cap2

* 主流分辨率大小宽度：高16位高度：低16位
* `com.gos.platform.api.devparam.VideoQualityParam`

```json
{
  "un_video_quality": 1,
  "video_resolution": [
    {
      "src_width": 1280,
      "src_height": 720,
      "dest_width": 1920,
      "dest_height": 1080
    }
  ]
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap2 |
| un_video_quality    | Integer | 当前直播分辨率指标值，对应分辨率的video_resolution下标 |
| src_width | Integer | 视频源宽度                     |
| src_height      | Integer | 视频源高度                            |
| dest_width    | Integer | APP显示视频宽度                  |
| dest_height    | Integer | APP显示视频高度                  |

## cap3

* 子码流（未使用）

## cap4

* 第三通道码流（未使用）

## cap5

* 是否有加密IC 0：否1：是
  ``
  cap.cap5 == 1
  ``

## cap6

* 是否有PIR传感器，0：无，1：有
    * `com.gos.platform.api.devparam.PirSettingParam`

  ``
  cap.cap6 == 1
  ``
  表示支持pir，详见“红外探测”`

## cap7

* 是否有云台
    * `com.gos.platform.api.devparam.PtzPositionPresetSelectedParam`

<br> 0-无1-支持云台控制
<br> 2- 支持云台控制和预设位置
<br> 3-支持云台控制/预设和巡航控制
<br> 4- 支持云台控制/预设和隐私模式
<br> 5-支持云台控制/预设位置和自检
<br> 32- 全功能云控制台

```json
{
  "Privacy": 1,
  "Active": {
    "x": 123,
    "y": 123
  }
}

```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap7 |
| Privacy    | Integer | 是否使用隐私模式 0-OFF,1-ON |
| x | Integer | 可以为点x预设的坐标值              |
| y      | Integer | Coordinate values that can be preset for point y                    |

* `注意：当APP启用预设位时，必须先关闭隐私模式；设备已收到预设设置，将被迫关闭隐私模式`
  <br>  设置隐私模式时，预设点活动坐标值被强制设置为-1;<br> 设置预设点时，需要先取消隐私模式;

### cap7-1

* `com.gos.platform.api.devparam.PtzPositionPresetParam`

```json
{
  "Preset": [
    {
      "index": 1,
      "position": {
        "x": 123,
        "y": 123
      },
      "text": "门口",
      "picture": "https://xxxx"
    },
    {
      "index": 2,
      "position": {
        "x": 123,
        "y": 123
      },
      "text": "门口",
      "picture": "https://xxxx"
    }
  ]
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap7-1 |
| position | Object | 预设点坐标值                   |
| x | Integer | 可以为点x预设的坐标值              |
| y      | Integer | 可以为点y预设的坐标值        |
| index      | Integer | 唯一标识              |
| text      | String | 文本描述              |
| picture      | String | 预览url              |

## cap8

* 是否有麦克风
  ``cap.cap8 == 1``

## cap9

* 是否有扬声器
  ``cap.cap9 == 1``

## cap10

* 是否支持SD卡插槽
  ``cap.cap10 == 1``

## cap11

* 是否有温度感应探头
  ``cap.cap11 == 1``

## cap12

* 是否支持同步时区1-仅支持正时区 2-支持一半时间
  区
* ``cap.cap12 > 0`` support
* `com.gos.platform.api.devparam.TimeZoneInfoParam`

```json

{
  "AppTimeSec": 28800,
  "un_TimeZone": 8
}

```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap12 |
| AppTimeSec | Integer | 当前时间戳                    |
| un_TimeZone      | Integer | timeZone(-12~11)                  |

## cap13

* 是否支持夜视0：否1：是2：全彩夜视
  `cap.cap13 > 0` support

* `com.gos.platform.api.devparam.NightSettingParam`

```json
{
  "un_auto": 0,
  "un_day_night": 0
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap13 |
| un_auto | Integer | 是否启用自动模式/智能夜视，值为0-关闭1-打开 |
| un_day_night      | Integer | 黑白模式，夜视模式在自动模式关闭时有效，值为0-第1天-夜视 |
| un_day_night      | Integer | 全色模式，夜视模式在自动模式关闭时有效，值范围从0-黑白到1-全色 |

* 普通夜视和全彩夜视共同协议；

## cap14

* 是否支持0:wifi 1有线2:wifi和有线的网卡
  *详见“蜂窝移动网络（自动）”

<br> 0 支持WIFI
<br> 1 有线
<br> 2 2.4GWIFI+有线
<br> 3 5G WIFI
<br> 4 2.4G/5G 双频 WIFI
<br> 5 4G流量
<br> 6 2.4G/5G 双频  WIFI+有线

* `com.gos.platform.api.devparam.CurrentWifiInfoParam`

## cap15

* 是否支持智能扫描0表示不支持，1表示7601智能，2表示
  代表8188市场
* no used

## cap16

* 是否支持移动检测0不支持1。支持2。支持自选区域。3.
  支持自定义时间
* `cap.cap16 > 0` support


* `com.gos.platform.api.devparam.MotionDetectionParam`

```json
{
  "c_sensitivity": 30,
  "c_switch": 1,
  "un_width": 14,
  "un_height": 14,
  "un_mode": 0,
  "un_submode": 0,
  "un_enable": 0,
  "s_threshold": 0,
  "rect_x": 0,
  "rect_y": 0,
  "custom_time_period": [],
  "un_enable_str": []
}

```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap16 |
| c_sensitivity | Integer | 值域（1~100）是运动检测级别的阈值。值越小，灵敏度越高。1（最大）~100（最小）：（关闭：100，低：90，中：60，高：30） |
| c_switch      | Integer | 开关  0-Off 1-On            |
| un_width      | Integer |                  |
| un_height      | Integer |                 |
| s_threshold      | short |       no used          |
| un_mode      | Integer | 手动分割坐标0或自动多屏幕分割坐标1 |
| un_submode      | Integer | 1x1=0, 2x2=1, 3x3=2, 4x4=3 在多屏幕模式下*        |
| un_enable      | Integer | 根据是否在多屏幕模式下为所选区域启用最多4x4=16* |
| rect_x      | Integer | 手动在水平轴方向上的区域数量（rect_x*rect_y<=200） |
| rect_y      | Integer | 手动在垂直坐标方向上的区域数量 |
| custom_time_period      | String Array |                   |
| un_enable_str      |  Array | 是否为每个区域启用了运动检测（JSON格式在大小为rect_x*rect_y的数组中传递） |

## cap17

* 设置视频录制的持续时间
  `cap.cap17 == 1`

* `com.gos.platform.api.devparam.RecordDurationParam`
    * see detail by `Recording Duration`

## cap18

* 设置照明开关
  `cap.cap18 == 1`

## cap19

* 声音检测报警
  `cap.cap19 == 1`

* `com.gos.platform.api.devparam.SoundDetectionParam`

```json
{
  "un_switch": 0,
  "un_sensitivity": 0
}

```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap19 |
| un_switch | Integer | 是否启用运动跟踪；值：0关闭1打开 |
| un_sensitivity      | Integer     |检测灵敏度，值：1-低，2-中，3-高|

## cap20

* 摇篮曲
  `cap.cap20 > 0`  no used

## cap21

* 是否有电池
  `cap.cap21 == 1`

## cap22

* 是否支持WIFI远程唤醒
  `cap.cap22 == 1` support

## cap23

* 是否有状态指示灯
  `cap.cap23 == 1`

* `com.gos.platform.api.devparam.LedSwitchParam`

```json
{
  "device_led_switch": 1
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap23 |
| device_led_switch | Integer |   Value: 0-Off 1-On            |

## cap24

* 是否支持关闭摄像头
  `cap.cap24 > 0`
  <br> 0 does not support switches,
  <br>1 supports switches,
  <br>2 supports switches and plans
    * `com.gos.platform.api.devparam.DeviceSwitchParam`

```json

{
  "device_switch": 0
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap24 |
| device_switch | Integer |   Value: 0-Off 1-On                 |

## cap25

* 是否支持麦克风开关
  `cap.cap25 == 1`

* `com.gos.platform.api.devparam.MicSwitchParam`
* see detail by `Speaker`

## cap26

* 是否支持云存储0:否1:阿里云2:亚马逊3:中国联通
  `cap.cap26 > 0`

## cap27

* 是否支持视频流加密
  `cap.cap27 == 1`

## cap28

* 是否播放TF流

## cap29

* 是否支持电力采集
  `cap.cap29 == 1` support
    * `com.gos.platform.api.devparam.BatteryInfoParam`
      <br> see detail by `Battery Status`

## cap30

* 获取网关模型
  `cap.cap30 == 1`

## cap31

* 是否支持alexa功能
  <br> `cap.cap31 == 1 || cap.cap31 == 3`  支持AlexaSkills
  <br> `cap.cap31 == 2 || cap.cap31 == 3`  支持GoogleHome

## cap32

* 支持视频录制旋转 0。不支持1。支持的
  `cap32 == 1`

* `com.gos.platform.api.devparam.MirrorModeSettingParam`

```json
{
  "mirror_mode": 1
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap32 |
| device_switch | Integer |  0-无翻转1-水平翻转2-垂直翻转3-水平垂直翻转  |

## cap33

* 湿度

## cap34

* 温度

## cap35

* 是否支持门铃铃声设置
  `cap.cap35 == 1`

* `com.gos.platform.api.devparam.DoorbellVolumeParam`

```json
{
  "doorbell_ring_switch": 0,
  "volume_level": 0
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap35 |
| doorbell_ring_switch | Integer | 振铃开关0-OFF 1-ON |
| volume_level | Integer | 音量级别和开关0-100 |

## cap36

* 门铃LED灯开关
  `cap.cap36 == 1`  no used

## cap37

* 是否支持摄像头切换方案
  `cap.cap37 == 1`

* `com.gos.platform.api.devparam.CameraSwitchPlanSettingParam`

```json
{
  "enable": 0,
  "repeat": 1,
  "start_time": 0,
  "end_time": 0
}
```

| Field name    | Type    | Describe                                           |
| ------------ | ------- | ------------------------------------------------------ |
| CMDType      | String  | cap37 |
| enable | Integer | 该计划是否激活0-OFF 1-ON |
| repeat | Integer | 计划周期，以位和值表示，如果启用，则位置为1，一周中的七天，天数[7]={“周日”、“周一”、“周二”、“周三”、“周四”、“周五”、“周六”}；0表示一次性效果 |
| start_time | Integer | 使用第二个表达式；例如，8:00时区是28800，这意味着8*60*60 |
| end_time | Integer | |

## cap38

* 是否可以安装传感器
  `cap.cap38 > 0`  support



## cap39 设置哭声检测参数

```
{
    "Body":{
        "ParamArray":[
            {
                "CMDType":"cap39",
                "DeviceParam":{
                    "un_switch":0,
                    "un_sensitivity":20
                }
            }
        ]
    },
    "MessageType":"DeviceParamReportNotify"
}
```

| 字段名称       | 类型    | 描述                              |
| -------------- | ------- | --------------------------------- |
| CMDType        | String  | 对应能力集，固定cap39             |
| un_switch      | Integer | 是否开启侦测；取值：0-关闭 1-开启 |
| un_sensitivity | Integer | 侦测灵敏度，取值：1-100，建议5档  |

## cap40

* 是否支持声光报警
  `cap.cap40 > 0` support

* `com.gos.platform.api.devparam.WarnSettingParam`

```json
{
  "un_switch": 0,
  "schedule": {
    "un_switch": 0,
    "un_repeat": 127,
    "start_time": 0,
    "end_time": 86399
  },
  "audio": {
    "un_switch": 0,
    "un_times": 1,
    "un_volume": 80,
    "un_type": 0,
    "url": "https://xxxx"
  },
  "light": {
    "un_switch": 0,
    "un_duration": 3
  }
}
```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap40                                        |
| un_switch   | Integer | 声光联动总开关，值：0-闭合1-打开      |
| schedule    | Object  | 联动方案                                                |
| un_switch   | Integer | 联动计划开关，值：0-关闭1-打开          |
| un_repeat   | Integer | 计划周期，以位和值表示，如果启用，则位置为1，一周中的七天，天数[7]={“周日”、“周一”、“周二”、“周三”、“周四”、“周五”、“周六”}；0表示一次性效果 |
| start_time  | Integer | 联动计划的开始时间，以秒表示；例如，8:00时区是28800，这意味着8*60*60 |
| end_time    | Integer | 联动计划结束时间                                   |
| audio       | Object  | 联动声音                                               |
| un_switch   | Integer | 联动声音开关，值：0-关 1-开     |
| un_times    | Integer | 链接的声音广播数量                                 |
| un_volume   | Integer | 联动声音播放音量                                  |
| un_type     | Integer | 用于联动声音广播的音频文件序列号                  |
| url         | String  | 联动声音广播音频文件下载链接；音频需要g711A格式的单通道8K采样率，g711A文件头已删除 |
| light       | Object  | 联动照明                                                 |
| un_switch   | Integer | 联动灯开关，值：0-关闭1-打开           |
| un_duration | Integer |联动灯点亮的持续时间                                        |

## cap41

* 是否支持自动重启计划
  `cap.cap41 > 0` support

* `com.gos.platform.api.devparam.ResetSettingParam`

```json
{
  "enable": 0,
  "repeat": 1,
  "start_time": 0
}
```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap41                                        |
| enable   | Integer |0-OFF 1-ON                       |
| repeat    | Integer  | 按位设定计划周期                                           |
| start_time   | Integer | 重启时间点                    |

## cap42

* 宽动态设置

## cap43

* HTTPS OTA升级
  `cap.cap43 == 1` support
* `com.gos.platform.api.devparam.UpgradeStatusParam`

```json
{
  "upgrade_item_type": 1,
  "upgrade_status": 1,
  "upgrade_progress": 80
}
```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap43                                        |
| upgrade_item_type   | Integer |升级模块，请参阅枚举UPGRADE_ITEM_TYPE，非强制性                       |
| upgrade_status    | Integer  | 升级状态，请参阅枚举UPRADE_State_E                           |
| upgrade_progress   | Integer | ota进展                    |

```C
typedef enum
{  
  E_UPGRADE_ERROR     	= 0x72,
  E_UPGRADE_STATION     = 0x73,  
  E_UPGRADE_CAMERA_DSP  = 0x74,
  E_UPGRADE_CAMERA_WIFI = 0x75,
  E_UPGRADE_CAMERA_MCU  = 0x76,  
  E_UPGRADE_STATION_APP = 0x77,  
} UPGRADE_ITEM_TYPE;

typedef enum{
  E_UPGRADE_DOWNLOADING = 0,            //(0)下载
  E_UPGRADE_FAILURE,              		//(1)升级失败
  E_UPGRADE_SUCCESS,                	//(2)升级成功
  E_UPGRADE_DOWNLOAD_START,				//(3)开始下载
  E_UPGRADE_DOWNLOAD_FINISH,			//(4)下载已完成
  E_UPGRADE_CHECK_ERROR,				//(5)升级包验证错误
  E_UPGRADE_INSTALL_START,				//(6)开始安装
}UPRADE_STATE_E;
```

## cap44

* 是否支持铃声设置

## cap45

* 户外门铃LED设置
  `cap.cap45 > 0` support

* `com.gos.platform.api.devparam.DoorbellLedSwitchParam`

```json
{
  "doorbell_led_switch": 1
}
```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap45                                        |
| doorbell_led_switch   | Integer                |是否打开设备（门铃设备）状态指示灯，值为0-关闭1-打开|

## cap46

* 室外门铃铃声设置
* no used

## cap47

* 室外门铃强制拆迁报警
  `cap.cap47 == 1` support

* `com.gos.platform.api.devparam.RemoveAlarmParam`
  <br> see detail by `Tamper Alert`

## cap48

* 省电模式

`cap.cap48 == 1` support

* `com.gos.platform.api.devparam.LowpowerModeSettingParam`
  <br> see detail by `Power saving Mode`

## cap49

* PIR特定设置
* no used

## cap50

`cap.cap50 > 0` support

* 人形侦测报警0:否1:人形侦测报警2:可以按区域选择检测
* `com.gos.platform.api.devparam.SmdAlarmSettingParam`

```json
{
  "permcnt": 1,
  "perms": [
    {
      "pcnt": 4,
      "rect": [
        {
          "x": 0,
          "y": 0
        }
      ]
    }
  ],
  "un_sensitivity": 4,
  "un_switch": 1
}

```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap50                                        |
| un_switch      | Integer | 人体形状检测开关，值：0-Off 1-On |
| un_sensitivity | Integer | 灵敏度，从低到高1,2,3,4,5 |
| permcnt        | Integer | 区域数量                      |
| perms          | Array   | 所有地区的详细信息             |
| pcnt           | Integer | 单个区域中的坐标点数量     |
| rect           | Array   | 单个区域坐标点的详细信息    |
| x              | Integer | 坐标点x坐标值，范围从0到10000 |
| y              | Integer | 坐标点y坐标值，范围从0到10000 |

## cap51

* 物体跟踪
  `cap.cap51 > 0` support

* `com.gos.platform.api.devparam.ObjTrackSettingParam`

```json
{
  "un_switch": 0
}
```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap51                                        |
| un_switch      | Integer | 是否启用物体跟踪；值：0关闭1打开 |

## cap52

* 人形追踪
  `cap.cap52 > 0` support

* `com.gos.platform.api.devparam.HumanTrackSettingParam`

```json
{
  "un_switch": 0
}
```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap52                                        |
| un_switch      | Integer | 是否启用人形追踪；值：0关闭1打开 |

## cap53

* 设备音量设置
  `cap.cap53 > 0` support

* `com.gos.platform.api.devparam.VolumeSettingParam`

```json
{
  "volume": 0
}
```

| Field name    | Type    | Describe                                           |
| ----------- | ------- | ------------------------------------------------------------ |
| CMDType     | String  | cap53                                        |
| volume      | Integer | 设备（通用设备）提示音量，范围从0到100 |

## cap54

* 推送时间间隔设置
  `cap.cap54 > 0` support


* `com.gos.platform.api.devparam.PushIntervalSettingParam`

```json
{
  "interval": 0,
  "motion_detection_switch": 0,
  "sound_detection_switch": 0,
  "person_detection_switch": 1,
  "cry_alarm_switch": 1,
  "temperature_alarm_switch": 1,
  "pir_alarm_switch": 1,
  "schedule": {
    "enable": 0,
    "repeat": 1,
    "start_time": 0,
    "end_time": 28800
  }
}

```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| CMDType                  | String  | cap54                                        |
| interval                 | Integer | 推送间隔                                                 |
| motion_detection_switch  | Integer | 运动检测报警按钮开关，值：0-Off 1-On |
| sound_detection_switch   | Integer | 声音报警按钮开关，值：0-Off 1-On  |
| person_detection_switch  | Integer | 人形侦测报警按钮开关，值：0-关闭1-打开 |
| cry_alarm_switch         | Integer | 哭声检测报警按钮开关，值：0-Off 1-On |
| temperature_alarm_switch | Integer | 温度报警按钮开关，值：0-关闭1-打开     |
| pir_alarm_switch         | Integer | PIR报警按钮开关，值：0-Off 1-On   |
| enable                   | Integer | 计划是否已激活                                         |
| repeat                   | Integer | 计划周期，以位和值表示，如果启用，则位置为1，一周中的七天，天数[7]={“周日”、“周一”、“周二”、“周三”、“周四”、“周五”、“周六”}； |
| start_time               | Integer | 使用第二个表达式；例如，8:00时区是28800，这意味着8*60*60 |
| end_time                 | Integer |                                              |

## cap55

* P2P加密的能力集
  `cap.cap55`
    * no used

## cap56

* 设备支持流类型1:TUTK 2:P2P 3:TCP 4:P2P_TCP（全部支持）
  `cap.cap56 = 2`

## cap57

* 对讲机类型0：半双工1：全双工2：支持半双工和全双工 3：声网
  `cap.cap57 = 1`

## cap58

* 是否为一拖多
  `cap.cap58 == 1`
    * the doorbell no used

## cap59

* 设备音频类型0:AAC 1:G711A
  `cap.cap59 = 0`

## cap60

* AI访问0:无访问1:访问服务器版本2:访问设备版本
  `cap.cap60 > 0` support

* `com.gos.platform.api.devparam.AIDetectSettingParam`

```json
{
  "AiMode": 1,
  "Pet_detect": {
    "un_switch": 0,
    "un_sensitivity": 1
  },
  "Car_detect": {
    "un_switch": 0,
    "un_sensitivity": 1
  },
  "Face_detect": {
    "un_switch": 0,
    "un_sensitivity": 1
  },
  "Cross_detect": {
    "un_switch": 0,
    "un_sensitivity": 1
  }
}
```

| 字段名称         | 类型    | 描述                                                         |
| ---------------- | ------- | ------------------------------------------------------------ |
| CMDType          | String  | 对应能力集，固定cap60                                        |
| AiMode           | Integer | 0-不支持或全部禁止 <br />1-服务端识别模式<br />2-设备端识别模式<br />3-服务端+设备端双重模式 |
| Pet_detect       | object  | 宠物识别相关属性                                             |
| Car_detect       | object  | 车辆识别相关属性                                             |
| Cry_detect       | object  | 哭声检测相关属性                                             |
| Face_detect      | object  | 人脸识别相关属性                                             |
| Fireworks_detect | object  | 火光检测相关属性                                             |
| Plate_detect     | object  | 车牌识别相关属性                                             |
| Perm_detect      | object  | 周界检测相关属性                                             |
| un_switch        | Integer | 开关 0-关闭 1-开启                                           |
| un_sensitivity   | Integer | 灵敏度 取值范围如下<br />宠物识别：0~5<br />车辆识别：0-4<br />人脸识别：0-3<br />越线检测：默认1 |

## cap61

* 获取TF文件列表方法0:Ulife长连接1:P2P方法2:P2P方法分页刷新
  <br>`(cap.cap61 == 1 || cap.cap61 == 2 || cap.cap61 == 3) ? 1 : 0`  -- 0-Ulife, 1-P2P
  <br>`(cap.cap61 == 2 || cap.cap61 == 3)` support P2pPaging

## cap62

* 设备云存储分辨率
  <br>0 -- 不支持
  <br> 1--1 million
  <br> 2--2 million (1920 * 1080)
  <br> 3--3 million (2304 * 1296)
  <br> 4--4 million

<br>`cap.cap62 = 0`

## cap63

* 卡片录制是否支持全天录制事件录制切换？0。不支持。1.
  支持的
* `cap.cap63 > 0` support
    * `no used `



## cap81设置睡眠检测功能

```
{
    "Body":{
        "ParamArray":[
            {
                "CMDType":"cap81",
                "DeviceParam":{
					"enable":0,
					"detect_masked_face":0,
					"detect_awaken":0,
                    "detect_motion":0,
                    "sleep_quality":0,
                    "heartrate_breathe":0
                }
            }
        ]
    },
    "MessageType":"DeviceParamReportNotify"
}
```

| 字段名称           | 类型    | 描述                                           |
| ------------------ | ------- | ---------------------------------------------- |
| CMDType            | String  | 对应能力集，固定cap81                          |
| enable             | Integer | 是否开启睡眠检测，0关，1开                     |
| detect_masked_face | Integer | 遮脸检测，0关，1高灵敏度，2中灵敏度，3低灵敏度 |
| detect_awaken      | Integer | 觉醒检测，0关，1高灵敏度，2中灵敏度，3低灵敏度 |
| detect_motion      | Integer | 活动检测，0关，1高灵敏度，2中灵敏度，3低灵敏度 |
| sleep_quality      | Integer | 睡眠质量检测，0关，1开                         |
| heartrate_breathe  | Integer | 心率和呼吸检测，0关，1开                       |



## cap82设置周界检测参数

```
{
	"Body": {
		"ParamArray": [{
			"CMDType": "cap82",
			"DeviceParam": {
				"un_switch": 0,
				"un_sensitivity": 1,
				"permcnt": 1,
				"perms": [{
					"pcnt": 4,
					"rect": [{
						"x": 0,
						"y": 0
					}, {
						"x": 10000,
						"y": 0
					}, {
						"x": 10000,
						"y": 10000
					}, {
						"x": 0,
						"y": 10000
					}]
				}]
			}
		}]
	},
	"MessageType": "DeviceParamReportNotify"
}
```

| 字段名称       | 类型    | 描述                        |
| -------------- | ------- | --------------------------- |
| CMDType        | String  | 对应能力集，固定cap82       |
| un_switch      | Integer | 是否开启周界检测，0关，1开  |
| un_sensitivity | Integer | 灵敏度，灵敏度低到高1，2，3 |
| permcnt        | Integer | 周界数量，最多4个           |
| perms          | Integer | 周界参数                    |
| pcnt           | Integer | 顶点个数，最多6个           |
| rect           | Integer | 顶点坐标                    |
| x              | Integer | 顶点横坐标，范围0-10000     |
| y              | Integer | 顶点纵坐标，范围0-10000     |



## cap84设置隐私遮挡

```
{
	"Body": {
		"ParamArray": [{
			"CMDType": "cap82",
			"DeviceParam": {
				"un_switch": 0,
				"x0": 1,
				"x1": 1,
				"y0": 1,
				"y1": 1
			}
		}]
	},
	"MessageType": "DeviceParamReportNotify"
}
```

| 字段名称  | 类型    | 描述                       |
| --------- | ------- | -------------------------- |
| CMDType   | String  | 对应能力集，固定cap82      |
| un_switch | Integer | 是否开启隐私遮挡，0关，1开 |
| x0        | Integer | 左上角x轴坐标              |
| x1        | Integer | 右下角x轴坐标              |
| y0        | Integer | 左上角y轴坐标              |
| y1        | Integer | 右下角y轴坐标              |

## cap87设置子设备

```
{
	"Body": {
		"ParamArray": [{
			"CMDType": "cap87",
			"DeviceParam": {
				"dev_list": [{
					"devicetype": 1,
					"SubDevId": "1",
					"un_switch": 0,
					"un_volume": 7,
					"un_duration": 15,
					"un_begintime": 0,
					"un_endtime": 0,
					"online":0,
					"battery_level":100,
					"un_low_temp": 35.34,
					"un_high_temp": 38.51
				}]
			}
		}]
	},
	"MessageType": "DeviceParamReportNotify"
}
```

| 字段名称      | 类型    | 描述                              |
| ------------- | ------- | --------------------------------- |
| CMDType       | String  | 对应能力集，固定cap87             |
| devicetype    | Integer | 设备类型，1:报警器，2:体温检测器  |
| SubDevId      | String  | 子设备ID                          |
| un_switch     | Integer | 是否开启报警提醒，0关，1开        |
| un_volume     | Integer | 提醒音量，取值：0-10              |
| un_duration   | Integer | 提醒时长，单位秒                  |
| un_begintime  | Integer | 工作开始时间，单位秒，取值0-86400 |
| un_endtime    | Integer | 工作结束时间，单位秒，取值0-86400 |
| online        | Integer | 是否在线，0：离线，1：在线        |
| battery_level | Integer | 电量                              |
| un_low_temp   | Integer | 体温检测器用，体温报警最低值      |
| un_hjgh_temp  | Integer | 体温检测器用，体温报警最高值      |

# cap87-1-1提醒器提醒方式

```
{
	"Body": {
		"ParamArray": [{
			"CMDType": "cap87-1-1",
			"DeviceParam": {
				"SubDevId": "1",
				"eventTypeId": 1,
				"reminder_method": [{
					"event_type": 1,
					"un_ringindex": 0,
					"un_colorindex": 0,
					"un_ledeffect": 1,
					"un_ring": 1,
					"un_light": 1,
					"un_shake": 1
				}, {
					"event_type": 2,
					"un_ringindex": 0,
					"un_colorindex": 0,
					"un_ledeffect": 1,
					"un_ring": 1,
					"un_light": 1,
					"un_shake": 1
				}],
			}
		}]
	},
	"MessageType": "DeviceParamReportNotify"
}
```

| 字段名称        | 类型    | 描述                                                         |
| --------------- | ------- | ------------------------------------------------------------ |
| CMDType         | String  | 对应能力集，固定cap87-1-1 最后的1位提醒器id cap87-1-SubDevId |
| SubDevId        | String  | 子设备ID                                                     |
| eventTypeId     | Integer | 当前设置的事件类型，取值0-6，[检测到哭声,碰到围栏,睡着了,醒了,在动,遮脸了,心率异常] |
| reminder_method | Array   | 事件提醒方式                                                 |
| event_type      | Integer | 事件类型，取值0-6，[检测到哭声,碰到围栏,睡着了,醒了,在动,遮脸了,心率异常] |
| un_ringindex    | Integer | 铃声下标，取值：0-4， [cry, move, sleep, wakeup, rock-a-bye] |
| un_colorindex   | Integer | 灯光颜色下标，取值：0-6，[red, orange, yellow, green, blue, purple] |
| un_ledeffect    | Integer | 灯光效果，1：常亮，2：闪烁                                   |
| un_ring         | Integer | 是否开启铃声报警，0关，1开                                   |
| un_light        | Integer | 是否开启灯光报警，0关，1开                                   |
| un_shake        | Integer | 是否开启震动报警，0关，1开                                   |

# 通道

* channel代表流的通道号，我们有多个流产品，所以通道如果已定义，则单通道流媒体产品的默认值为0
* 通道被保留，默认为0，所有通道参数设置为0

# 什么是蜂窝移动网络及其用途

* 作为设备的移动网卡

# SDK功能
## 连接
```java
Connection mConnection = mDevice.getConnection()
 mConnection.connect(0);
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| mConnection                  | object  | p2p长连接                                  |
| channel                 | Integer | 0    通道预留用于扩展 |
* ConnectResult

```java
private int conStatus;
private ConnType connType;
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| conStatus                  | object  | 0-成功1-错误11-连接丢失                         |
| connType                 | enum |  0 - 未知  1-TYPE_TUTK 2-TYPE_P2P 3-TYPE_TCP 4-TYPE_P2P_TCP  |

## 开始视频
```java
int timestamp = (int) (System.currentTimeMillis() / 1000L);
int timezone = (TimeZone.getDefault().getRawOffset() / 3600000) + 24;// on the IPC side, -24,
if(TimeZone.getDefault().inDaylightTime(new Date())){
 timezone++;
 }
mConnection.startVideo(0, StreamType.VIDEO_AUDIO, mDevice.getStreamPsw(), timestamp, timezone, this);

```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| channel                  | integer  | 0                                       |
| StreamType                 | int | 0-视频1-音频 2-音视频 3-STREAM_REC（TF流播放）4-STREAM_REC_JPEG（未使用）<br/>我们使用了2或3 |
| psw                 | String | 流的密码 |
| lTimeSeconds                 | int | 当前时间 |
| lTimeZone                 | int  | 时区 |
| IVideoPlay                 | object  | AvFrame回调接口 |

## 停止视频
```java
 mConnection.stopVideo(0, this);
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| channel                  | integer  | 0                                       |
| IVideoPlay                  | object  | AvFrame回调接口                            |
## 开始对讲
```java
//The device uses full duplex intercom mode
    int talkType=STREAM_TYPE;

public void startTalk(View view){
        if(mConnection.isConnected()){
        mConnection.startTalk(0,mDevice==null?"":mDevice.getStreamPsw());
        mBtnStopTalk.setEnabled(true);
        }
}
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| channel                  | integer  | 0                                       |
| psw                  | String  | 流的密码                                  |
## 停止对讲
```java
mConnection.stopTalk(0);
```
## 静音/取消静音

```java
//Add a tag to control whether AudioTrack writes data
private boolean startAudio = true;  

//unmute
startAudio = true;
mConnection.startAudio(0);
//mute
startAudio = false;
mConnection.stopAudio(0);

@Deprecated
mConnection.stopAudio(0,psw)

//in AudioHandler class
if(!startAudio)
 return;


```
## 开始录像
```java
 mMediaPlayer.startRecord(recordPath,0);
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| mMediaPlayer                  | GosMediaPlayer  | 音视频解码器                                 |
| recordPath                  | String  | 文件保存路径                                 |
| flag                  | integer  | 0     每个mMediaPlayer都有一个标志，只是为了区分GosMediaPlayer |
## 停止录像
```java
mMediaPlayer.stopRecord();
```

# 保活

* 当进入实时视频页面时，如果设备状态发生变化，门铃设备需要保持唤醒状态，设备将报告最新的状态 `NotifyDeviceStatus`
  
```java
//检查设备是否处于省电模式。
DevParamResult devParamResult = GosSession.getSession().AppGetDeviceParam(mDeviceId, LowpowerModeSetting);
//如果设备处于低功耗模式，我们应该尝试唤醒它。
PlatModule.getModule().wakeUpDeviceSyn(mDeviceId);
SystemClock.sleep(1000);
//查询设备在线状态。
QueryDeviceOnlineStatusResult statusResult = PlatModule.getModule().queryDeviceOnlineStatusSyn(mDeviceId);
//如果我们成功唤醒了设备。
connection.connect(0);
```

# TF 回放。

* 你需要发起一个请求，`MsgType` 参考 `com.gos.platform.api.request.Request.MsgType`，这些 `MsgType` 是与服务器约定的信令。

```json

{
  "Body": {
    "DeviceId": "A99UD210EL1ED5E",
    "DeviceParam": {
      "CMDType": 972,
      "page_data": "202305303|202305313|202306013|202306023|202306033"
    },
    "SessionId": "4574153488637"
  },
  "MessageType": "BypassParamResponse",
  "ResultCode": 0
}
```

* `page_data` 中的 `202305303`，其中 `20230530` 是日期（年月日），`3` 是当天的总文件数。这个类对应的是 `com.gos.platform.device.domain.FileForMonth.ForMonth`。
  <br>  `202305303` 中，第 0 到第 7 位是日期，剩下的数字是视频文件的数量。
  <br>总文件数用于分页加载。
  <br> `20230530` 表示当天有录制数据。

```java
//20230530 day time
String dayTime=getIntent().getStringExtra("DAY_TIME");
// 0-All records 1-Alarm records
        int type=getIntent().getIntExtra("TYPE",0);
//get files by this time
        long startTime=dateFormat.parse(dayTime).getTime()/1000;
//getRecDayEventRefresh call native function NativeGetRecDayEventRefresh
        mDevice.getConnection().getRecDayEventRefresh(0,(int)startTime,0,0);  

```

<br> * response `GetRecDayEventRefreshResult`

```json
{
  "result": 0,
  "total_num": 1,
  "day_event_list": [
    {
      "start_time": 1597116179,
      "end_time": 1597116193,
      "type": 1
    },
    {
      "start_time": 1597116179,
      "end_time": 1597116193,
      "type": 0
    }
  ]
}
```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| CMDType                  | String  | getRecDayEventRefresh                                        |
| result                 | Integer |                                                    |
| total_num  | Integer | 总数量。                |
| day_event_list   | Array |                     |
| start_time  | Integer | 单个文件的开始时间          |
| end_time         | Integer | 单个文件的结束时间        |
| type | Integer | 0-正常记录，1-报警记录。         |

# VPhoto HTTP API

HTTP Request Template

```kotlin
val baseUrl="https://vphoto.waophoto.com/apiv3/"
```

## 按Uuid注册

```java
● URL：/apiv3/user/userRegisterByUuid
● Method：POST
```

| Field name        | Type   | Describe                                           |
| ----------------- | ------ | -------------------------------------------------- |
| user_secret       | String | user_secret=base64Encode(rsa(user_uuid))           |
| company_name      | String | VPhoto                                             |
| nickname          | String | 用户名                                             |
| profile_image     | String | User profile picture address (can be passed blank) |
| timestamp         | String | let timestamp = Date.currentTimeStamp              |
| sign<header>      | String | 参数按升序排列并与user_secret连接后的md5值         |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp              |

## 由Uuid登录

```
● URL：/apiv3/user/signinByUuid
● Method：POST
```

| Field name     | Type   | Describe                                   |
| -------------- | ------ | ------------------------------------------ |
| company_name   | String | VPhoto                                     |
| user_fcm_token | String | Can pass ""                                |
| user_platform  | String | 1                                          |
| user_secret    | String | 将uuid通过rsa加密转换为base64              |
| sign<header>   | String | 参数按升序排列并与user_secret连接后的md5值 |
| timestamp      | String | let timestamp = Date.currentTimeStamp      |

## 按设备绑定

```
● URL：/apiv3/user/bindByDeviceConnectionCode
● Method：POST
```

| Field name             | Type   | Describe                                   |
| ---------------------- | ------ | ------------------------------------------ |
| device_connection_code | String | 链接码                                     |
| company_name           | String | VPhoto                                     |
| deviceUserName         | String | Can pass ""                                |
| sign<header>           | String | 参数按升序排列并与user_secret连接后的md5值 |
| timestamp<header>      | String | let timestamp = Date.currentTimeStamp      |

## 绑定状态

```java
● URL：/apiv3/user/bindstatus
● Method：POST
```

| Field name             | Type   | Describe                                   |
| ---------------------- | ------ | ------------------------------------------ |
| device_connection_code | String | Link code                                  |
| user_id<header>        | String | user id                                    |
| sign<header>           | String | 参数按升序排列并与user_secret连接后的md5值 |
| timestamp<header>      | String | let timestamp = Date.currentTimeStamp      |

## 更新帐户密码

```
● URL：/apiv3/user/devicecam
● Method：POST
```

| Field name        | Type   | Describe                                   |
| ----------------- | ------ | ------------------------------------------ |
| device_password   | String | 密码，如果是新密码，则为必填项，更新可选   |
| device_user_name  | String | 帐户，如果是新的，您必须填写，更新可选     |
| user_id<header>   | String | user id                                    |
| sign<header>      | String | 参数按升序排列并与user_secret连接后的md5值 |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp      |

## 解绑设备

```
● URL： /apiv3/user/status
● Method：POST
```

| Field name        | Type   | Describe                                   |
| ----------------- | ------ | ------------------------------------------ |
| user_id           | String | user id                                    |
| device_id         | String | device id                                  |
| status            | String | 状态                                       |
| sign<header>      | String | 参数按升序排列并与user_secret连接后的md5值 |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp      |

## 绑定设备

```
● URL：/apiv3/user/user_device
● Method：POST
```

| Field name        | Type   | Describe                                        |
| ----------------- | ------ | ----------------------------------------------- |
| user_id           | String | user id                                         |
| user_token        | String | signinByUuid指定接口返回的user_system_token字段 |
| sign<header>      | String | 参数按升序排列并与user_secret连接后的md5值      |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp           |

## 预签名Url压缩

```
● URL：/apiv3/upload/presignedUrlZipS3
● Method：POST
```

| Field name   | Type   | Describe                                                     |
| ------------ | ------ | ------------------------------------------------------------ |
| user_id      | String | user id                                                      |
| user_imei    | String | user uuid                                                    |
| image_data   | String | 括号中是device_id数组，其中device_id是要发送的图片帧的id：{“device_id”：[1,2,3]} |
| desc         | String | Can pass ""                                                  |
| data         | String | Can pass ""                                                  |
| file_size    | String | Can pass ""                                                  |
| sign<header> | String | 参数按升序排列并与user_secret连接后的md5值                   |
|              |        |                                                              |

## 预签名Url视频

```
● URL：/apiv3/upload/presignedUrlVideoS3
● Method：POST
```

| Field name        | Type   | Describe                                                     |
| ----------------- | ------ | ------------------------------------------------------------ |
| user_id           | String | user id                                                      |
| user_imei         | String | user uuid                                                    |
| video_data        | String | 括号中是device_id数组，其中device_id是要发送的图片帧的id：{“device_id”：[1,2,3]} |
| suffix            | String | 视频文件后缀，只支持mp4和mov两种                             |
| desc              | String | Can pass ""                                                  |
| data              | String | Can pass ""                                                  |
| file_size         | String | Can pass ""                                                  |
| sign<header>      | String | 参数按升序排列并与user_secret连接后的md5值                   |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp                        |

## 上传文件

```
● URL：presignedUrlVideoS3或presignedUrlZipS3返回preSignedUrl字段
● Method：put
```

| Field name           | Type   | Describe                                        |
| -------------------- | ------ | ----------------------------------------------- |
| file                 | File   | file path                                       |
| Content-Type<header> | String | application/zip or video/mp4 or video/quicktime |

# HTTP API

* HTTP Request Template

```kotlin
val baseUrl="https://usapp-open.ulifecam.com"

@POST(".")
suspend fun register(@Body body: RequestBody):Response<BaseResponse<CommonResult?>>

data class BaseResponse<T>(
  val Body: T,
  val MessageType: String,
  val ResultCode: Int
)



```

```json
{
  "Body": {
    "UserName": "tianya202111@163.com"
  },
  "MessageType": "GetUserDeviceListRequest"
}
```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | GetUserDeviceListRequest --- messageType is the signaling for each request                                         |
| Body                 | Body |    requestBody                                                |
| UserName                 | String  |    login email         |

## 登录

```kotlin

val map = mapOf(
    Pair("UserName", username),
    Pair("Password", gsoSession.encodeData(psw)),
    Pair("MobileCN", ""),
    Pair("UserType", GApplication.app.userType),
    Pair("ProtocolType", 1),
)
val nMap = mapOf(
    Pair("Body", map),
    Pair("MessageType", LoginCGSARequest)
)
val json = Gson().toJson(nMap)

```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | LoginCGSARequest --- messageType is the signaling for each request                                         |
| Body                 | Body |    requestBody                                                |
| UserName                 | String  |    登录账号   |
| Password                 | String  |    密码 -- encodeData()          |
| MobileCN                 | String  |    ""-- 地区,中国 +86 海外 +1        |
| UserType                 | Integer  |   32           |
| ProtocolType                 | Integer  |   1 -- http ,0 --tcp           |

## 获取验证码

```kotlin
val map = mapOf(
    Pair("FindPasswordType", findType),
    Pair("UserInfo", userInfo),
    Pair("VerifyWay", verifyWay),
    Pair("UserType", userType),
    Pair("MobileCN", ""),
    Pair("Language", null),
)
val nMap = mapOf(
    Pair("Body", map),
    Pair("MessageType", GetVerifyCodeRequest)
)
val json = Gson().toJson(nMap).toRequestBody()

```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | GetVerifyCodeRequest --- messageType is the signaling for each request                                         |
| Body                 | Body |    requestBody                                                |
| FindPasswordType                 | Integer  |    1-USER_NAME,2-EMAIL_ADDRESS,3-PHONE         |
| UserInfo                 | String  |    账号      |
| VerifyWay                 | Integer  |     注册 = 1,忘记密码 = 2      |
| MobileCN                 | String  | 地区,中国 +86 海外 +1 |
| UserType                 | Integer  |   32           |
| Language                 | String   |   no used            |

## 注册

```kotlin
val map = mapOf(
    Pair("UserType", userType),
    Pair("RegisterWay", registWay),
    Pair("UserName", userName),
    Pair("Password", gsoSession.encodeData(psw)),
    Pair("PhoneNumber", phoneNum),
    Pair("EmailAddr", emailAddr),
    Pair("AreaId", ""),
    Pair("VerifyCode", verifyCode),
    Pair("MobileCN", mobileCN),

    )
val nMap = mapOf(
    Pair("Body", map),
    Pair("MessageType", UserRegisterRequest)
)
val json = Gson().toJson(nMap)
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | UserRegisterRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| RegisterWay                 | Integer  |   2-邮箱,3-手机号      |
| UserName                 | String  |    账号      |
| Password                 | String  |    密码     |
| VerifyWay                 | Integer  | 注册 = 1,忘记密码 = 2 |
| MobileCN                 | String  | 地区,中国 +86 海外 +1 |
| UserType                 | Integer  |   32           |
| PhoneNumber                 | String   | "" 电话号码和电子邮件地址的替代方案 |
| EmailAddr                 | String   | "" 电话号码和电子邮件地址的替代方案 |
| AreaId                 | String   |   no used          |
| VerifyCode                 | String   |   验证码       |

## 更改用户密码

```kotlin
val map = mapOf(
  Pair("UserName", userName),
  Pair("VerifyCode", code),
  Pair("NewPassword", gsoSession.encodeData(newPsw)),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", ModifyUserPasswordRequest)
)
val json = Gson().toJson(nMap).toRequestBody()

```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | ModifyUserPasswordRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| VerifyCode                 | Integer  |    验证码  |
| UserName                 | String  |    用户账号    |
| NewPassword                 | String  |    用户新密码    |

## 获取设备列表

```kotlin
val map = mapOf(
  Pair("UserName", userName),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", GetUserDeviceListRequest)
)
val json = Gson().toJson(nMap)

```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | GetUserDeviceListRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| UserName                 | String  |    user name        |

## 获取绑定设备token

```kotlin
val map = mapOf(
  Pair("UserName", userName),
  Pair("DeviceId", null),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", GetBindTokenRequest)
)
val json = Gson().toJson(nMap)
```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | GetBindTokenRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| UserName                 | String  |    user name        |
| DeviceId                 | String  |    null        |

## 查询设备绑定状态

```kotlin
val map = mapOf(
  Pair("UserName", userName),
  Pair("BindToken", bindToken),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", QueryUserBindResultRequest)
)
val json = Gson().toJson(nMap).toRequestBody()

```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | QueryUserBindResultRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| UserName                 | String  |    user name        |
| BindToken                 | String  | 绑定设备token  |

## 修改设备名称

```kotlin

val map = mapOf(
  Pair("DeviceId", deviceId),
  Pair("DeviceName", deviceName),
  Pair("StreamUser", streamUser),
  Pair("StreamPassword", streamPsw),
  Pair("LinkDevice", linkDevice),
  Pair("UserName", userName),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", ModifyDeviceAttrRequest)
)
val json = Gson().toJson(nMap).toRequestBody()
```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | ModifyDeviceAttrRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| DeviceName                 | String  |    deivce name        |
| DeviceId                 | String  |    deviceID       |
| StreamUser                 | String  |    Can pass ""       |
| StreamPassword                 | String  |    Can pass ""        |
| LinkDevice | String | VPhoto association id |
| UserName | String | userName |

## 分享设备

```kotlin
val map = mapOf(
  Pair("UserName", userName),
  Pair("DeviceId", deviceId),
  Pair("DeviceOwner", isOwner),
  Pair("DeviceName", deviceName),
  Pair("DeviceType", deviceType),
  Pair("StreamUser", streamUser),
  Pair("StreamPassword", streamPsw),
  Pair("AreaId", areaId),
  Pair("AppMatchType", appMatchType),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", BindSmartDeviceRequest)
)
val json = Gson().toJson(nMap).toRequestBody()
```

| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | BindSmartDeviceRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| UserName                 | String  |   共享到用户名  |
| DeviceId                 | String  |    deviceID       |
| DeviceOwner                 | Integer  |  0-分享1-拥有 |
| DeviceName                 | String  |    device name       |
| DeviceType                 | Integer  |    deviceID       |
| StreamUser                 | String  |    Can pass ""       |
| StreamPassword                 | String  |    Can pass ""        |
| AreaId                 | String  |    Can pass ""        |
| AppMatchType                 | Integer  | 1-当前平台，0-旧平台 |

## 绑定设备

```kotlin
val map = mapOf(
  Pair("UserName", userName),
  Pair("DeviceId", deviceId),
  Pair("DeviceOwner", isOwner),
  Pair("DeviceName", deviceName),
  Pair("DeviceType", deviceType),
  Pair("StreamUser", streamUser),
  Pair("StreamPassword", streamPsw),
  Pair("AreaId", areaId),
  Pair("AppMatchType", appMatchType),
  Pair("LinkDevice", linkDevice),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", BindSmartDeviceRequest)
)
val json = Gson().toJson(nMap).toRequestBody()
```

| Field name     | Type    | Describe                                                     |
| -------------- | ------- | ------------------------------------------------------------ |
| MessageType    | String  | BindSmartDeviceRequest --- messageType is the signaling for each request |
| Body           | Body    | requestBody                                                  |
| UserName       | String  | 用户名                                                       |
| DeviceId       | String  | deviceID                                                     |
| DeviceOwner    | Integer | 0-分享1-拥有                                                 |
| DeviceName     | String  | device name                                                  |
| DeviceType     | Integer | deviceID                                                     |
| StreamUser     | String  | Can pass ""                                                  |
| StreamPassword | String  | Can pass ""                                                  |
| AreaId         | String  | Can pass ""                                                  |
| AppMatchType   | Integer | 1 -current platform , 0-old platform                         |
| LinkDevice     | String  | VPhoto association id                                        |

## 强制删除设备

```kotlin
val map = mapOf(
  Pair("DeviceId", deviceId),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", ForceUnbindDeviceRequest)
)
val json = Gson().toJson(nMap).toRequestBody()
```

| Field name  | Type   | Describe                                                     |
| ----------- | ------ | ------------------------------------------------------------ |
| MessageType | String | GetShareUserListRequest --- messageType is the signaling for each request |
| Body        | Body   | requestBody                                                  |
| DeviceId    | String | deviceID                                                     |

## 获取共享用户列表

```kotlin

val map = mapOf(
  Pair("DeviceId", deviceId),
)
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", GetShareUserListRequest)
)
val json = Gson().toJson(nMap).toRequestBody()
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | GetShareUserListRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| DeviceId                 | String  |    deviceID       |

## 解除分享

```kotlin

val map = mapOf(
  Pair("UserName", userName),
  Pair("DeviceId", deviceId),
  Pair("DeviceOwner", 0),

  )
val nMap = mapOf(
  Pair("Body", map),
  Pair("MessageType", UnbindSmartDeviceRequest)
)
val json = Gson().toJson(nMap).toRequestBody()
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | UnbindSmartDeviceRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| UserName                 | String  |   分享的用户名   |
| DeviceId                 | String  |    deviceID       |
| DeviceOwner                 | Integer  |   共享设备为0 |

## 获取设备参数
```kotlin

//General Request Body
data class DeviceParam(
    val CMDTypeList: List<String>,
    val DeviceId: String,
    val SessionId: String,
    val UserName: String,
    val UserType: Int
)

// Complete Request Body
cmdArray.clear()
cmdArray.addAll(cmd)
val param =
  GetDeviceParam(
    cmdArray,
    deviceId,
    GApplication.app.user.sessionId!!,
    GApplication.app.user.userName!!,
    GApplication.app.userType

  )
val map = mapOf(
  Pair("Body", param),
  Pair("MessageType", AppGetDeviceParamRequest),
)

val json = Gson().toJson(map).toRequestBody()

```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | AppGetDeviceParamRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                |
| CMDTypeList                 | List<String>  | "cap1","cap2",······请参阅“设备功能集” |
| UserName                 | String  |    用户名     |
| DeviceId                 | String  |    deviceID       |
| sessionId                 | String  |    session id       |
| userType                 | Integer  |   32           |

###  response

```kotlin
//Complete response
data class BaseResponse(
    val Body: Body,
    val MessageType: String,
    val ResultCode: Int
)
// response Body
data class Body(
  val AccessToken: String,
  val CGSId: String,
  val DeviceId: String,
  val ParamArray: List<ParamArray>,
  val SessionId: String,
  val UserName: String
)
//parameter array
data class ParamArray(
  val CMDType: String,
  val DeviceParam: DeviceParam
)
//Specific parameters
data class DeviceParam(
  
)
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | AppGetDeviceParamResponse --- messageType is the signaling for each request         |
| Body                 | Body |    responseBody                                                |
| AccessToken                 | String  |    no used     |
| CGSId                 | String  |    no used     |
| UserName                 | String  |    login user name        |
| DeviceId                 | String  |    deviceID       |
| sessionId                 | String  |    session id       |
| ParamArray                 | Array   |   An array containing all request replies          |
| CMDType                 | String   |   "cap1"-"capn"          |
| DeviceParam                 | object   |  please refer to `package com.gos.platform.api.devparam` |

## 设置设备参数

* set device param  general Request 
```kotlin
//request 
data class DevSetParam(
  val Body: DevSetBody,
  val MessageType: String
)
//request body
data class DevSetBody(
  val DeviceId: String,
  val ParamArray: List<DevParamArray>,
  val SessionId: String,
  val UserName: String,
  val UserType: Int
)
//ParamArray  Contains multiple request parameters,Distinguish using CMDType
data class DevParamArray(
  val CMDType: String,
  val DeviceParam: PirParam
)
//exp pir 
data class PirParam(
  val un_cdtime: Int,
  val un_sensitivy: Int,
  val un_stay: Int,
  val un_switch: Int
)
//One CMDType corresponds to one DeviceParam

```

<br>// test for pir

```kotlin

val DevParamArray = DevParamArray(
  DevParamCmdType.PirSetting,
  pirParam
)
val DevSetBody = DevSetBody(
  deviceId,
  arrayListOf(DevParamArray),
  GApplication.app.user.sessionId!!,
  GApplication.app.user.userName!!,
  GApplication.app.userType
)
val DevSetParam = DevSetParam(
  DevSetBody,
  AppSetDeviceParamRequest
)

val json = Gson().toJson(DevSetParam).toRequestBody()
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| MessageType                  | String  | AppSetDeviceParamRequest --- messageType is the signaling for each request         |
| Body                 | Body |    requestBody                                                 |
| UserName                 | String  |    login user name        |
| DeviceId                 | String  |    deviceID       |
| sessionId                 | String  |    session id       |
| ParamArray                 | Array   |   An array containing all request replies          |
| CMDType                 | String   |   "cap1"、"cap2"、"cap4"、·····          |
| DeviceParam                 | object   |  please refer to `package com.gos.platform.api.devparam` |



<br> * 一个CMDType对应一个DeviceParam
<br> * `CMDType“请参考”包com.gos.platform.api.devparam。DevParamCmdType` 
<br> * `DeviceParam请参考`package com.gos.platform.api.devparam`，如`PirSettingParam`、`NightSettingParam'、`MirrorModeSettingParam`



## 保活

```kotlin
data class KeepLiveParam(
    val CMDType: Int,
    val channel: Int
) : BaseDeviceParam()

//Keep the doorbell beating,Send a request every 5 seconds
val keepLiveParam = KeepLiveParam(IOTYPE_USER_IPCAM_KEEPALIVE_REQ, 0);
val result = RemoteDataSource.getCmdParam(keepLiveParam, deviceId)
SystemClock.sleep(5000)

```



## 音频控制

```kotlin
//Add an audio playback flag bit
var playAudio = true;
inner class AudioHandler : Handler {
    constructor(looper: Looper) : super(looper)
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        //Control whether sAudioTrack writes audio data
        if (!playAudio)
            return
        val data = msg.obj as ByteArray
        sAudioTrack?.apply {
            if (playState != AudioTrack.PLAYSTATE_PLAYING) {
                play()
            }
            write(data, 0, data.size)
        }
    }
}
```

## 实时时间戳显示

* 呼叫时区校准请求，校准时间

  ```kotlin
      val device = DeviceManager.getInstance().findDeviceById(deviceId)
          viewModelScope.launch {
              val timeParam = TimeParam(
                  (System.currentTimeMillis() / 1000).toInt(),
                  device.getVerifyTimezone()    //sorry ,it is device.getVerifyTimezone() not device.verifyTimezone
              )
              val devParamArray = DevParamArray(
                  TimeZoneInfo,
                  timeParam
              )
              val result = RemoteDataSource.setDeviceParam(devParamArray,deviceId = deviceId)
          }
  ```

  ```java
  //Get Time Zone  in Device.class    
  public int getVerifyTimezone(){
          DevCap devCap = getDevCap();
          if(timezoneVerifyType == 0 && devCap != null){
              timezoneVerifyType = devCap.timeZoneType;
          }
          ....
      }
  ```

  ## OTA
  
  获取设备列表时，可以获取设备的系统版本
  
  ```java
    public Device(String devName, String devId, boolean isOnline, int devType, String streamUser, String streamPassword, GetDeviceListResponse.Cap cap,
                    String deviceHdType, String deviceSfwVer, String deviceHdwVer) {
          this.devName = devName;
          this.devId = devId;
          this.isOnline = isOnline;
          this.devType = devType;
          this.streamUser = streamUser;
          this.streamPassword = streamPassword;
          this.deviceHdType = deviceHdType;  //Device Type
          this.deviceSfwVer = deviceSfwVer;  //software
          this.deviceHdwVer = deviceHdwVer;  //hardware
          parseDap(cap);
      }
  ```
  
  **是否有新版本的请求，从服务器获取固件版本并将其与设备的版本号进行比较**
  
  ```kotlin
          val job = asyncTask {
              val map = mapOf(
                  Pair("DeviceType", deviceType),
              )
              val nMap = mapOf(
                  Pair("Body", map),
                  Pair("MessageType", CheckNewerVerRequest)
              )
              val json = Gson().toJson(nMap).toRequestBody()
              val response = RetrofitClient.apiService.checkNewVer(json)
  
              return@asyncTask response.body()
          }
  		//response is
  	data class FirmWareParam(
      val CGSId: String,
      val Desc: String,
      val DeviceType: String,
      val FileSize: Int,
      val MD5: String,
      val Url: String,
      val Version: String   //version
  )
  
  
  ```
  
  ```java
  // Comparing deviceSfwVer and version   
  public static boolean compareDeviceSoftwareVersion(String version, String newVersion){
          return !TextUtils.isEmpty(newVersion)
                  && !TextUtils.isEmpty(version)
                  && version.compareToIgnoreCase(newVersion) < 0;
      }
  ```
  
  **If updates are needed**

## OTA

##### 获取设备列表时，可以获取设备的系统版本

~~~jav
 public Device(String devName, String devId, boolean isOnline, int devType, String streamUser, String streamPassword, GetDeviceListResponse.Cap cap,
                  String deviceHdType, String deviceSfwVer, String deviceHdwVer) {
        this.devName = devName;
        this.devId = devId;
        this.isOnline = isOnline;
        this.devType = devType;
        this.streamUser = streamUser;
        this.streamPassword = streamPassword;
        this.deviceHdType = deviceHdType;  //Device Type
        this.deviceSfwVer = deviceSfwVer;  //software
        this.deviceHdwVer = deviceHdwVer;  //hardware
        parseDap(cap);
    }
~~~

**是否有新版本的请求，从服务器获取固件版本并将其与设备的版本号进行比较**

~~~kotlin
        val job = asyncTask {
            val map = mapOf(
                Pair("DeviceType", deviceType),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", CheckNewerVerRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.checkNewVer(json)

            return@asyncTask response.body()
        }
		//response is
	data class FirmWareParam(
    val CGSId: String,
    val Desc: String,
    val DeviceType: String,
    val FileSize: Int,
    val MD5: String,
    val Url: String,
    val Version: String   //version
)
~~~

~~~jav
// Comparing deviceSfwVer and version   
public static boolean compareDeviceSoftwareVersion(String version, String newVersion){
        return !TextUtils.isEmpty(newVersion)
                && !TextUtils.isEmpty(version)
                && version.compareToIgnoreCase(newVersion) < 0;
    }
~~~

**如果需要更新，如果设备处于睡眠状态，则需要先唤醒它**

~~~java
RemoteDataSource.queryDeviceOnlineStatusSyn(deviceId)  //wake up device 
   
~~~

~~~kotlin
  val upGradeStatusParam = UpgradeStatusParam(
                0, 0, -1
            )  //Clear progress first  

            val devParamArray = DevParamArray(
                UpgradeStatus,
                upGradeStatusParam
            )
            mViewModel?.setDeviceParam(devParamArray, deviceId)


            val updateRestParam = GetUpgradeInfoRequestParam(
                IOTYPE_USER_IPCAM_GET_UPGRADE_INFO_REQ,
                0,
                0,
                firmWareParam.Url,
                firmWareParam.Version,
                firmWareParam.MD5,
                firmWareParam.FileSize
            ) //Set to start firmware upgrade
            mViewModel?.setDeviceUpdate(updateRestParam, deviceId = deviceId)
~~~



~~~java
//Get download progress once a second
    fun getUpdateProgressParam(deviceId: String) {
        viewModelScope.launch {
            val result = RemoteDataSource.getDeviceParam(UpgradeStatus, deviceId = deviceId)
            result!!.let {
                for (param in it) {
                    if (param.CMDType == UpgradeStatus) {
                        val progress = Gson().fromJson(param.DeviceParam, UpgradeStatusParam::class.java)
                        Log.e(TAG, "getUpdateProgressParam: $progress", )
                    }
                }
            }
        }
    }

//response is
data class UpgradeStatusParam(
    val upgrade_item_type: Int,
    val upgrade_progress: Int,
    val upgrade_status: Int,
) : BaseDeviceParam()

//check the upgrade_status
    public final static int E_UPGRADE_DOWNLOADING = 0;     //(0)Downloading
    public final static int E_UPGRADE_FAILURE = 1;         //(1)Upgrade failed
    public final static int E_UPGRADE_SUCCESS = 2;         //(2)Upgrade Successful
    public final static int E_UPGRADE_DOWNLOAD_START = 3;  //(3)Start downloading
    public final static int E_UPGRADE_DOWNLOAD_FINISH = 4; //(4)Download completed
    public final static int E_UPGRADE_CHECK_ERROR = 5;     //(5)Upgrade package validation error
    public final static int E_UPGRADE_INSTALL_START = 6;   //(6)Start Installation
~~~

## 红外夜视帧测

门铃的夜视模式只能打开、关闭和自动。如果是自动的，它将在触发夜视后自动打开夜视模式。
红外检测检测热量并报警

省电与手机的蜂窝网络无关



### 设备设置

*获取设备列表时，解析“GetDeviceListResponse.Cap”'

~~~java
    public Device(String devName, String devId, boolean isOnline, int devType, String streamUser, String streamPassword, GetDeviceListResponse.Cap cap,
                  String deviceHdType, String deviceSfwVer, String deviceHdwVer) {
        this.devName = devName;
        this.devId = devId;
        this.isOnline = isOnline;
        this.devType = devType;
        this.streamUser = streamUser;
        this.streamPassword = streamPassword;
        this.deviceHdType = deviceHdType;
        this.deviceSfwVer = deviceSfwVer;
        this.deviceHdwVer = deviceHdwVer;
        parseDap(cap); //get the device support setting
    }
//refer to the 'parseDap' function and sdk.md "Capability Set Request Structure"
//On the settings page, corresponding settings can be displayed based on whether the device supports settings
~~~

**例子**

设备是否支持夜视模式

~~~JSON
devCap.nightVisonType = cap.cap13;

//in sdk.md "cap13"
cap.cap13 > 0  //means support 
//get night version mode param    
{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap13"
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppGetDeviceParamRequest"
}    

//set night version mode

{
  "Body": {
    "DeviceId": "Z99862100000011",
    "ParamArray": [
      {
        "CMDType": "cap13",
          "DeviceParam": {
          "un_auto": 0,
          "un_day_night": 1,
        }
      }
    ],
    "AccessToken": "B07367BD4387464F956DF076C906C58C",
    "UserName": "542957111@qq.com",
    "UserType": 1
  },
  "MessageType": "AppSetDeviceParamRequest"
}
~~~

从设备的Cap中获取支持的设置类型和相应的显示设置，获取相应设置的参数，并在相应设置下设置参数



## 验证时区

~~~kotlin
TimeZoneInfo = "cap12"
//AppTimeSec -- System.currentTimeMillis() / 1000
//un_TimeZone -- getVerifyTimeZone() function int Device.class 
data class TimeParam(
    val AppTimeSec: Int,
    val un_TimeZone: Int
) : BaseDeviceParam()

// refer to the function "setTimeVerify" in TimeVerifyViewModel.kt

~~~

~~~json
//update the timeZone
{
    "Body":{
        "AccessToken":"",
        "DeviceId":"ZHAWE210DNOVVGP",
        "ParamArray":[
            {
                "CMDType":"cap12",
                "DeviceParam":{
                    "AppTimeSec":1688630927,  //System.currentTimeMillis() / 1000
                    "un_TimeZone":28800      //hi ,I found out the  value of 'un_TimeZone' is incorrect 
                }
            }
        ],
        "SessionId":"180389431578",
        "UserName":"1265636714@qq.com",
        "UserType":35
    },
    "MessageType":"AppSetDeviceParamRequest"
}
// un_TimeZone means timezone offset ,not timezone ,such as china : 8*60*60
// India timezone is 5.5-->un_TimeZone:5.5*60*60=19800 

~~~

~~~k
设备支持的时区类型：`timezoneVerifyType`
0：需要从设备能力集中获取
1：整点时区（例如：8，9，10）
2：半小时时区（例如：3.5、4.5、5.5）
3：精确分钟时区（秒值）。
~~~

## 保活

**`keepAliveSyn` 返回的是 `-20004` 还是 `-2004`？如果返回码是 `-20004`，表示设备已离线。**

## 删除账户

1. ###### 获取验证码。

   ~~~jso
   {
     "Body": {
   	"FindPasswordType":2,  //1-USER_NAME,2-EMAIL_ADDRESS,3-PHONE
       "UserInfo": "userName",  //username
       "VerifyWay": 3,  //1- REGIST,2-FORGET_PSW,3-delete Account
       "UserType": 1, //your type goscam pro is 32
       "MobileCN":""
     },
     "MessageType": "GetVerifyCodeRequest"
   }
   
   ~~~

   ###### *2. 删除账户。*

   ~~~json
   {
       "Body":{
           "AccessToken":"03CBBCA487954B2C9CF8DA7B002B99F5",
           "DeviceIdList":[
   
           ],  //the device id array just owne you
           "UserName":"653429798@qq.com",
           "UserType":32,
           "VerifyCode":"225255"
       },
       "MessageType":"DeleteAccountByVerifyRequest"
   }
   ~~~

   ## 删除设备

   ~~~json
   {
       "Body":{
           "DeviceId":"A99VD210Z1FTABS",
           "DeviceOwner":0,   //0-share 1-owne
           "AccessToken":"03CBBCA487954B2C9CF8DA7B002B99F5",
           "SessionId":"0",
           "UserName":"xxx@qq.com",  //you name
           "UserType":32
       },
       "MessageType":"UnbindSmartDeviceRequest"
   }
   ~~~

   ## 分享设备

   **分享**

   ~~~json
   {
       "Body":{
           "AppMatchType":1,
           "AreaId":"", 
           "DeviceId":"A99IE2102K6XCZH",
           "DeviceName":"",
           "DeviceOwner":0,   // share to friend is always 0
           "DeviceType":1,
           "LinkDevice":"",
           "StreamPassword":"",
           "StreamUser":"",
           "AccessToken":"03CBBCA487954B2C9CF8DA7B002B99F5",
           "SessionId":"0",
           "UserName":"tianya202111@163.com",   //friend name
           "UserType":32
       },
       "MessageType":"BindSmartDeviceRequest"
   }
   
   ~~~

   **获取分享列表**

   ~~~json
   {
       "Body":{
           "DeviceId":"A99IE2102K6XCZH",
           "AccessToken":"03CBBCA487954B2C9CF8DA7B002B99F5",
           "SessionId":"0",
           "UserName":"xxx@qq.com",
           "UserType":32
       },
       "MessageType":"GetShareUserListRequest"
   }
   ~~~

   **取消共享并删除设备**

   ~~~json
   {
       "Body":{
           "DeviceId":"A99IE2102K6XCZH",
           "DeviceOwner":0,   //unshare alawys 0,if you want to delete owne device DeviceOwner = 1
           "AccessToken":"03CBBCA487954B2C9CF8DA7B002B99F5",
           "SessionId":"0",
           "UserName":"tianya202111@163.com",//the username you shared, if you want to delete owne device,DeviceOwner=1
           "UserType":32
       },
       "MessageType":"UnbindSmartDeviceRequest"
   }
   //delete owne device :DeviceOwner = 1,UserName is owne username
   ~~~

   ## LED 开关

   ~~~json
   {
       "Body":{
           "DeviceId":"A99IE2102K6XCZH",
           "ParamArray":[
               {
                   "CMDType":"cap23",
                   "DeviceParam":{
                       "device_led_switch":1
                   }
               }
           ],
           "AccessToken":"7394A5E6888C423191F878E38F81DD0A",
           "SessionId":"null",
           "UserName":"xxx@qq.com",
           "UserType":32
       },
       "MessageType":"AppSetDeviceParamRequest"
   }
   ~~~

   ## 门铃 LED

   ~~~json
    public static final String DoorbellLedSwitch = "cap45";  //use 'DoorbellLedSwitch' ,rather than 'LedSwitch'
   
   {
       "Body":{
           "DeviceId":"ZHAWE210DNOVVGP",
           "ParamArray":[
               {
                   "CMDType":"cap45",
                   "DeviceParam":{
                       "doorbell_led_switch":0
                   }
               }
           ],
           "AccessToken":"AE1C4445EB8E4E389D837622E5D9D084",
           "SessionId":"0",
           "UserName":"xxx@qq.com",
           "UserType":32
       },
       "MessageType":"AppSetDeviceParamRequest"
   }
   ~~~

   ~~~jso
   //"LedSwitch" request param:
           {
                   "CMDType":"cap23",
                   "DeviceParam":{
                       "device_led_switch":1  //device_led_switch
                   }
               }
   // "DoorbellLedSwitch" request param:
        {
                   "CMDType":"cap45",
                   "DeviceParam":{
                       "doorbell_led_switch":0   //doorbell_led_switch
                   }
               }
   ~~~

   ## 云存储

   **设备是否支持云存储**

   ~~~java
   cap26 > 0;
   cap26 == 1 --> OSS_aliyun;
   cap26 == 2 --> OSS_aws;
   cap26 == 3 --> OSS_liantong;
   ~~~

   **设备是否已开通云存储以及云存储是否已过期。**

   ~~~java
   Request:
    https://cn-css.ulifecam.com/api/cloudstore/cloudstore-service/service/data-valid?device_id=A99TB210HB6KGJE&token=21EE0F9BD661488FBB0BA1672772631D&check=novel&username=15599368709
   
   errorCode:
   responseCode == 1200;  //expired
   responseCode == 1024;  //not activated
   ~~~

   request param:

   | Field     | Type   | Describe                |
   | --------- | ------ | ----------------------- |
   | device_id | String |                         |
   | token     | String |                         |
   | check     | String | "novel"  -- fixed value |
   | username  | String |                         |

~~~json
//GetCloudSetMenuInfoResult
{
    "code":"0",
    "message":"success",
    "data":[
        {
            "id":2738,
            "orderNo":"202212151650428609",
            "planId":61,
            "deviceId":"A99TB210HB6KGJE",
            "status":"1",   //1:in use  0：expired   2：renew  7：remove
            "startTime":"1687852144",
            "preinvalidTime":"1690444144",
            "dataExpiredTime":"1691048944",    // expiration time of Cloud storage
            "serviceTime":null,
            "invalidTime":null,
            "switchEnable":"1",
            "createUser":"15599368709",
            "createTime":"1673575937",
            "modifyTime":null,
            "modifyUser":null,
            "planName":"7天云存储",
            "dataLife":7,
            "dateLife":7,
            "serviceLife":30,
            "orderCount":1,
            "payTime":"20221215165042",
            "count":0,
            "timeStamp":0,
            "freeFlag":null
        }
    ],
    "sucessully":true
}
~~~

**获取云存储数据**

*1 获取所有数据*

~~~java
Request:
https://cn-css.ulifecam.com/api/cloudstore/cloudstore-service/move-video/time-line?device_id=A99ZC210AQGL6GE&start_time=1688581800&end_time=1688668200&token=21EE0F9BD661488FBB0BA1672772631D&check=novel&username=15599368709&version=1.0 
~~~

request param:

| Field      | Type   | Describe                |
| ---------- | ------ | ----------------------- |
| device_id  | String |                         |
| start_time | Long   | 查询时间段的开始时间    |
| end_time   | Long   | 查询时间段的结束时间    |
| token      | String |                         |
| check      | String | "novel"  -- fixed value |
| username   | String |                         |
| version    | String | 无需传递，兼容旧平台。  |

*2 分页获取数据*

~~~java
Request:
https://cn-css.ulifecam.com/api/cloudstore/cloudstore-service/move-video/time-line/details/pagination?device_id=A99ZC210AQGL6GE&start_time=1688581800&end_time=1688668200&token=21EE0F9BD661488FBB0BA1672772631D&check=novel&username=15599368709&version=1.0&size=300&page_number=8&sort=des
~~~

request param:

| Field       | Type    | Describe                                  |
| ----------- | ------- | ----------------------------------------- |
| device_id   | String  |                                           |
| start_time  | Long    | 查询时间段的开始时间                      |
| end_time    | Long    | 查询时间段的结束时间                      |
| token       | String  |                                           |
| check       | String  | "novel"  -- fixed value                   |
| username    | String  |                                           |
| version     | String  | 无需传递，兼容旧平台。                    |
| size        | Integer | 每次查询的总数量。                        |
| page_number | Integer | 当前查询的页码 n。                        |
| sort        | String  | 排序顺序："des"（降序），"asce"（升序）。 |

response:

~~~json
{
    "code":"0",
    "message":"success",
    "data":[
        {
            "deviceId":null,
            "startTime":1688634714,    //Single file start timestamp
            "endTime":1688634726,     //Single file end timestamp
            "alarmType":"66",			//Event Type
            "bucket":"gos-media-c",
            "key":"7_A99ZC210AQGL6GE/1688634714.media",
            "dateLife":0,
            "cycle":0,
            "deleteDate":null,
            "faces":null,
            "shoulders":null
        }
    ],
    "sucessully":true
}
~~~

**获取单个回放文件**

*1 获取云存储信息*

~~~java
Request:
https://cn-css.ulifecam.com/api/cloudstore/cloudstore-service/sts/check-token
~~~

post param:

| Field     | Type   | Describe                                           |
| --------- | ------ | -------------------------------------------------- |
| device_id | String |                                                    |
| token     | String |                                                    |
| check     | String | "novel"                                            |
| username  | String |                                                    |
| version   | String | No need to transfer, compatible with old platforms |

response:

~~~json
{
    "code":"0",
    "message":"success",
    "data":{
        "creater":"15599368709",
        "deviceid":"A99ZC210AQGL6GE",
        "key":"STS.NTWdPTScoYdK6A1CfpweFuciM",
        "secret":"xj2suBFXo6WLfXyCKaZgGUuhRSGw5D1vbRp5wTAivgW",
        "token":"CAIS9QF1q6Ft5B2yfSjIr5fiL+.....................",
        "endPoint":"http://oss-cn-shenzhen.aliyuncs.com",
        "bucket":null,
        "durationSeconds":3600,
        "timeStamp":1688800839,
        "expiration":"2023-07-08T08:20:39Z"
    },
    "sucessully":true
}
// GetCloudOssInfoResult.class
// Call Alibaba Cloud sdk , get the OSSClient
~~~

*2 下载文件*

~~~java
1.DownloadHandler -- download file;
2.after successful download PlayHandler.class -- begin play file

~~~

## TF 回看

TF 回放数据使用 TCP 协议，因此建议使用从 SDK 获取数据的方法。

~~~kotlin
GosSession.getSession().appGetBSAddress(uuid,"")   //To initialize the TCP server address, this function needs to be called
val result = RemoteDataSource.login(username, psw)
~~~

我们提供了一个新的演示，包括获取云存储和 TF 回放的功能

## 编辑设备 Wi-Fi

按下门铃的设置按钮两次，门铃将进入配网模式，使用门铃扫描生成二维码

~~~java
//.1.GetBindTokenRequest  
public boolean getBindToken(String userName, String deviceId) {
        GetBindTokenRequest request = new GetBindTokenRequest(userName, deviceId);
        this.submit(new PlatformApiTask(request, this.TIME_OUT));
        return true;
    }
//get the token
//2.Generated QR code and use doorbell to scan it
//3.QueryUserBindResultRequest,Once a second
    public boolean queryUserBindResult(String userName, String bindToken) {
        QueryUserBindRequest request = new QueryUserBindRequest(userName, bindToken);
        this.submit(new PlatformApiTask(request, this.TIME_OUT));
        return true;
    } 
~~~

~~~kotlin
when(responseCode){
    0->{}  //success
    -10000->{} //Benchmark error ,recall queryUserBindResult
    10128,-10128:->{} //The device has been bound to another account
    -10130->{} //TOKEN failure
    
}
~~~

## 获取视频封面

**接收缩略图数据**

~~~java
int timestamp = (int) (System.currentTimeMillis() / 1000L);// IPC 为unsigned int, so +24, timezone > 0;
int timezone = mDevice.getVerifyTimezone();
mDevice.getConnection().openRecJpeg(0, mDevice.getStreamPsw(), timestamp, timezone, videoPlay);
    IVideoPlay videoPlay = new IVideoPlay() {
        @Override
        public void onVideoStream(String s, AvFrame avFrame) {
            //nFrameNo=1,nFrameType=102,nTimestamp=1592964007,nReserved=1,nDataSize=7035
            int nFrameType = Packet.byteArrayToInt_Little(avFrame.data, 4);//Corresponding to this frame type 102
            if (nFrameType != 102) {
                return;
            }
            File file = new File(filePath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(avFrame.data, 32, nDataSize);   //avFrame.data 
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };
~~~

**获取缩略图数据**

~~~java
 mDevice.getConnection().getRecJpeg(0, buf, buf.length);
~~~

| Field   | type      | Describe         |
| ------- | --------- | ---------------- |
| channel | Integer   | 0                |
| utcTime | Integer[] | 视频开始时间戳   |
| count   | Integer   | 视频时间戳的数量 |

## 门铃音量

~~~kotlin
//doorbell volume 
     class DoorbellVolumeParam : BaseDeviceParam(){
        var doorbell_ring_switch:Int = 0    
        var volume_level:Int = 0;  
    }
~~~

| Field                | type    | Describe   |
| -------------------- | ------- | ---------- |
| doorbell_ring_switch | Integer | 0-off,1-on |
| volume_level         | Integer | 0-100      |

***request***

~~~kotlin
 val mDoorbellVolumeParam  = DoorbellVolumeParam()
 mDoorbellVolumeParam.doorbell_ring_switch = OnOff.On
 mDoorbellVolumeParam.volume_level = progress
 val devParamArray = DevParamArray(
                        DevParam.DevParamCmdType.DoorbellVolume,
                        mDoorbellVolumeParam
                    )
 lifecycleScope.launch {
                        RemoteDataSource.setDeviceParam(devParamArray,deviceId = deviceId)
                    }
~~~

# IPC

## 添加 IPC 设备

***1.第一种添加方法与门铃相同，为IPC扫描码生成QRCode***

***2.第二种添加方式是手机扫描IPC机上的二维码，获取设备ID***

~~~kotlin
//1.Query device binding status    
val job = asyncTask {
            val map = mapOf(
                Pair("DeviceId", deviceId),
                Pair("UserType", GApplication.app.userType),
                Pair("SessionId", GApplication.app.user.sessionId),
                Pair("AccessToken", gsoSession.accessToken),
                Pair("UserName",   GApplication.app.user.userName!!),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", QueryDeviceBindRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.checkBindStatus(json)
            Log.e(TAG, "checkBindStatus: $response")
            return@asyncTask response.body()
        }
~~~

~~~kotlin
data class BindStatus(
    val deviceID:String,
    val bindStatus:Int
)

         when(it?.bindStatus){
                BindStatus.BIND->{
                    finish()
                    Log.e(TAG, "queryBindStatus: The device has been bound" )
                }
                BindStatus.SHARE_BIND, BindStatus.OWN_BIND->{
                    finish()
                    Log.e(TAG, "queryBindStatus: You have bound the device" )
                }
                BindStatus.UNBIND->{
                    Log.e(TAG, "queryBindStatus: device not bound" )
                    mViewModel.getBindToken(devId)
                }
            }
~~~

~~~kotlin
//2.The device is not bound. Get the bound token
     val job = asyncTask {
            val map = mapOf(
                Pair("UserName", userName),
                Pair("DeviceId", deviceId),
                Pair("UserType", GApplication.app.userType),
                Pair("SessionId", GApplication.app.user.sessionId),
                Pair("AccessToken", gsoSession.accessToken),
            )
            val nMap = mapOf(
                Pair("Body", map),
                Pair("MessageType", GetBindTokenRequest)
            )
            val json = Gson().toJson(nMap).toRequestBody()
            val response = RetrofitClient.apiService.getBindToken(json)

            return@asyncTask response.body()
        }
~~~

~~~kotlin
class BindTokenResult {
    var BindToken: String? = null
}

//According to the token query binding state, the interface and doorbell query binding state are the same
//The device will automatically bind when it is connected to the Internet
~~~

### 重启设备

| Field   | type    | Describe          |
| ------- | ------- | ----------------- |
| CMDType | Integer | 4400(Fixed value) |
| channel | Integer | 0                 |

~~~kotlin
data class RebootParam(
    val CMDType: Int,
    val channel:Int
):BaseDeviceParam()


       val job = asyncTask {
            val cmdBody = CmdBody(
                GApplication.app.user.token!!,
                deviceId,
                baseDeviceParam,
                GApplication.app.user.sessionId!!,
                GApplication.app.user.userName!!,
                GApplication.app.userType,
            )

            val cmdRequestParam = CmdRequestParam(
                cmdBody,
                BypassParamRequest
            )

            val json = Gson().toJson(cmdRequestParam).toRequestBody()
            val response = RetrofitClient.apiService.getCMDParam(json)
        }
~~~

### 云台控制

~~~kotlin
data class PztCmdParam(
    val control:Int,
    val CMDType: Int,
    val channel:Int
):BaseDeviceParam()


//Set direction

        val job = asyncTask {
            val cmdBody = CmdBody(
                GApplication.app.user.token!!,
                deviceId,
                baseDeviceParam,
                GApplication.app.user.sessionId!!,
                GApplication.app.user.userName!!,
                GApplication.app.userType,
            )

            val cmdRequestParam = CmdRequestParam(
                cmdBody,
                BypassParamRequest
            )

            val json = Gson().toJson(cmdRequestParam).toRequestBody()
            val response = RetrofitClient.apiService.getCMDParam(json)
        }

~~~

| Field   | type    | Describe     |
| ------- | ------- | ------------ |
| control |         | Ptz          |
| CMDType | Integer | 4097(固定值) |
| channel | Integer | 0            |

~~~java
public class Ptz {
    public static final int PTZ_KEEP_UP = 38; // 持续向上转动
    public static final int PTZ_KEEP_DOWN = 39; // 持续向下转动
    public static final int PTZ_KEEP_LEFT = 40; // 持续向左转动
    public static final int PTZ_KEEP_RIGHT = 41; // 持续向右转动
    public static final int PTZ_STOP = 0; // 停止转动
    public static final int PTZ_UP = 1; // 向上转动
    public static final int PTZ_DOWN = 2; // 向下转动
    public static final int PTZ_LEFT = 3; // 向左转动
    public static final int PTZ_RIGHT = 6; // 向右转动
    public static final int PTZ_SELF_CHECK = 252; // 云台自检
    public static final int PTZ_VIRTUAL_CHECK = 253; // 虚拟转向-校准
    public static final int PTZ_VIRTUALTO = 254; // 虚拟转向-APP上直接点击屏蔽坐标
}

//If the parameter is PTZ_KEEP_UP, it keeps rotating. To stop, send PTZ_STOP
~~~





# 获取语音信息列表请求

获取语音报警资源文件

```kotlin
data class Param(
    val VoicePlayId:Int,
    val VoiceUrl:String,
    val PlayLanguage:String,
    val Describe:String,
)
```

| Field        | type   | Describe                                                     |
| ------------ | ------ | ------------------------------------------------------------ |
| VoicePlayId  | Int    | 联动声音广播音频文件序列号（cap40:：un_type）                |
| VoiceUrl     | String | 联动声音广播音频文件下载链接；音频需要g711A格式的单通道8K采样率，g711A文件头已删除（cap40:：url） |
| PlayLanguage | String |                                                              |
| Describe     | String | 报警音频文件说明                                             |

```json
cap40
{
	...,
  "audio": {
    "un_switch": 0,
    "un_times": 1,
    "un_volume": 80,
    "un_type": 0,
    "url": "https://xxxx"
  },
...
}
```



# 获取当月文件

获取一个月内是否有警报

```kotlin
mDevice.getConnection().getFileForMonth(0, 0)
```

```java
    public void onDevEvent(String s, DevResult devResult) {
        if(devResult.getDevCmd() == DevResult.DevCmd.getFileForMonth){
            dismissLoading();
            if(ResultCode.SUCCESS == devResult.getResponseCode()){
                GetFileForMonthResult result = (GetFileForMonthResult) devResult;
                forMonthList = result.fileForMoth.mothFile;
                mTfDayAdapter.notifyDataSetChanged();
            }
        }
    }
```

GetFileForMonthResult

获取FileForMonth的集合，如果当天存在视频，则日期将在集合中

```
FileForMonth [fileStatus=0, totalNum=0, currNo=0, mothFile=[ForMonth [body=202412293, monthTime=20241229, fileNum=3], ForMonth [body=202412303, monthTime=20241230, fileNum=3], ForMonth [body=202412313, monthTime=20241231, fileNum=3], ForMonth [body=202501013, monthTime=20250101, fileNum=3], ForMonth [body=202501023, monthTime=20250102, fileNum=3], ForMonth [body=202501033, monthTime=20250103, fileNum=3], ForMonth [body=202501043, monthTime=20250104, fileNum=3], ForMonth [body=202501053, monthTime=20250105, fileNum=3], ForMonth [body=202501063, monthTime=20250106, fileNum=3], ForMonth [body=202501073, monthTime=20250107, fileNum=3], ForMonth [body=202501083, monthTime=20250108, fileNum=3], ForMonth [body=202501093, monthTime=20250109, fileNum=3], ForMonth [body=202501103, monthTime=20250110, fileNum=3], ForMonth [body=202501113, monthTime=20250111, fileNum=3], ForMonth [body=202501123, monthTime=20250112, fileNum=3], ForMonth [body=202501133, monthTime=20250113, fileNum=3]]]
```

