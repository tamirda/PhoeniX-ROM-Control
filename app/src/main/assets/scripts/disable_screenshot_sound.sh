#!/system/bin/sh
export PATH=/system/bin:$PATH

mount -o rw,remount /system

cp -p /system/td/files/audio/silent.ogg /system/media/audio/ui/camera_click.ogg
