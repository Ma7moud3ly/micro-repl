<div align="center">

# `>>>` Micro REPL

### A MicroPython IDE for Android

Plug a microcontroller into your phone over USB and get a real REPL,
a file manager, and a code editor. No laptop required.

[![F-Droid](https://img.shields.io/f-droid/v/micro.repl.ma7moud3ly?logo=fdroid&logoColor=white&label=F-Droid&color=1976D2)](https://f-droid.org/packages/micro.repl.ma7moud3ly/)
[![Android](https://img.shields.io/badge/Android-6.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![MicroPython](https://img.shields.io/badge/MicroPython-ready-2B2728?logo=micropython&logoColor=white)](https://micropython.org)
[![License](https://img.shields.io/github/license/Ma7moud3ly/micro-repl?color=blue)](LICENSE.txt)

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="60">](https://f-droid.org/packages/micro.repl.ma7moud3ly/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="60">](https://play.google.com/store/apps/details?id=micro.repl.ma7moud3ly)

Or check the [Releases](https://github.com/Ma7moud3ly/micro-repl/releases/latest).

<img src="images/feature_graphic.png" alt="Micro REPL" width="100%" />

</div>

## What it does

**Terminal**: a live MicroPython prompt. Run code, reset the board, or stop a
script that's stuck in a loop.

**Files Explorer**: browse the board's storage. Create, rename, delete and share
files, or import them from your phone.

**Code Editor**: syntax highlighting, autocomplete and undo/redo. Save to the
board or your phone.

**Scripts**: keep your own scripts on the phone, and edit them with no board
connected.

**Themes**: 20+ themes that colour the whole app, not just the code.

## What you need

- Android 6.0 or newer, with [USB On-The-Go](https://en.wikipedia.org/wiki/USB_On-The-Go) support
- A board flashed with [MicroPython](https://micropython.org/download/)

Tested on [Raspberry Pi Pico](https://micropython.org/download/RPI_PICO) and
[ESP32](https://micropython.org/download/ESP32_GENERIC).

> CircuitPython boards are not supported yet.

## Screenshots

### Pick a theme, the whole app follows

<div align="center">
<img src="images/screens/home_light.jpg" width="230" />
&nbsp;
<img src="images/screens/home_dark.jpg" width="230" />
&nbsp;
<img src="images/screens/home_monokai.jpg" width="230" />
&nbsp;
<img src="images/screens/home_crimson.jpg" width="230" />
</div>

### Terminal

<div align="center">
<img src="images/screens/terminal_light.jpg" width="250" />
&nbsp;&nbsp;
<img src="images/screens/terminal_dark.jpg" width="250" />
</div>

### Files Explorer

<div align="center">
<img src="images/screens/explorer_light.jpg" width="250" />
&nbsp;&nbsp;
<img src="images/screens/explorer_dark.jpg" width="250" />
</div>

### Code Editor

<div align="center">
<img src="images/screens/editor1_light.jpg" width="250" />
&nbsp;&nbsp;
<img src="images/screens/editor2_dark.jpg" width="250" />
</div>

Run a script and see its output right away:

<div align="center">
<img src="images/screens/editor1_output_light.jpg" width="250" />
&nbsp;&nbsp;
<img src="images/screens/editor2_output_dark.jpg" width="250" />
</div>

## Built with

- [nemo-editor](https://github.com/Ma7moud3ly/nemo-editor): the Compose code editor
- [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android): serial communication

## References

- [MicroPython REPL](https://docs.micropython.org/en/latest/esp8266/tutorial/repl.html)
- [MicroPython machine module](https://docs.micropython.org/en/latest/library/machine.html)

