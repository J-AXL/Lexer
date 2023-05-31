package axl.lexer;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Stack;

public final class Logger {

    public static final String logo = """
                _   __  __   __
               /_\\  \\ \\/ /  / /
              //_\\\\  \\  /  / /
             /  _  \\ /  \\ / /___
             \\_/ \\_//_/\\_\\\\____/
            """;

    public static final String logo_full = """
                _                 _         _    _
               /_\\  __  __  ___  | |  ___  | |_ | |
              //_\\\\ \\ \\/ / / _ \\ | | / _ \\ | __|| |
             /  _  \\ >  < | (_) || || (_) || |_ | |
             \\_/ \\_//_/\\_\\ \\___/ |_| \\___/  \\__||_|
            """;

    public static final String logo_img = """
                 ^.             .^
             ^.   7:           :7   .^
             :!.  7!           !7  .!:
            . ~7:.?P55PPPPPPP55P?.:7~ .
            ^~:^?GBBB BBBBBBB BBBG?^:~^
             .^~YBBB  BBBBBBB  BBBY~^.
                .YBBBBBBBBBBBBBBBY.
                  !GGGGBBBBBGGGG!
                 !PP~..:^^^:..~PP!
                :BP.           .PB:
               :Y5P~           ~P5Y:
           """;

    private static final Stack<Integer> warnings_stack = new Stack<>();
    private static final Stack<Integer> errors_stack = new Stack<>();

    public static final Color BACKGROUND   = Color.BLACK;
    public static final Color FOREGROUND   = Color.WHITE;
    public static final Color WARNING      = Color.YELLOW;
    public static final Color SUCCESS      = Color.GREEN;
    public static final Color ERROR        = Color.RED;

    public static final String SEPARATOR   = " - ";


    private static boolean enable = true;

    private static String save_file = null;
    private static String title = "LOG";

    private static FileOutputStream stream = null;

    public static String get_absolute_path() {
        try {
            return new File(save_file).getAbsolutePath();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static String get_file_name() {
        try {
            return new File(save_file).getName();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private static int warnings = 0;
    private static int errors = 0;

    public static void drop() {
        warnings = 0;
        errors = 0;
    }

    public static int getWarnings() {
        return warnings;
    }

    public static int getErrors() {
        return errors;
    }

    public static void push() {
        warnings_stack.push(warnings);
        errors_stack.push(errors);
    }

    public static int[] pop() {
        return new int[] {
                warnings_stack.pop(),
                errors_stack.pop()
        };
    }

    public static int[] peek() {
        return new int[] {
                warnings_stack.peek(),
                errors_stack.peek()
        };
    }

    public static int get_size() {
        return warnings_stack.size() == errors_stack.size() ? warnings_stack.size() : -1;
    }

    public static void logger_install(String save_file_) {
        AnsiConsole.systemInstall();
        Logger.save_file = save_file_;
        Logger.enable = true;

        try {
            if(save_file_ != null)
                stream = new FileOutputStream(save_file_);
        } catch (FileNotFoundException e) {
            print_error(String.format("Файл \"%s\" недоступен для лога.", save_file_), "LOG");
        }
    }

    public static void logger_uninstall() {
        Logger.enable = false;

        if(stream != null) {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }

        AnsiConsole.systemUninstall();
    }

    public static void set_title(String title_) {
        Logger.title = title_;
    }


    private static String get_time() {
        LocalDateTime time = LocalDateTime.now();
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();

        return String.format(" %02d:%02d:%02d", hour, minute, second);
    }

    private static void print(String msg, String title, Color color) {
        String time = get_time();

        Ansi ansi = Ansi.ansi();

        ansi.bg(BACKGROUND);
        ansi.fg(color);
        ansi.a(time);
        ansi.a(' ');
        ansi.a('[').a(title).a(']');
        ansi.fg(FOREGROUND);
        ansi.a(SEPARATOR);
        ansi.a(msg);

        System.out.println(ansi);
    }

    private static void newline_file() {
        if(stream == null) return;

        try {
            stream.write(0xA);
        } catch (IOException ignored) {
        }
    }

    private static void write(String msg, String title) {
        if(stream == null) return;

        String time = get_time();

        StringBuilder builder = new StringBuilder();

        builder.append(time);
        builder.append(' ');
        builder.append('[').append(title).append(']');
        builder.append(SEPARATOR);
        builder.append(msg);
        builder.append('\n');

        try {
            stream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
    }


    public static void print(String msg) {
        if(!enable) return;
        System.out.print(msg);

        if(stream == null) return;
        try {
            stream.write(msg.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }
    }

    public static void print_success(String msg) {
        if(!enable) return;
        print(msg, title, SUCCESS);
        write(msg, title);
    }

    public static void print_success(String msg, String title) {
        if(!enable) return;
        print(msg, title, SUCCESS);
        write(msg, title);
    }

    public static void print_warning(String msg) {
        warnings++;
        if(!enable) return;
        print(msg, title, WARNING);
        write(msg, title);
    }

    public static void print_warning(String msg, String title) {
        warnings++;
        if(!enable) return;
        print(msg, title, WARNING);
        write(msg, title);
    }

    public static void print_error(String msg) {
        errors++;
        if(!enable) return;
        print(msg, title, ERROR);
        write(msg, title);
    }

    public static void print_error(String msg, String title) {
        errors++;
        if(!enable) return;
        print(msg, title, ERROR);
        write(msg, title);
    }

    public static void print_default(String msg) {
        if(!enable) return;
        print(msg, title, FOREGROUND);
        write(msg, title);
    }

    public static void print_default(String msg, String title) {
        if(!enable) return;
        print(msg, title, FOREGROUND);
        write(msg, title);
    }

    public static void newline() {
        if(!enable) return;
        System.out.println();
        newline_file();
    }

    public static void report() {
        if(!enable) return;
        String msg = String.format(" --------- report ---------\n         warnings: %d\n         errors: %d\n --------------------------", warnings, errors);
        System.out.print('\r');
        System.out.println(msg);

        if(stream == null) return;
        try {
            stream.write(msg.getBytes(StandardCharsets.UTF_8));
            stream.write(0xA);
        } catch (IOException ignored) {
        }
    }


    public static void print_logo() {
        if(!enable) return;
        System.out.println(logo);

        if(stream == null) return;
        try {
            stream.write(logo.getBytes(StandardCharsets.UTF_8));
            stream.write(0xA);
        } catch (IOException ignored) {
        }
    }

    public static void print_logo_full() {
        if(!enable) return;
        System.out.println(logo_full);

        if(stream == null) return;
        try {
            stream.write(logo_full.getBytes(StandardCharsets.UTF_8));
            stream.write(0xA);
        } catch (IOException ignored) {
        }
    }

    public static void print_logo_img() {
        if(!enable) return;
        System.out.println(logo_img);

        if(stream == null) return;
        try {
            stream.write(logo_img.getBytes(StandardCharsets.UTF_8));
            stream.write(0xA);
        } catch (IOException ignored) {
        }
    }
}
