UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Linux)
    OS_NAME := linux
    NATIVE_TARGET := libcom_udeyrishi_encryptedfileserver_common_tea_TEANative.so
endif
ifeq ($(UNAME_S),Darwin)
    OS_NAME := darwin
    NATIVE_TARGET := libcom_udeyrishi_encryptedfileserver_common_tea_TEANative.dylib
endif

OUT_DIR := bin
SRC_DIR := src
NATIVE_PATH := com/udeyrishi/encryptedfileserver/common/tea
NATIVE_TARGET := $(OUT_DIR)/$(NATIVE_PATH)/$(NATIVE_TARGET)

# export LD_LIBRARY_PATH := $(LD_LIBRARY_PATH):.

MKDIR_P := mkdir -p
RM_DIR := $(RM) -r

CC := gcc
JAVA_COMPILER := javac
JAVA_FLAGS := -d $(OUT_DIR) -cp $(SRC_DIR)

JAVA_MAIN_SERVER := $(SRC_DIR)/com/udeyrishi/encryptedfileserver/server/Main.java
JAVA_MAIN_CLIENT := $(SRC_DIR)/com/udeyrishi/encryptedfileserver/client/Main.java
NATIVE_SRC := $(SRC_DIR)/$(NATIVE_PATH)/com_udeyrishi_encryptedfileserver_common_tea_TEANative.c
NATIVE_FLAGS := -std=c99 -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/$(OS_NAME) -shared -fpic

.PHONY: directories clean

default: all

all: directories server client

directories:
	$(MKDIR_P) $(OUT_DIR)
	$(MKDIR_P) $(OUT_DIR)/$(NATIVE_PATH)

server: directories $(JAVA_MAIN_SERVER) native
	$(JAVA_COMPILER) $(JAVA_FLAGS) $(JAVA_MAIN_SERVER)

client: directories $(JAVA_MAIN_CLIENT) native
	$(JAVA_COMPILER) $(JAVA_FLAGS) $(JAVA_MAIN_CLIENT)

native: directories $(NATIVE_SRC)
	$(CC) $(NATIVE_FLAGS) -o $(NATIVE_TARGET) $(NATIVE_SRC)

clean:
	$(RM_DIR) $(OUT_DIR)

rebuild: clean all