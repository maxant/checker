# checks sites using a background thread

## connecting device

- https://android.stackexchange.com/a/144967/378169
- https://stackoverflow.com/questions/31638582/android-adb-devices-unauthorized
- connect the device using usb
  - then settings > system > developer
  - CHANGE connection to allow file transfer!
  - enable usb debugging

it may help to do this:

    sudo usermod -aG plugdev $LOGNAME
    sudo apt-get install android-sdk-platform-tools-common

    lsusb

outputs other stuff too:

    Bus 002 Device 014: ID 12d1:107e Huawei Technologies Co., Ltd. P10 smartphone

then:

    sudo vi /etc/udev/rules.d/51-android.rules

should contain this, where vendor and product ids come from above:

    SUBSYSTEM=="usb", ATTR{idVendor}=="12d1", ATTR{idProduct}=="107e", MODE="0666", GROUP="plugdev", SYMLINK+="android%n"

    vi ~/.android/adb_usb.ini

should contain the following, where the code is the vendor code from `lsusb`:

    0x12d1

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
    0WEDS19458102153	device

if it says `unauthorized` instead of `device`, then do the following and on the phone, enable debugging. check the box to always allow it for this laptop.

    adb shell

## TODO

- use jvm 11 in AndroideManifest.xml
- make app navigate to web, where we can e.g. install new certs
- check multiple sites
- add regexps to search for
- check a new page which tells us when stuff is going to expire
- persistent log messages
- remove email button
- make settings button do something?
- get rid of second fragment