# Android Sdk

## Tamper Alert

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
| a_doorbell_remove_alarm   | Integer | Enable device (doorbell device) anti disassembly alarm,value: 0-OFF 1-ON |

## Power saving Mode

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
| a_doorbell_lowpower   | Integer | Enable device (low-power device) power-saving mode,value: 0-OFF 1-ON |

## Intercom

Intercom requires calling the sdk function native int NativeStartTalk(int channel,String psw)

| Field name | Type    | Describe               |
| ---------- | ------- | ---------------------- |
| channel    | Integer | 0                      |
| psw        | String  | the password if stream |



## Volume setting （device）

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
| volume   | Integer | device voice，value: 0-100 |

## Recording Duration

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
| un_duration | Integer | The duration of a single file for event recording; Current value：6、9、12、15 |

## Take Picture

* nativeCapture 

  

* | Field name | Type   | Describe                                                 |
  | ---------- | ------ | -------------------------------------------------------- |
  | filePath   | String | file save path                                           |
  | port       | long   | GosMediaPlayer.getPort()  Create a port for each decoder |

## Record

nativeStartRecord

| Field name | Type    | Describe                                                 |
| ---------- | ------- | -------------------------------------------------------- |
| port       | long    | GosMediaPlayer.getPort()  Create a port for each decoder |
| filePath   | String  | file save path                                           |
| flag       | Integer | a tag                                                    |





## Video Quality

nativeAVSwsScale 

| Field name | Type    | Describe                                                 |
| ---------- | ------- | -------------------------------------------------------- |
| port       | long    | GosMediaPlayer.getPort()  Create a port for each decoder |
| nEnable    | Integer | 0-OFF,1-ON                                               |
| nWidth     | Integer | Modified width                                           |
| nHeight    | Integer | Modified height                                          |

## History

* TF file

### Get file for month

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
| fileStatus | Integer | no use |
| totalNum | Integer | no use  |
| currNo | Integer | no use |
| monthTime | Integer | used to sort  |
| fileNum | Integer |  0- no file|

### Get file for day

    * NativeGetRecDayEventRefresh

| Field name    | Type    | Describe                                     |
| ----------- | ------- | -------------------------------------------- | 
| channel | Integer | 0|
| time | Integer | current time|
| type | Integer | 0-normal record ,1-aram record|
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
| total_num | Integer | Total pages |
| page_num | Integer | Current page |
| start_time | Integer |  |
| end_time | Integer |  |
| type | Integer | 0-normal record ,1-aram record |

## Album

* Photos and videos are stored in the phone's memory

## Battery Status

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
| battery_level | Integer | Current battery percentage; value：0-100 |
| charging | Integer | charge status  1-charging,0-idle |

## Speaker

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

## Cellular Mobile Network (Automatic)

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

## Infrared Detection

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
| un_switch    | Integer | pir switch 0-OFF, 1-ON                   |
| un_sensitivy | Integer | PIR level 20-40-60-80-100                          |
| un_stay      | Integer | Stay mode：level 0-3                                  |
| un_cdtime    | Integer | PIR detection cooling duration setting：second                            |

```C
typedef enum{
	ULIFE_HWPIR_NONE,				// PIR parameter setting not supported
	ULIFE_HWPIR_SWITCH 		= 1,	// PIR parameter setting  supported
	ULIFE_HWPIR_SENSITIVITY	= 2,	// Support for PIR sensitivity settings
	ULIFE_HWPIR_STAY_MODE	= 4,	// Supports PIR un_stay mode settings
    ULIFE_HWPIR_CDTIME		= 8,	// Support for PIR cooling time setting
}Ulife_HWPir_en;
```

# Device Capability Set

## Capability Set Request Structure

* ParamArray Multiple settings can be set at once
  </br> When we obtain the device list, we will obtain the device's capability set, `DeviceEntity`
  </br>get all device capability from `DeviceEntity.Cap` and we use `Device. parse (Cap cap)` to
  parse the device capability set,The parameters obtained are all of type int
  </br> set device capability by `com.gos.platform.api.devparam.DevParam.DevParamCmdType`
  </br> if you want to set the params to device,you can refer to `DevParamCmdType`,this classes in
  SDK display the parameters that the device can set
  </br> we get `cap20` is integer,and we set device params user `cap20` just a key

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

* Device Type

## cap2

* Main stream resolution size Width: high 16 bits Height: low 16 bits
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
| un_video_quality    | Integer | Current live streaming resolution index value, corresponding to video_resolution subscript of resolution                |
| src_width | Integer | Video source width                          |
| src_height      | Integer | Video source height                                 |
| dest_width    | Integer | APP Display Video Width                           |
| dest_height    | Integer | APP Display Video Height                           |

## cap3

* Subcode stream (no used)

## cap4

* 3rd channel code stream (no used)

## cap5

* Is there an encryption IC 0: No 1: Yes
  ``
  cap.cap5 == 1
  ``

## cap6

* Is there a PIR sensor, 0: none, 1: yes
    * `com.gos.platform.api.devparam.PirSettingParam`

  ``
  cap.cap6 == 1
  ``
  means support pir, see detail by `Infrared Detection`

## cap7

* Is there a pan tilt
    * `com.gos.platform.api.devparam.PtzPositionPresetSelectedParam`

<br> 0- None 1- Supports pan tilt control
<br> 2- Supports pan tilt control and preset positions
<br> 3- Supports pan tilt control/preset and cruise control
<br> 4- Support for pan tilt control/preset and privacy mode
<br> 5- Support pan tilt control/preset position and self check
<br> 32- Full function cloud console

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
| Privacy    | Integer | Whether to use privacy mode 0-OFF,1-ON |
| x | Integer | Coordinate values that can be preset for point x                         |
| y      | Integer | Coordinate values that can be preset for point y                    |

* `Note: When the APP enables preset points, privacy mode must be turned off first; The device has received a preset setting and is forced to turn off privacy mode`
  <br>  When setting privacy mode, the preset point Active coordinate value is forcibly set to
  -1;<br> When setting preset points, privacy mode needs to be cancelled first;

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
| position | Object | Preset point coordinate values                         |
| x | Integer | Coordinate values that can be preset for point x                         |
| y      | Integer | Coordinate values that can be preset for point y                    |
| index      | Integer | unique identification                   |
| text      | String | Text description                  |
| picture      | String | preview url                   |

## cap8

* Is there a microphone
  ``cap.cap8 == 1``

## cap9

* Is there a horn
  ``cap.cap9 == 1``

## cap10

* Does it support SD card slots
  ``cap.cap10 == 1``

## cap11

* Is there a temperature sensing probe
  ``cap.cap11 == 1``

## cap12

* Does it support synchronous time zone 1. Only supports positive time zone 2. Supports half time
  zone
  ``cap.cap12 > 0`` support

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
| AppTimeSec | Integer | Current timestamp                         |
| un_TimeZone      | Integer | timeZone(-12~11)                  |

## cap13

* Does it support night vision 0: No 1: Yes 2: Full color night vision
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
| un_auto | Integer | Whether to enable automatic mode/intelligent night vision, with values of 0- off 1- on                 |
| un_day_night      | Integer | Black and white mode, night vision mode is effective when automatic mode is turned off, with values of 0- day 1- night vision    |
| un_day_night      | Integer | Full color mode, night vision mode is effective when automatic mode is turned off, with values ranging from 0- black and white to 1- full color |

* Common Agreement for Ordinary Night Vision and Full Color Night Vision;

## cap14

* Is there a network card with 0: wifi 1 wired 2: wifi and wired
  *see detail by `Cellular Mobile Network (Automatic)`

<br> 0 supports WIFI
<br> 1 wired
<br> 2 2.4G WIFI+wired
<br> 3 5G WIFI
<br> 4 2.4G/5G dual mixing WIFI
<br> 5 4G traffic
<br> 6 2.4G/5G dual mixing WIFI+wired

* `com.gos.platform.api.devparam.CurrentWifiInfoParam`

## cap15

* Does it support smart scanning? 0 represents not supported, 1 represents 7601smart, and 2
  represents 8188mart
* no used

## cap16

* Does it support mobile detection? 0 does not support 1.support 2. supports self selected areas. 3.
  supports custom time
  `cap.cap16 > 0` support


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
| c_sensitivity | Integer | Value range (1~100) is the threshold value for motion detection level. The smaller the value, the higher the sensitivity. 1 (max)~100 (min): (Off: 100, Low: 90, Medium: 60, High: 30)                      |
| c_switch      | Integer | switch  0-Off 1-On               |
| un_width      | Integer |                  |
| un_height      | Integer |                 |
| s_threshold      | short |       no used          |
| un_mode      | Integer | Manual division of coordinates 0 or automatic multi screen division of coordinates 1*                  |
| un_submode      | Integer | 1x1=0, 2x2=1, 3x3=2, 4x4=3 in multi screen mode*                |
| un_enable      | Integer | Based on whether to enable up to 4x4=16 for the selected area in multi screen mode* |
| rect_x      | Integer | Number of regions in the horizontal axis direction (rect_x * rect_y<=200) by hand                 |
| rect_y      | Integer | Number of regions in the vertical coordinate direction  by hand                |
| custom_time_period      | String Array |                   |
| un_enable_str      |  Array | Is motion detection enabled for each region (JSON format is passed in an array with a size of rect_x * rect_y)           |

## cap17

* Set the duration of video recording
  `cap.cap17 == 1`

* `com.gos.platform.api.devparam.RecordDurationParam`
    * see detail by `Recording Duration`

## cap18

* Set the lighting switch
  `cap.cap18 == 1`

## cap19

* Sound detection alarm
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
| un_switch | Integer |  Whether to enable motion tracking; Value: 0-Off 1-On                 |
| un_sensitivity      | Detection sensitivity, values: 1-low, 2-medium, 3-high            |

## cap20

* lullaby
  `cap.cap20 > 0`  no used

## cap21

* Is it equipped with a battery
  `cap.cap21 == 1`

## cap22

* Does it support WIFI remote wake-up
  `cap.cap22 == 1` support

## cap23

* Is there a status indicator light
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
| device_led_switch | Integer |   Value: 0-Off 1-On                 |

## cap24

* Is there a camera switch
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

* Is there a microphone switch
  `cap.cap25 == 1`

* `com.gos.platform.api.devparam.MicSwitchParam`
* see detail by `Speaker`

## cap26

* Whether to support cloud storage 0: No 1: Alibaba Cloud 2: Amazon 3: China Unicom
  `cap.cap26 > 0`

## cap27

* Whether to support video stream encryption
  `cap.cap27 == 1`

## cap28

* Whether to play TF stream

## cap29

* Whether to support power acquisition
  `cap.cap29 == 1` support
    * `com.gos.platform.api.devparam.BatteryInfoParam`
      <br> see detail by `Battery Status`

## cap30

* Get the strength of the gateway model
  `cap.cap30 == 1`

## cap31

* Is there an alexa function
  <br> `cap.cap31 == 1 || cap.cap31 == 3`  SupportAlexaSkills
  <br> `cap.cap31 == 2 || cap.cap31 == 3`  SupportGoogleHome

## cap32

* Rotate video recording 0. Not supported 1. Supported
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
| device_switch | Integer |  0- No flipping 1- Horizontal flipping 2- Vertical flipping 3- Horizontal vertical flipping  |

## cap33

* humidity  `no used`

## cap34

* wbgt `no used`

## cap35

* Whether to support the doorbell ringtone setting
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
| doorbell_ring_switch | Integer |  Ring switch  0-OFF 1-ON|
| volume_level | Integer |  Volume level and switch  0-100|

## cap36

* Doorbell LED Light Switch
  `cap.cap36 == 1`  no used

## cap37

* Whether to support the camera switch plan
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
| enable | Integer | Is the plan activated  0-OFF 1-ON|
| repeat | Integer |  Plan period, in bits and values, if enabled, the position is 1, seven days of the week, days [7]={"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}; 0 indicates a one-time effect|
| start_time | Integer |  Use second expression; For example, the 8:00 time zone is 28800, which means 8 * 60 * 60|
| end_time | Integer | |

## cap38

* Whether the sensor can be mounted
  `cap.cap38 > 0`  support

## cap39

* Whether to cry to the police
  `cap.cap39 == 1` support

## cap40

* Whether to support sound and light alarm
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
| un_switch   | Integer | Sound and light linkage main switch, value: 0- closed 1- open                        |
| schedule    | Object  | Linkage Plan                                                    |
| un_switch   | Integer | Linkage plan switch, values: 0- off 1- on                           |
| un_repeat   | Integer | Plan period, in bits and values, if enabled, the position is 1, seven days of the week, days [7]={"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}; 0 indicates a one-time effect |
| start_time  | Integer | The start time of the linkage plan, expressed in seconds; For example, the 8:00 time zone is 28800, which means 8  * 60 * 60 |
| end_time    | Integer | End time of linkage plan                                           |
| audio       | Object  | Linkage sound                                                   |
| un_switch   | Integer | Linkage sound switch, value: 0-Off 1-On                           |
| un_times    | Integer | Number of linked sound broadcasts                                          |
| un_volume   | Integer | Linkage sound broadcasting volume                                          |
| un_type     | Integer | Serial number of audio files for linkage sound broadcasting                                  |
| url         | String  | Linkage sound broadcasting audio file download link; Audio requires single channel 8K sampling rate in g711A format, with G711A file header removed |
| light       | Object  | Linkage lighting                                                     |
| un_switch   | Integer | Linkage light switch, value: 0-Off 1-On                           |
| un_duration | Integer |Duration of linkage light on                                        |

## cap41

* Whether to support automatic restart plan
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
| repeat    | Integer  | Planning cycle set by bit                                                   |
| start_time   | Integer | Restart time point                         |

## cap42

* Wide dynamic settings   `no used`

## cap43

* HTTPS OTA Upgrade
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
| upgrade_item_type   | Integer |Upgraded module, refer to enumeration UPGRADE_ ITEM_ TYPE, non mandatory                       |
| upgrade_status    | Integer  | Upgrade status, refer to enumerating UPRADE_ State_ E                                                   |
| upgrade_progress   | Integer | ota progress                         |

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
  E_UPGRADE_DOWNLOADING = 0,            //(0)Downloading
  E_UPGRADE_FAILURE,              		//(1)Upgrade failed
  E_UPGRADE_SUCCESS,                	//(2)Upgrade Successful
  E_UPGRADE_DOWNLOAD_START,				//(3)Start downloading
  E_UPGRADE_DOWNLOAD_FINISH,			//(4)Download completed
  E_UPGRADE_CHECK_ERROR,				//(5)Upgrade package validation error
  E_UPGRADE_INSTALL_START,				//(6)Start Installation
}UPRADE_STATE_E;
```

## cap44

* Does the base station have ringtone settings `no used`

## cap45

* Outdoor doorbell LED setting
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
| doorbell_led_switch   | Whether to turn on the device (doorbell device) status indicator light, with values of 0- off 1- on                      |

## cap46

* Outdoor doorbell ringtone settings
* no used

## cap47

* Outdoor doorbell forced demolition alarm
  `cap.cap47 == 1` support

* `com.gos.platform.api.devparam.RemoveAlarmParam`
  <br> see detail by `Tamper Alert`

## cap48

* Power saving mode

`cap.cap48 == 1` support

* `com.gos.platform.api.devparam.LowpowerModeSettingParam`
  <br> see detail by `Power saving Mode`

## cap49

* PIR specific settings
* no used

## cap50

`cap.cap50 > 0` support

* Anthropomorphic detection alarm 0: No 1: Anthropomorphic detection alarm 2: Anthropomorphic
  detection can be selected by region

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
| un_switch      | Integer | Human shape detection switch, values: 0-Off 1-On |
| un_sensitivity | Integer | Sensitivity, from low to high 1, 2, 3, 4, 5 |
| permcnt        | Integer | Number of areas                          |
| perms          | Array   | Details of all regions                      |
| pcnt           | Integer | Number of coordinate points in a single area                |
| rect           | Array   | Details of individual area coordinate points                |
| x              | Integer | Coordinate point x coordinate value, ranging from 0 to 10000        |
| y              | Integer | Coordinate point y coordinate value, ranging from 0 to 10000        |

## cap51

* Object detection alarm
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
| un_switch      | Integer | Whether to enable motion tracking; Value: 0-Off 1-On|

## cap52

* Humanoid tracking
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
| un_switch      | Integer | Whether to enable humanoid tracking; Value: 0-Off 1-On|

## cap53

* Volume Setting
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
| volume      | Integer |  Device (universal device) prompt volume, ranging from 0 to 100|

## cap54

* Push frequency and time interval settings
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
| interval                 | Integer | Push interval                                                     |
| motion_detection_switch  | Integer | Movement detection alarm push switch, values: 0-Off 1-On                    |
| sound_detection_switch   | Integer | Audible alarm push switch, value: 0-Off 1-On                       |
| person_detection_switch  | Integer | Humanoid detection alarm push switch, values: 0- off 1- on                    |
| cry_alarm_switch         | Integer | Cry detection alarm push switch, values: 0-Off 1-On                   |
| temperature_alarm_switch | Integer | Temperature alarm push switch, value: 0- off 1- on                        |
| pir_alarm_switch         | Integer | PIR alarm push switch, values: 0-Off 1-On                         |
| enable                   | Integer | Is the plan activated                                                |
| repeat                   | Integer | Plan period, in bits and values, if enabled, the position is 1, seven days of the week, days [7]={"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}; |
| start_time               | Integer | Use second expression; For example, the 8:00 time zone is 28800, which means 8  * 60 * 60                  |
| end_time                 | Integer |                                              |

## cap55

* The Capability Set of P2P Encryption
  `cap.cap55`
    * no used

## cap56

* Device supports streaming type 1: TUTK 2: P2P 3: TCP 4: P2P_ TCP (all supported)
  `cap.cap56 = 2`

## cap57

* Intercom type 0: Half duplex 1: Full duplex 2: Both half and full duplex are supported
  `cap.cap57 = 1`

## cap58

* Is it more than one drag
  `cap.cap58 == 1`
    * the doorbell no used

## cap59

* Device audio type 0: AAC 1: G711A
  `cap.cap59 = 0`

## cap60

* AI access 0: No access 1: Access server version 2: Access device version
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

| Field name    | Type    | Describe                                           |
| -------------- | ------- | ------------------------------------------------------------ |
| CMDType        | String  | cap60                                        |
| AiMode         | Integer | 0- Not supported or completely prohibited<br/>1- Server recognition mode<br/>2- Device recognition mode<br/>3- Server+Device dual mode |
| Pet_detect     | object  | Pet identification related attributes                                             |
| Car_detect     | object  | Vehicle identification related attributes                                             |
| Face_detect    | object  | Facial recognition related attributes                                             |
| Cross_detect   | object  | Cross line detection related attributes                                             |
| un_switch      | Integer | Switch 0- Off 1- On                                           |
| un_sensitivity | Integer | The sensitivity range is as follows:<br/>Pet recognition: 0-5<br/>Vehicle recognition: 0-4<br/>Face recognition: 0-3<br/>Cross line detection: default 1 |

## cap61

* Obtain TF file list method 0: Ulife Long connection 1: P2P method 2: P2P method pagination refresh
  <br>`(cap.cap61 == 1 || cap.cap61 == 2 || cap.cap61 == 3) ? 1 : 0`  -- 0-Ulife, 1-P2P
  <br>`(cap.cap61 == 2 || cap.cap61 == 3)` support P2pPaging

## cap62

* Device cloud storage resolution
  <br>0 -- Default
  <br> 1--1 million
  <br> 2--2 million (1920 * 1080)
  <br> 3--3 million (2304 * 1296)
  <br> 4--4 million

<br>`cap.cap62 = 0`

## cap63

* Does card recording support full day recording event recording switching? 0. Not supported. 1.
  Supported
  `cap.cap63 > 0` support
    * `no used `

# Channel

* Channel is reserved and defaults to 0,all channel params set 0

# What is Cellular Mobile Network and what is its use

* As a mobile network card for devices

# Error Code

The error I currently have_ The code is not very comprehensive, I need to seek assistance from my
colleagues Here is a previous document that I hope will be helpful to you
`EN.7z`

# channel

* channel represents the channel number of the stream,We have multi-stream products,so the channel
  is defined,the default value for unichannel streaming products is 0
  
# SDK function
## connect
```java
Connection mConnection = mDevice.getConnection()
 mConnection.connect(0);
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| mConnection                  | object  | p2p long connection                                        |
| channel                 | Integer |  0    channel is reserved for expansion    |
* ConnectResult

```java
private int conStatus;
private ConnType connType;
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| conStatus                  | object  | 0-success 1-error 11-CONNECT_LOST                                        |
| connType                 | enum |  0 - UNKNOW  1-TYPE_TUTK 2-TYPE_P2P 3-TYPE_TCP 4-TYPE_P2P_TCP  |

## startVideo
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
| StreamType                 | int |  0 - VIDEO  1-AUDIO 2-VIDEO_AUDIO 3-STREAM_REC(TF Stream play back) 4-STREAM_REC_JPEG(no used)  <br /> we used 2 or 3 |
| psw                 | String | the password of stream |
| lTimeSeconds                 | int | current time |
| lTimeZone                 | int  | time zone |
| IVideoPlay                 | object  | AvFrame callback interface |

## stopVideo
```java
 mConnection.stopVideo(0, this);
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| channel                  | integer  | 0                                       |
| IVideoPlay                  | object  | AvFrame callback interface                                       |
## Start Talk
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
| psw                  | String  | the password of stream                                      |
## stopTalk
```java
mConnection.stopTalk(0);
```
## mute/unmute

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
## startRecord
```java
 mMediaPlayer.startRecord(recordPath,0);
```
| Field name    | Type    | Describe                                           |
| ------------------------ | ------- | ------------------------------------------------------------ |
| mMediaPlayer                  | GosMediaPlayer  | LAV Filters                                       |
| recordPath                  | String  | file save path                                       |
| flag                  | integer  | 0     every mMediaPlayer has a flag,Just to distinguish GosMediaPlayer    |
## stopRecord
```java
mMediaPlayer.stopRecord();
```

# keep alive

* The doorbell device needs to remain awake when entering the live streaming page if device status
  has changed ,the device will report the latest status `NotifyDeviceStatus`
  
```java
//Check if the device is in power saving mode
DevParamResult devParamResult = GosSession.getSession().AppGetDeviceParam(mDeviceId, LowpowerModeSetting);
//if device in lowpower mode ,we should try to wake it
PlatModule.getModule().wakeUpDeviceSyn(mDeviceId);
SystemClock.sleep(1000);
//Query device online status
QueryDeviceOnlineStatusResult statusResult = PlatModule.getModule().queryDeviceOnlineStatusSyn(mDeviceId);
//If we successfully wake up the device
connection.connect(0);
```

# TF playback

* you need to make a request MsgType refer to `com.gos.platform.api.request.Request.MsgType` , These
  MsgTypes are signaling agreed upon with the server

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

* page_data  `202305303--- 20230530 is monthday an 3 is the total file of the day ` the
  class `com.gos.platform.device.domain.FileForMonth.ForMonth`
  <br>  `202305303` The 0th to 7th digits are dates ,the remaining quantity is the number of video
  files
  <br> the number of total files used to Paginated loading
  <br> `20230530` means the day has record data

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
| total_num  | Integer | total num                    |
| day_event_list   | Array |                     |
| start_time  | Integer | start time of one file                   |
| end_time         | Integer | end time                 |
| type | Integer | 0- normal 1- alarm                       |

# VPhoto HTTP API

HTTP Request Template

```kotlin
val baseUrl="https://vphoto.waophoto.com/apiv3/"
```

## Register By Uuid

```java
● URL：/apiv3/user/userRegisterByUuid
● Method：POST
```

| Field name        | Type   | Describe                                                     |
| ----------------- | ------ | ------------------------------------------------------------ |
| user_secret       | String | user_secret=base64Encode(rsa(user_uuid))                     |
| company_name      | String | VPhoto                                                       |
| nickname          | String | user name                                                    |
| profile_image     | String | User profile picture address (can be passed blank)           |
| timestamp         | String | let timestamp = Date.currentTimeStamp                        |
| sign<header>      | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp                        |

## Signin By Uuid

```
● URL：/apiv3/user/signinByUuid
● Method：POST
```

| Field name     | Type   | Describe                                                     |
| -------------- | ------ | ------------------------------------------------------------ |
| company_name   | String | VPhoto                                                       |
| user_fcm_token | String | Can pass ""                                                  |
| user_platform  | String | 1                                                            |
| user_secret    | String | uuid converts rsa encryption to base64                       |
| sign<header>   | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp      | String | let timestamp = Date.currentTimeStamp                        |

## Bind By Device

```
● URL：/apiv3/user/bindByDeviceConnectionCode
● Method：POST
```

| Field name             | Type   | Describe                                                     |
| ---------------------- | ------ | ------------------------------------------------------------ |
| device_connection_code | String | Link code                                                    |
| company_name           | String | VPhoto                                                       |
| deviceUserName         | String | Can pass ""                                                  |
| sign<header>           | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header>      | String | let timestamp = Date.currentTimeStamp                        |

## Bind status

```java
● URL：/apiv3/user/bindstatus
● Method：POST
```

| Field name             | Type   | Describe                                                     |
| ---------------------- | ------ | ------------------------------------------------------------ |
| device_connection_code | String | Link code                                                    |
| user_id<header>        | String | user id                                                      |
| sign<header>           | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header>      | String | let timestamp = Date.currentTimeStamp                        |

## Update account password

```
● URL：/apiv3/user/devicecam
● Method：POST
```

| Field name        | Type   | Describe                                                     |
| ----------------- | ------ | ------------------------------------------------------------ |
| device_password   | String | Password, required if it is new, update optional             |
| device_user_name  | String | Account, if it is new, you must fill in, update optional     |
| user_id<header>   | String | user id                                                      |
| sign<header>      | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp                        |

## untie device

```
● URL： /apiv3/user/status
● Method：POST
```

| Field name        | Type   | Describe                                                     |
| ----------------- | ------ | ------------------------------------------------------------ |
| user_id           | String | user id                                                      |
| device_id         | String | device id                                                    |
| status            | String | status                                                       |
| sign<header>      | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp                        |

## Bound device

```
● URL：/apiv3/user/user_device
● Method：POST
```

| Field name        | Type   | Describe                                                     |
| ----------------- | ------ | ------------------------------------------------------------ |
| user_id           | String | user id                                                      |
| user_token        | String | signinByUuid Specifies the user_system_token field returned by the interface |
| sign<header>      | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp                        |

## presigned Url Zip

```
● URL：/apiv3/upload/presignedUrlZipS3
● Method：POST
```

| Field name        | Type   | Describe                                                     |
| ----------------- | ------ | ------------------------------------------------------------ |
| user_id           | String | user id                                                      |
| user_imei         | String | user uuid                                                    |
| image_data        | String | In parentheses is an array of device_id, where device_id is the id of the picture frame to send :{"device_id":[1,2,3]} |
| desc              | String | Can pass ""                                                  |
| data              | String | Can pass ""                                                  |
| file_size         | String | Can pass ""                                                  |
| sign<header>      | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp                        |

## presigned Url Video

```
● URL：/apiv3/upload/presignedUrlVideoS3
● Method：POST
```

| Field name        | Type   | Describe                                                     |
| ----------------- | ------ | ------------------------------------------------------------ |
| user_id           | String | user id                                                      |
| user_imei         | String | user uuid                                                    |
| video_data        | String | In parentheses is an array of device_id, where device_id is the id of the picture frame to send :{"device_id":[1,2,3]} |
| suffix            | String | Video file suffix, only support mp4 and mov two              |
| desc              | String | Can pass ""                                                  |
| data              | String | Can pass ""                                                  |
| file_size         | String | Can pass ""                                                  |
| sign<header>      | String | md5 value after parameters are arranged in ascending order and concatenated with user_secret |
| timestamp<header> | String | let timestamp = Date.currentTimeStamp                        |

## Upload File

```
● URL：presignedUrlVideoS3 or presignedUrlZipS3 returns the preSignedUrl field
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

## Login

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
| UserName                 | String  |    login email         |
| Password                 | String  |    psw -- encodeData()          |
| MobileCN                 | String  |    ""-- country no,China is +86 US is +1          |
| UserType                 | Integer  |   the app type ,gsocam is 32           |
| ProtocolType                 | Integer  |   1 -- http ,0 --tcp           |

## Verification Code

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
| UserInfo                 | String  |    user email        |
| VerifyWay                 | Integer  |     REGIST = 1,FORGET_PSW = 2          |
| MobileCN                 | String  |    ""-- country no,China is +86 US is +1          |
| UserType                 | Integer  |   the app type ,gsocam is 32           |
| Language                 | String   |   no used            |

## Register

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
| RegisterWay                 | Integer  |   2-EMAIL_ADDRESS,3-PHONE         |
| UserName                 | String  |    user email        |
| Password                 | String  |    user psw       |
| VerifyWay                 | Integer  |     REGIST = 1,FORGET_PSW = 2          |
| MobileCN                 | String  |    ""-- country no,China is +86 US is +1          |
| UserType                 | Integer  |   the app type ,gsocam is 32           |
| PhoneNumber                 | String   |   "" PhoneNumber and  EmailAddr alternative           |
| EmailAddr                 | String   |   "" PhoneNumber and  EmailAddr alternative           |
| AreaId                 | String   |   no used          |
| VerifyCode                 | String   |   VerifyCode          |

## Change User Password

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
| VerifyCode                 | Integer  |    Verify Code      |
| UserName                 | String  |    user email        |
| NewPassword                 | String  |    user psw       |

## Get device list

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

## Get bound device token

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

## Query device binding status

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
| BindToken                 | String  |    bound device token        |

## Modify device name

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

## Share Device

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
| UserName                 | String  |   share to user name        |
| DeviceId                 | String  |    deviceID       |
| DeviceOwner                 | Integer  |    0-share 1-own       |
| DeviceName                 | String  |    device name       |
| DeviceType                 | Integer  |    deviceID       |
| StreamUser                 | String  |    Can pass ""       |
| StreamPassword                 | String  |    Can pass ""        |
| AreaId                 | String  |    Can pass ""        |
| AppMatchType                 | Integer  |    1 -current platform , 0-old platform     |

## Bind Device

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
| UserName       | String  | share to user name                                           |
| DeviceId       | String  | deviceID                                                     |
| DeviceOwner    | Integer | 0-share 1-own                                                |
| DeviceName     | String  | device name                                                  |
| DeviceType     | Integer | deviceID                                                     |
| StreamUser     | String  | Can pass ""                                                  |
| StreamPassword | String  | Can pass ""                                                  |
| AreaId         | String  | Can pass ""                                                  |
| AppMatchType   | Integer | 1 -current platform , 0-old platform                         |
| LinkDevice     | String  | VPhoto association id                                        |

## Force Delete Device

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

## Obtain a list of shared users

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

## unbindSharedSmartDevice

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
| UserName                 | String  |    shared user name        |
| DeviceId                 | String  |    deviceID       |
| DeviceOwner                 | Integer  |    0  shared device is 0       |

## Obtain device parameters
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
| CMDTypeList                 | List<String>  | "cap1","cap2",······ please refer to `Device Capability Set`       |
| UserName                 | String  |    login user name        |
| DeviceId                 | String  |    deviceID       |
| sessionId                 | String  |    session id       |
| userType                 | Integer  |   the app type ,gsocam is 32           |

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

## set Device param

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




<br> * One CMDType corresponds to one DeviceParam
<br> * `CMDType` please refer to `package com.gos.platform.api.devparam.DevParamCmdType` 
<br> * `DeviceParam` please refer to `package com.gos.platform.api.devparam`  such as `PirSettingParam`、`NightSettingParam`、`MirrorModeSettingParam`



## KeepAlive

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



## Audio Control

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

## Live timestamp display

* Call time zone calibration request, calibration time

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
  
  **When obtaining the device list, the system version of the device can be obtained**
  
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
  
  **Is there a new version of the request,get the firmware version from the server and compare it with the device's version number**
  
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

##### When obtaining the device list, the system version of the device can be obtained

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

**Is there a new version of the request,get the firmware version from the server and compare it with the device's version number**

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

**If updates are needed,if the device is in a sleep state, it needs to be awakened first**

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

## night vision and infrared detection

**The night vision mode of the doorbell can only be turned on, off, and automatic. If automatic, it will automatically turn on the night vision mode after triggering the night vision.
Infrared detection detects heat and alarms**

**Power saving is not associated with the cellular network of the phone**



### devices setting

*When obtaining the device list, parse 'GetDeviceListResponse. Cap'

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

**example**

Does the device support night vision mode

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

**Obtain supported setting types and corresponding display settings from the device's Cap,Obtain the parameters of the corresponding settings and set the parameters under the corresponding settings**



## Verify time zone

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
Time zone types supported by the device：timezoneVerifyType
0:Need to obtain from device capability set
1：Hour time zone   // 8，9，10
2：Half hour time zone  //3.5、4.5、5.5
3：Precise minute time zone (second value)
~~~

## Keep Alive

***is keepAliveSyn return '-20004' ? or '-2004', if code is '-20004' mens the device is offline***

## Delete Account

1. ###### Obtain verification code

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

   ###### *2. delete account*

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

   ## Delete Device

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

   ## Share Device

   **share **

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

   **get share list**

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

   **unshare& delete device**

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

   ## LED Switch

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

   ## Door bell Led

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

   ## Cloud storage

   **Whether the device supports Cloud storage**

   ~~~java
   cap26 > 0;
   cap26 == 1 --> OSS_aliyun;
   cap26 == 2 --> OSS_aws;
   cap26 == 3 --> OSS_liantong;
   ~~~

   **Whether the device has opened Cloud storage and whether Cloud storage has expired**

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

**get cloud storage data**

*1 get all data*

~~~java
Request:
https://cn-css.ulifecam.com/api/cloudstore/cloudstore-service/move-video/time-line?device_id=A99ZC210AQGL6GE&start_time=1688581800&end_time=1688668200&token=21EE0F9BD661488FBB0BA1672772631D&check=novel&username=15599368709&version=1.0 
~~~

request param:

| Field      | Type   | Describe                                           |
| ---------- | ------ | -------------------------------------------------- |
| device_id  | String |                                                    |
| start_time | Long   | Query time period start time                       |
| end_time   | Long   | Query time period end time                         |
| token      | String |                                                    |
| check      | String | "novel"  -- fixed value                            |
| username   | String |                                                    |
| version    | String | No need to transfer, compatible with old platforms |

*2 get data by page*

~~~java
Request:
https://cn-css.ulifecam.com/api/cloudstore/cloudstore-service/move-video/time-line/details/pagination?device_id=A99ZC210AQGL6GE&start_time=1688581800&end_time=1688668200&token=21EE0F9BD661488FBB0BA1672772631D&check=novel&username=15599368709&version=1.0&size=300&page_number=8&sort=des
~~~

request param:

| Field       | Type    | Describe                                           |
| ----------- | ------- | -------------------------------------------------- |
| device_id   | String  |                                                    |
| start_time  | Long    | Query time period start time                       |
| end_time    | Long    | Query time period end time                         |
| token       | String  |                                                    |
| check       | String  | "novel"  -- fixed value                            |
| username    | String  |                                                    |
| version     | String  | No need to transfer, compatible with old platforms |
| size        | Integer | The total number of queries at a time              |
| page_number | Integer | Current query page n                               |
| sort        | String  | sort order    "des","asce"                         |

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

**get a single playback file**

*1 Get Cloud storage information*

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

*2 download file*

~~~java
1.DownloadHandler -- download file;
2.after successful download PlayHandler.class -- begin play file

~~~

## TF Playback

TF playback data uses the TCP protocol,so  recommended to use the method of obtaining data from SDK

~~~kotlin
GosSession.getSession().appGetBSAddress(uuid,"")   //To initialize the TCP server address, this function needs to be called
val result = RemoteDataSource.login(username, psw)
~~~

*We provide a new demo, including the function of obtaining Cloud storage and TF playback*

## Edit Device Wifi

Press the set button after the doorbell twice, and the doorbell will enter distribution mode,use doorbell scanning to generate QR codes

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

## Get video cover

**Receive thumbnail data**

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

**Obtain thumbnail data**

~~~java
 mDevice.getConnection().getRecJpeg(0, buf, buf.length);
~~~

| Field   | type      | Describe                   |
| ------- | --------- | -------------------------- |
| channel | Integer   | 0                          |
| utcTime | Integer[] | Video start timestamp      |
| count   | Integer   | Number of video timestamps |

## Doorbell Volume

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

## Add IPC Device

***1.The first addition method is the same as the doorbell, generating QRCode for IPC scan code***

***2.The second way to add is for the mobile phone to scan the QR code on the IPC machine to obtain the device ID***

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

### Reboot Device

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

### PTZ Control

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

| Field   | type    | Describe          |
| ------- | ------- | ----------------- |
| control |         | Ptz               |
| CMDType | Integer | 4097(Fixed value) |
| channel | Integer | 0                 |

~~~java
public class Ptz {
    public static final int PTZ_KEEP_UP = 38;
    public static final int PTZ_KEEP_DOWN = 39;
    public static final int PTZ_KEEP_LEFT = 40;
    public static final int PTZ_KEEP_RIGHT = 41;
    public static final int PTZ_STOP = 0;
    public static final int PTZ_UP = 1;
    public static final int PTZ_DOWN = 2;
    public static final int PTZ_LEFT = 3;
    public static final int PTZ_RIGHT = 6;
    public static final int PTZ_SELF_CHECK = 252;
    public static final int PTZ_VIRTUAL_CHECK = 253;
    public static final int PTZ_VIRTUALTO = 254;
}

//If the parameter is PTZ_KEEP_UP, it keeps rotating. To stop, send PTZ_STOP
~~~



