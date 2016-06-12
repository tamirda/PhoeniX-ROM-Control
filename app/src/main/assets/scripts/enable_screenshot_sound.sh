#!/system/bin/sh
export PATH=/system/bin:$PATH

mount -o rw,remount /system

cp -p /system/td/files/audio/camera_click.ogg /system/media/audio/ui/camera_click.ogg
