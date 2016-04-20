package com.lyf.jason.androidemulatordetect;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EmulatorDetector {

    private Context context;
    private String[] emulator_phone_numbers = {"15555215554", "15555215556", "15555215558", "15555215560", "15555215562", "15555215564", "15555215566", "15555215568", "15555215570", "15555215572", "15555215574", "15555215576", "15555215578", "15555215580", "15555215582", "15555215584"};
    private String[] emulator_files = {"/system/lib/libc_malloc_debug_qemu.so", "/sys/qemu_trace", "/system/bin/qemu-props", "/dev/qemu_pipe"};

    public EmulatorDetector(Context context) {
        this.context = context;
    }

    public boolean isEmulator() {
        return (hasEmulatorFile() || hasEmulatorIMEI() || hasEmulatorNumbers() || hasEmulatorProperty());
    }

    private boolean hasEmulatorNumbers() {

        TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

        String phone_number = manager.getLine1Number();

        for (String number : emulator_phone_numbers) {
            if (number.equalsIgnoreCase(phone_number)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasEmulatorIMEI() {

        TelephonyManager manager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

        String IMEI = manager.getDeviceId();

        if ("000000000000000".equalsIgnoreCase(IMEI)) {
            return true;
        }

        return false;
    }

    private boolean hasEmulatorProperty() {

        String board = Build.BOARD;
        String brand = Build.BRAND;
        String device = Build.DEVICE;
        String hardware = Build.HARDWARE;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String cpu_abi = Build.CPU_ABI;

        if (board.equalsIgnoreCase("unknown") ||
                brand.equalsIgnoreCase("generic") ||
                device.equalsIgnoreCase("generic") ||
                hardware.equalsIgnoreCase("sdk") ||
                model.equalsIgnoreCase("sdk") ||
                model.equalsIgnoreCase("google_sdk") ||
                product.equalsIgnoreCase("goldfish") ||
                cpu_abi.contains("x86") ||
                cpu_abi.contains("i386")) {
            return true;
        }

        try {
            Method system_property_get = Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class});
            system_property_get.setAccessible(true);
            String value = (String) system_property_get.invoke(null, "ro.kernel.qemu");
            return value.equalsIgnoreCase("1");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean hasEmulatorFile() {

        for (String filename : emulator_files) {
            if (new File(filename).exists()) {
                return true;
            }
        }

        return false;
    }
}
