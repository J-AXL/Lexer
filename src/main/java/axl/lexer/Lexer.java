package axl.lexer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import axl.logger.Logger;
/**
 * User:Quark
 * Date: 24/05/2023
 * This class tokenises the input code
 */

public class Lexer {
    public static void main(String[] args) {
        try {
            String code = Files.readString(Path.of("src/main/resources/examples/class.txt"), Charset.defaultCharset());

            ArrayList<String> tokens = splitJavaCode(code);

            // need method to printing { ENDFILE}

            Logger.logger_install(null);
            //Logger.print_logo_full();
            Logger.set_title("LEXER");
            //Logger.print_success("LEXER OUTPUT:");
            Logger.print_warning("{");
            for (String token : tokens) {
                Logger.print_warning(token);
            }
            Logger.print_warning("ENDFILE");
            Logger.print_warning("}");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> splitJavaCode(String code) {
        String regex = "([a-zA-Z_][a-zA-Z0-9_]*)|\\b\\d+\\b|[\\[\\]{}()+*/=.;#-]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(code);

        ArrayList<String> tokens = new ArrayList<>();

        while (matcher.find()) {
            String match = matcher.group();
            tokens.add(tokeniser(match) + ","); // need to do something with the comma because in tokenizer
                                                // i`m also added comma (dunno why it`s not prints)
        }
        return tokens;
    }

    private static String tokeniser(String token) {
        //many symbols are not registered and will appear as UNKNOWN
        if (token.matches("\\b(public|private|protected|class|void|int|double|float|boolean|char)\\b")) {
            return "RWORD: " + token;
        } else if (token.matches("\\d+")) {
            return "INT: "+ token;
        } else if (token.matches("\\b\\w+\\b")) {
            return "WORD: \"" + token + "\"";
        } else if (token.equals(".")) {
            return "DOT";
        } else if (token.equals(";")) {
            return "SEMI";
        } else if (token.equals("(")) {
            return "LPAR";
        } else if (token.equals(")")) {
            return "RPAR";
        } else if (token.equals("{")) {
            return "LBRACE";
        } else if (token.equals("}")) {
            return "RBRACE";
        } else if (token.equals("#")) {
            return "WORD: \"" + "this" + "\",\nDOT";
        } else if (token.equals("=")) {
            return "EQUAL";
        } else if (token.equals("+")) {
            return "PLUS";
        } else {
            return "UNKNOWN";
        }
    }
}