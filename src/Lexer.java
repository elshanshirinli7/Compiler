import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String sourceCode;
    private int currentPosition;

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.currentPosition = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (currentPosition < sourceCode.length()) {
            char currentChar = sourceCode.charAt(currentPosition);

            if (Character.isWhitespace(currentChar)) {
                // Skip whitespace characters
                currentPosition++;
            } else if (Character.isLetter(currentChar)) {
                tokens.add(scanIdentifierOrKeyword());
            } else if (Character.isDigit(currentChar)) {
                tokens.add(scanNumber());
            } else {
                Token operatorOrPunctuation = scanOperatorOrPunctuation();
                if (operatorOrPunctuation != null) {
                    tokens.add(operatorOrPunctuation);
                } else {
                    // Invalid token or unrecognized character
                    currentPosition++;
                }
            }
        }

        // Add the EOF token
        tokens.add(new Token(TokenType.EOF, "EOF"));
        return tokens;
    }

    private Token scanIdentifierOrKeyword() {
        StringBuilder identifier = new StringBuilder();

        while (currentPosition < sourceCode.length() && Character.isLetterOrDigit(sourceCode.charAt(currentPosition))) {
            identifier.append(sourceCode.charAt(currentPosition));
            currentPosition++;
        }

        String identifierString = identifier.toString();

        // Check if the identifier is a keyword or a variable declaration
        switch (identifierString) {
            case "var":
                return new Token(TokenType.VAR, identifierString);
            case "int":
                return new Token(TokenType.INT, identifierString);
            case "float":
                return new Token(TokenType.FLOAT, identifierString);
            case "if":
                return new Token(TokenType.IF, identifierString);
            case "else":
                return new Token(TokenType.ELSE, identifierString);
            case "for":
                return new Token(TokenType.FOR, identifierString);
            case "func":
                return new Token(TokenType.FUNC, identifierString);
            case "return":
                return new Token(TokenType.RETURN, identifierString);
            case "array":
                return new Token(TokenType.ARRAY, identifierString);
            case "true":
                return new Token(TokenType.TRUE, identifierString);
            case "false":
                return new Token(TokenType.FALSE, identifierString);
            case "new": // Handle NEW keyword
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == ' ') {
                    currentPosition++; // Consume the space character

                    // Check if the next token is '=' to indicate a variable declaration
                    if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '=') {
                        currentPosition++; // Consume the '=' character
                        return new Token(TokenType.NEW, identifierString);
                    }
                }

                return new Token(TokenType.IDENTIFIER, identifierString);
            default:
                // Check if it is a string literal
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '"') {
                    currentPosition++; // Consume the opening quote character

                    StringBuilder stringLiteral = new StringBuilder();
                    while (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) != '"') {
                        stringLiteral.append(sourceCode.charAt(currentPosition));
                        currentPosition++;
                    }

                    if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '"') {
                        currentPosition++; // Consume the closing quote character
                        return new Token(TokenType.STRING_LITERAL, stringLiteral.toString());
                    } else {
                        // Unterminated string literal
                        return new Token(TokenType.ERROR, identifierString);
                    }
                }

                // Check if it is a valid identifier or a variable declaration
                if (identifier.length() > 0) {
                    return new Token(TokenType.IDENTIFIER, identifierString);
                }

                // Invalid token or unrecognized character
                currentPosition++;
                return new Token(TokenType.ERROR, identifierString);
        }
    }



    private Token scanNumber() {
        StringBuilder number = new StringBuilder();

        while (currentPosition < sourceCode.length() && Character.isDigit(sourceCode.charAt(currentPosition))) {
            number.append(sourceCode.charAt(currentPosition));
            currentPosition++;
        }
        if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '.') {
            number.append('.');
            currentPosition++;

            while (currentPosition < sourceCode.length() && Character.isDigit(sourceCode.charAt(currentPosition))) {
                number.append(sourceCode.charAt(currentPosition));
                currentPosition++;
            }

            return new Token(TokenType.FLOAT_LITERAL, number.toString());
        } else {
            return new Token(TokenType.INTEGER_LITERAL, number.toString());
        }
    }

    private Token scanOperatorOrPunctuation() {
        char currentChar = sourceCode.charAt(currentPosition);

        switch (currentChar) {
            case '+' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '+') {
                    currentPosition++;
                    return new Token(TokenType.INCREMENT, "++");
                } else {
                    return new Token(TokenType.PLUS, "+");
                }
            }
            case '-' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '-') {
                    currentPosition++;
                    return new Token(TokenType.DECREMENT, "--");
                } else {
                    return new Token(TokenType.MINUS, "-");
                }
            }
            case '*' -> {
                currentPosition++;
                return new Token(TokenType.MULTIPLY, "*");
            }
            case '/' -> {
                currentPosition++;
                return new Token(TokenType.DIVIDE, "/");
            }
            case '=' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '=') {
                    currentPosition++;
                    return new Token(TokenType.EQUAL, "==");
                } else {
                    return new Token(TokenType.ASSIGN, "=");
                }
            }
            case ';' -> {
                currentPosition++;
                return new Token(TokenType.SEMICOLON, ";");
            }
            case ',' -> {
                currentPosition++;
                return new Token(TokenType.COMMA, ",");
            }
            case '(' -> {
                currentPosition++;
                return new Token(TokenType.LEFT_PAREN, "(");
            }
            case ')' -> {
                currentPosition++;
                return new Token(TokenType.RIGHT_PAREN, ")");
            }
            case '{' -> {
                currentPosition++;
                return new Token(TokenType.LEFT_BRACE, "{");
            }
            case '}' -> {
                currentPosition++;
                return new Token(TokenType.RIGHT_BRACE, "}");
            }
            case '[' -> {
                currentPosition++;
                return new Token(TokenType.LEFT_BRACKET, "[");
            }
            case ']' -> {
                currentPosition++;
                return new Token(TokenType.RIGHT_BRACKET, "]");
            }
            case '<' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '=') {
                    currentPosition++;
                    return new Token(TokenType.LESS_THAN_OR_EQUAL, "<=");
                } else {
                    return new Token(TokenType.LESS_THAN, "<");
                }
            }
            case '>' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '=') {
                    currentPosition++;
                    return new Token(TokenType.GREATER_THAN_OR_EQUAL, ">=");
                } else {
                    return new Token(TokenType.GREATER_THAN, ">");
                }
            }
            case '!' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '=') {
                    currentPosition++;
                    return new Token(TokenType.NOT_EQUAL, "!=");
                } else {
                    return new Token(TokenType.NOT, "!");
                }
            }
            case '|' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '|') {
                    currentPosition++;
                    return new Token(TokenType.OR, "||");
                } else {
                    return null; // Invalid token
                }
            }
            case '&' -> {
                currentPosition++;
                if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '&') {
                    currentPosition++;
                    return new Token(TokenType.AND, "&&");
                } else {
                    return null; // Invalid token
                }
            }
            // Add more cases for other operators and punctuations as needed
            default -> {
                return null; // Unrecognized character or invalid token
            }
        }
    }
}


