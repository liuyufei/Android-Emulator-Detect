#include "emulator_detect.h"
#include <unistd.h>
#include <sys/mman.h>
#include <android/log.h>

JNIEXPORT jint JNICALL Java_com_lyf_jason_detector_EmulatorDetector_cacheExecute (JNIEnv *env, jobject obj) {

    void (*call)(void);

    char code[] =
            "\xF0\x41\x2D\xE9\x00\x60\xA0\xE3\x00\x70\xA0\xE3\x0F\x80\xA0\xE1"
            "\x00\x40\xA0\xE3\x01\x70\x87\xE2\x00\x50\x98\xE5\x01\x40\x84\xE2"
            "\x0F\x80\xA0\xE1\x0C\x80\x48\xE2\x00\x50\x88\xE5\x01\x60\x86\xE2"
            "\x0A\x00\x54\xE3\x02\x00\x00\xAA\x0A\x00\x57\xE3\x00\x00\x00\xAA"
            "\xF5\xFF\xFF\xEA\x04\x00\xA0\xE1\xF0\x81\xBD\xE8";

    void *exec = mmap(NULL, (size_t) 4096, PROT_WRITE | PROT_EXEC, MAP_SHARED | MAP_ANONYMOUS, -1,
                      (off_t) 0);

    memcpy(exec, code, sizeof(code) + 1);

    call = exec;

    __android_log_print(ANDROID_LOG_INFO, "JNIMsg11", "msg: %p\n", exec);

    call();

    int a;

    __asm __volatile (
    "mov %0,r0\n"
    :"=r"(a)
    :
    :
    );

    munmap(exec, (size_t) 4096);

    if (a == 10)
        return 0;
    else
        return 1;
}
