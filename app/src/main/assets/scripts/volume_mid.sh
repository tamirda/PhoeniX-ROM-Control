#!/system/bin/sh
export PATH=/system/bin:$PATH

mount -o rw,remount /system

cp -p /system/td/files/sound/mild/mixer_paths.xml /system/etc/mixer_paths.xml
