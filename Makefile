all:
	ant debug
install:
	adb install -r bin/GyacoUploader-debug.apk
uninstall:
	adb uninstall com.pitecan.gyacouploader
debug:
	adb logcat | grep Gyaco
clean:
	/bin/rm -r -f bin/classes
