/*
 * Created by Mahmoud Aly - engma7moud3ly@gmail.com
 * Project Micro REPL - https://github.com/Ma7moud3ly/micro-repl
 * Copyright (c) 2023 . MIT license.
 *
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package micro.repl.ma7moud3ly.platform

import kotlin.js.Promise

/*
 * Bindings to the browser's Web Serial API, used by WebSerialPortManager.
 * A port, reader, writer and byte buffer are passed around as opaque JsAny.
 */

/** Whether this browser has Web Serial: Chrome, Edge and Opera on desktop. */
internal fun isSerialSupported(): Boolean = js("'serial' in navigator")

/** Whether the page is handling a click, the only time the port picker may open. */
internal fun hasUserActivation(): Boolean =
    js("!!(navigator.userActivation && navigator.userActivation.isActive)")

/** The ports this site was granted before. */
internal fun serialGetPorts(): Promise<JsArray<JsAny>> = js("navigator.serial.getPorts()")

/** Shows the browser's port picker. Rejects when the user dismisses it. */
internal fun serialRequestPort(): Promise<JsAny> = js("navigator.serial.requestPort()")

/** The port's USB vendor id, or -1 for a port that is not USB. */
internal fun portVendorId(port: JsAny): Int = js("port.getInfo().usbVendorId ?? -1")

/** The port's USB product id, or -1 for a port that is not USB. */
internal fun portProductId(port: JsAny): Int = js("port.getInfo().usbProductId ?? -1")

internal fun portOpen(port: JsAny, baudRate: Int): Promise<JsAny?> =
    js("port.open({ baudRate: baudRate })")

internal fun portSetDtr(port: JsAny): Promise<JsAny?> =
    js("port.setSignals({ dataTerminalReady: true })")

internal fun portReader(port: JsAny): JsAny = js("port.readable.getReader()")

internal fun portWriter(port: JsAny): JsAny = js("port.writable.getWriter()")

internal fun writerWrite(writer: JsAny, data: JsAny): Promise<JsAny?> = js("writer.write(data)")

/**
 * Reads [reader] until it ends, handing each chunk to [onData]. [onEnd] runs once
 * with an empty reason when the stream closed, or the error that stopped it.
 */
internal fun readLoop(
    reader: JsAny,
    onData: (JsAny) -> Unit,
    onEnd: (String) -> Unit
): Unit = js(
    """
    void (async () => {
        for (;;) {
            const { value, done } = await reader.read();
            if (done) return;
            onData(value);
        }
    })().then(() => onEnd(''), (e) => onEnd(String(e)))
    """
)

/** Cancels [reader], frees [writer] and closes [port], ignoring each step's errors. */
internal fun portClose(port: JsAny, reader: JsAny?, writer: JsAny?): Unit = js(
    """
    void (async () => {
        try { if (reader) { await reader.cancel(); reader.releaseLock(); } } catch (e) {}
        try { if (writer) writer.releaseLock(); } catch (e) {}
        try { await port.close(); } catch (e) {}
    })()
    """
)

internal fun uint8Length(array: JsAny): Int = js("array.length")

/** The byte at [index], from 0 to 255. */
internal fun uint8Get(array: JsAny, index: Int): Int = js("array[index]")

internal fun uint8New(size: Int): JsAny = js("new Uint8Array(size)")

internal fun uint8Set(array: JsAny, index: Int, value: Int): Unit = js("array[index] = value")

/** Copies a JS Uint8Array into a [ByteArray]. */
internal fun JsAny.toByteArray(): ByteArray =
    ByteArray(uint8Length(this)) { uint8Get(this, it).toByte() }

/** Copies bytes into a new JS Uint8Array. */
internal fun ByteArray.toUint8Array(): JsAny {
    val array = uint8New(size)
    forEachIndexed { index, byte -> uint8Set(array, index, byte.toInt() and 0xFF) }
    return array
}

/** The array's items as a list. */
internal fun <T : JsAny> JsArray<T>.toList(): List<T> =
    (0 until length).mapNotNull { get(it) }
