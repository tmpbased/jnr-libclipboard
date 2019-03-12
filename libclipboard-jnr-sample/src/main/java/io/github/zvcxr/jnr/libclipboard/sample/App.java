package io.github.zvcxr.jnr.libclipboard.sample;


import io.github.zvcxr.jnr.libclipboard.Clipboard;
import io.github.zvcxr.jnr.libclipboard.Clipboard.ClipboardMode;

public class App {
  public static void main(String[] args) {
    final Clipboard cb = new Clipboard();
    try {
        System.out.println(cb.get(ClipboardMode.LCB_CLIPBOARD));
        cb.set(ClipboardMode.LCB_CLIPBOARD, "test");
    } finally {
        cb.close();
    }
  }
}
