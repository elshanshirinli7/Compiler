import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //Read from file
        String result = "";
        try (InputStream in = new FileInputStream("/home/ghost/IdeaProjects/Compiler/src/program.txt");
             InputStreamReader r = new InputStreamReader(in);
             BufferedReader reader = new BufferedReader(r);) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }
        }

        String code = result;
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        for(Token token: tokens){
            System.out.println(token);
        }
        Parser parser = new Parser(tokens);

        try {
            parser.parse();
            System.out.println("Parsing completed successfully!");
        } catch (RuntimeException e) {
            System.out.println("Parsing failed:" + e.getMessage());
        }

    }
}

