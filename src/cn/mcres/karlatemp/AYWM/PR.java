/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.mcres.karlatemp.AYWM;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.function.Consumer;

public class PR extends PrintWriter {

    final PrintStream w;

    Consumer<PrintStream> prefix;

    private PrintStream pre() {
        prefix.accept(w);
        return w;
    }

    public PR(PrintStream out) {
        super(new EW());
        this.w = out;
    }

    public synchronized void pall(String s) {
        if (s == null || s.isEmpty()) {
            return;
        }
        synchronized (w) {
            if (s.indexOf('\n') != -1) {
                int cut = 0;
                int f = 0;
                int ed = s.length();
                for (; f < ed; f++) {
                    if (s.charAt(f) == '\n') {
                        pre().append(s, cut, f).println();
                        cut = f + 1;
                    }
                }
                if (cut < ed) {
                    pre().append(s, cut, ed).println();
                }
                if (s.charAt(ed - 1) == '\n') {
                    pre().println();
                }
            } else {
                println(s);
            }
        }
    }

    @Override
    public void println(Object x) {
        println(String.valueOf(x));
    }

    @Override
    public synchronized void println(String s) {
        synchronized (w) {
            pre().println(s);
        }
    }

    private static final class EW extends Writer {

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

}
