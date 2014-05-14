LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_ARM_MODE := arm
LOCAL_MODULE    := MemoryRWJni
LOCAL_SRC_FILES := app_sand_throughputtest_MemoryRWJni.cpp.arm
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)