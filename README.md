# checks sites using a background thread

## Tools

if not opening say this project, and starting a new one for the first time in intellij:
https://www.jetbrains.com/help/idea/create-your-first-android-application.html#fae27f2e

otherwise:

- IntelliJ > Settings > Languages & Frameworks > Android SDK > Location ... edit
- then install it to /home/ant/Android/Sdk and just keep clicking next


    sudo apt install google-android-platform-tools-installer


## connecting device

- https://android.stackexchange.com/a/144967/378169
- https://stackoverflow.com/questions/31638582/android-adb-devices-unauthorized
- connect the device using usb (7 times on aboutPhone>buildNumber), then enable debugging in developer>settings
  - then settings > system > developer
  - CHANGE connection to allow file transfer! (in normal usb connection dialog)
  - enable usb debugging

it may help to do this:

    sudo usermod -aG plugdev $LOGNAME
    sudo apt-get install android-sdk-platform-tools-common

    lsusb

outputs other stuff too:

    Bus 002 Device 014: ID 12d1:107e Huawei Technologies Co., Ltd. P10 smartphone
    Bus 001 Device 072: ID 22b8:2e76 Motorola PCS moto g54 5G

then:

    sudo vi /etc/udev/rules.d/51-android.rules

should contain this, where vendor and product ids come from above:

    SUBSYSTEM=="usb", ATTR{idVendor}=="22b8", ATTR{idProduct}=="2e76", MODE="0666", GROUP="plugdev", SYMLINK+="android%n"

    vi ~/.android/adb_usb.ini

should contain the following, where the code is the vendor code from `lsusb`:

    0x22b8

    systemctl restart udev
    systemctl status udev

then:

    rm /home/ant/.android/adbkey

reconnect usb device.

ensure connected with file transfer enabled.

enable usb debugging in phone's dev settings

ensure connected with file transfer enabled.

enable usb debugging in phone's dev settings

    cd /home/ant/Android/Sdk/platform-tools/
    adb kill-server && adb start-server

    adb devices

should output:

    List of devices attached
    ZY22HMJ2J6    device

if it says `unauthorized` instead of `device`, then do the following and on the phone, enable debugging. check the box to always allow it for this laptop.

    adb shell

## Deployment

Use IntelliJ, select the phone, and `app` and then click run. 
That should install the latest version of the app on the phone.

## TODO

- use jvm 11 in AndroideManifest.xml
- check multiple sites, rather than just hard coded one
- add regexps to search for
- persistent log messages
- remove email button
- make settings button do something?
- get rid of second fragment