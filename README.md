Copyright (c) 2014 FAN XIAODONG

It is an android application used to test the throughput of memory. It apply a speed test when read and write data between the memory and the cpu register. The buffer size can be set as a parameter to test it in the different conditions. 
The memory throughput test function is performed with C code. So the android-ndk is required to build the jni interface. And the android-support-v7-appcompat.jar is required, configure the project refer to https://developer.android.com/tools/support-library/setup.html.