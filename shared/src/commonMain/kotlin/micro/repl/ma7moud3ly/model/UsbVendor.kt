/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

package micro.repl.ma7moud3ly.model

/**
 * USB vendors of boards that run MicroPython and of the USB-to-serial chips
 * boards use, by their USB vendor [id], with the [title] to show.
 */
enum class UsbVendor(val id: Int, val title: String) {
    RaspberryPi(0x2E8A, "Raspberry Pi Pico"),
    Espressif(0x303A, "Espressif"),
    MicroPython(0xF055, "MicroPython"),
    Adafruit(0x239A, "Adafruit"),
    Arduino(0x2341, "Arduino"),
    PidCodes(0x1209, "Open hardware"),
    SiliconLabs(0x10C4, "Silicon Labs"),
    Wch(0x1A86, "WCH"),
    Ftdi(0x0403, "FTDI"),
    Prolific(0x067B, "Prolific"),
    StMicro(0x0483, "STMicroelectronics"),
    Segger(0x1366, "SEGGER");

    companion object {
        /** The vendor with [vendorId], or null for one not listed. */
        fun of(vendorId: Int?): UsbVendor? = entries.firstOrNull { it.id == vendorId }
    }
}
