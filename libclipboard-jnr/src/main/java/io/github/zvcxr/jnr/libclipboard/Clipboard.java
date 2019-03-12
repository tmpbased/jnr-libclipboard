package io.github.zvcxr.jnr.libclipboard;

import java.io.Closeable;
import java.nio.file.Paths;

import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Struct;
import jnr.ffi.annotations.Out;
import jnr.ffi.types.size_t;
import jnr.ffi.util.EnumMapper;

public class Clipboard implements Closeable {
    public static enum ClipboardMode implements EnumMapper.IntegerEnum {
        /** The primary (global) clipboard **/
        LCB_CLIPBOARD(0),
        /** The (global) mouse selection clipboard **/
        LCB_PRIMARY(1),
        /** The (global) mouse selection clipboard; for backwards compatibility **/
        LCB_SELECTION(1),
        /** The largely unused (global) secondary selection clipboard **/
        LCB_SECONDARY(2),
        /** Sentinel value for end of clipboard modes **/
        LCB_MODE_END(3);

        private final int value;

        private ClipboardMode(int value) {
            this.value = value;
        }

        @Override
        public int intValue() {
            return value;
        }
    }

    private static final class Length extends Struct {
        @SuppressWarnings("unused")
        public final Signed32 a_number = new Signed32();

        public Length(final jnr.ffi.Runtime runtime) {
            super(runtime);
        }
    }

    public interface LibClipboard {
        Pointer clipboard_new(Pointer cb_opts);

        String clipboard_text_ex(Pointer cb, @Out Length length, ClipboardMode mode);

        boolean clipboard_set_text_ex(Pointer cb, CharSequence src, @size_t int length, ClipboardMode mode);

        void clipboard_clear(Pointer cb, ClipboardMode mode);

        void clipboard_free(Pointer cb);
    }

    private static String getWorkingDirectory() {
        return Paths.get(".").toAbsolutePath().toString();
    }

    private final LibClipboard lib;
    private Pointer cb;

    public Clipboard() {
        this.lib = LibraryLoader.create(LibClipboard.class).search(getWorkingDirectory()).load("clipboard");
        this.cb = this.lib.clipboard_new(null);
    }

    public String get(final ClipboardMode mode) {
        return this.lib.clipboard_text_ex(this.cb, null, mode);
    }

    public boolean set(final ClipboardMode mode, final String text) {
        return this.lib.clipboard_set_text_ex(this.cb, text, text.length(), mode);
    }

    public void clear(final ClipboardMode mode) {
        this.lib.clipboard_clear(this.cb, mode);
    }

    @Override
    public void close() {
        this.lib.clipboard_free(this.cb);
        this.cb = null;
    }
}
