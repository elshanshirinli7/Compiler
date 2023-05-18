import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;
    private Map<String, TokenType> variables;
    private List<String> errors;
    private int declaraCount = 0;

    private int initializerCount = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens.get(currentTokenIndex);
        this.variables = new HashMap<>();
        this.errors = new ArrayList<>();
    }

    public void parse() {
        while (currentToken.getType() != TokenType.EOF) {
            parseStatement();
        }

        checkVariableDeclarations();
    }

    private void checkVariableDeclarations() {
        for (Map.Entry<String, TokenType> entry : variables.entrySet()) {
            String variableName = entry.getKey();
            TokenType variableType = entry.getValue();

            int declarationCount = 0;
            for (Token token : tokens) {
                if (token.getType() == TokenType.IDENTIFIER && token.getValue().equals(variableName)) {
                    declarationCount++;
                    if (declarationCount > 1) {
                            addError("Variable " + variableName + " is already declared.");
                        break;
                    }
                }
            }

            if (declarationCount == 1 && !matchVariableType(variableName, variableType)) {
                addError("Variable " + variableName + " is declared with a different type.");
            }
        }
    }

    private boolean matchVariableType(String variableName, TokenType variableType) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token.getType() == TokenType.IDENTIFIER && token.getValue().equals(variableName)) {
                if (i + 2 < tokens.size() && tokens.get(i + 2).getType() == variableType) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addError(String message) {
        errors.add(message);
    }

    private boolean variableExists(String variableName) {
        return variables.containsKey(variableName);
    }

    private void parseStatement() {
        if (match(TokenType.VAR)) {
            parseVariableDeclaration();
        } else if (match(TokenType.IDENTIFIER)) {
            if (peek().getType() == TokenType.LEFT_BRACKET) {
                parseArrayAssignment();
            } else if (peek().getType() == TokenType.INCREMENT) {
                parseIncrementStatement();
            } else if (peek().getType() == TokenType.DECREMENT) {
                parseDecrementStatement();
            } else if (peek().getType() == TokenType.ASSIGN) {
                parseAssignment();
            } else {
                parseAssignmentWithArithmetic(); // Handle assignment with arithmetic expressions
            }
        } else if (match(TokenType.IF)) {
            parseIfStatement();
        } else if (match(TokenType.FOR)) {
            parseForLoop();
        } else if (match(TokenType.FUNC)) {
            parseFunctionDeclaration();
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken.getType());
        }
    }




    private void parseVariableDeclaration() {
        consume(TokenType.VAR);

        Token identifier = consume(TokenType.IDENTIFIER);
        String variableName = identifier.getValue();

        if (variableExists(variableName)) {
            addError("Variable " + variableName + " is already declared.");
            return;
        }

        if (match(TokenType.INT) || match(TokenType.FLOAT)) {
            TokenType type = currentToken.getType();
            consume(type);

            if (match(TokenType.ASSIGN)) {
                consume(TokenType.ASSIGN);
                parseExpression();
            }

            if (!match(TokenType.SEMICOLON)) {
                addError("Expected token type SEMICOLON but found " + currentToken.getType());
                return;
            }

            consume(TokenType.SEMICOLON);

            variables.put(variableName, type);
        } else if (match(TokenType.ASSIGN)) {
            TokenType type = currentToken.getType();
            consume(TokenType.ASSIGN);
            parseArrayDeclaration(variableName, type);
            parseArrayInitializer(variableName, type);
            if(declaraCount != initializerCount){
                throw new ArrayIndexOutOfBoundsException();
            }
        } else {
            addError("Expected token type INT or FLOAT but found " + currentToken.getType());
            return;
        }
    }

    private void parseArrayDeclaration(String variableName, TokenType variableType) {
        consume(TokenType.LEFT_BRACKET);

        if (match(TokenType.INTEGER_LITERAL)) {
            Token integerValue = consume(TokenType.INTEGER_LITERAL);
            declaraCount = Integer.parseInt(integerValue.getValue());
        } else {
            addError("Expected token type INTEGER_LITERAL but found " + currentToken.getType());
            return;
        }

        consume(TokenType.RIGHT_BRACKET);
        if(match(TokenType.INT)){
            consume(TokenType.INT);
            
        } else if (match(TokenType.FLOAT)) {
            consume(TokenType.FLOAT);
        }

    }

    private void parseArrayInitializer(String variableName, TokenType variableType) {
        consume(TokenType.LEFT_BRACE);

        while (!match(TokenType.RIGHT_BRACE)) {
            parseExpression();
            if (!match(TokenType.RIGHT_BRACE)) {
                consume(TokenType.COMMA);
            }
            initializerCount++;
        }
        consume(TokenType.RIGHT_BRACE);

        if(declaraCount != initializerCount){
            addError("ArrayOutOfBounds Exception");
        }
    }


    private void parseFunctionDeclaration() {
        consume(TokenType.FUNC);
        consume(TokenType.IDENTIFIER);
        consume(TokenType.LEFT_PAREN);
        consume(TokenType.RIGHT_PAREN);
        parseBlock();
    }

    private void parseArrayAssignment() {
        Token identifier = consume(TokenType.IDENTIFIER);
        consume(TokenType.LEFT_BRACKET);
        parseExpression();
        consume(TokenType.RIGHT_BRACKET);
        consume(TokenType.ASSIGN);
        parseExpression();
        consume(TokenType.SEMICOLON);
    }


    private void parseAssignment() {
        Token identifier = consume(TokenType.IDENTIFIER);

        if (!variableExists(identifier.getValue())) {
            addError("Variable " + identifier.getValue() + " is not declared.");
            return;
        }

        consume(TokenType.ASSIGN);
        parseExpression();
        consume(TokenType.SEMICOLON);
    }

    private void parseIfStatement() {
        consume(TokenType.IF);
        if (match(TokenType.IDENTIFIER)) {
            consume(TokenType.IDENTIFIER);
            if(match(TokenType.EQUAL)){
                consume(TokenType.EQUAL);
                parseExpression();
            } else if (match(TokenType.NOT_EQUAL)) {
                consume(TokenType.NOT_EQUAL);
                parseExpression();
            }
        }
        parseBlock();

        if (match(TokenType.ELSE)) {
            consume(TokenType.ELSE);
            if (match(TokenType.IF)) {
                parseIfStatement();
            } else {
                parseBlock();
            }
        }
    }


    private void parseForLoop() {
        consume(TokenType.FOR);
        consume(TokenType.LEFT_PAREN);
        if (match(TokenType.VAR)) {
            parseVariableDeclaration();
            consume(TokenType.SEMICOLON);
        } else {
            parseAssignment();
        }
        parseExpression();
        consume(TokenType.SEMICOLON);
        consume(TokenType.IDENTIFIER);
        if(match(TokenType.INCREMENT)){
            consume(TokenType.INCREMENT);
        }
        else if(match(TokenType.DECREMENT)){
            consume(TokenType.DECREMENT);
        }
        consume(TokenType.RIGHT_PAREN);

        parseBlock();
    }

    private void parseIncrementStatement() {
        Token identifier = consume(TokenType.IDENTIFIER);
        consume(TokenType.INCREMENT);
        consume(TokenType.SEMICOLON);
        // You can perform further processing or create an AST node for the increment statement here
    }

    private void parseDecrementStatement() {
        Token identifier = consume(TokenType.IDENTIFIER);
        consume(TokenType.DECREMENT);
        consume(TokenType.SEMICOLON);
        // You can perform further processing or create an AST node for the decrement statement here
    }


    private void parseExpression() {
        parseBooleanExpression();
    }

    private void parseBlock() {
        consume(TokenType.LEFT_BRACE);

        while (!match(TokenType.RIGHT_BRACE)) {
            parseStatement();
        }

        consume(TokenType.RIGHT_BRACE);
    }

    private void parseBooleanExpression() {
        parseComparisonExpression();

        while (match(TokenType.AND) || match(TokenType.OR)) {
            consume(currentToken.getType());
            parseComparisonExpression();
        }
    }

    private void parseComparisonExpression() {
        parseAdditiveExpression();

        if (match(TokenType.EQUAL) || match(TokenType.NOT_EQUAL) ||
                match(TokenType.LESS_THAN) || match(TokenType.LESS_THAN_OR_EQUAL) ||
                match(TokenType.GREATER_THAN) || match(TokenType.GREATER_THAN_OR_EQUAL)) {
            consume(currentToken.getType());
            parseAdditiveExpression();
        }
    }


    private void parseAdditiveExpression() {
        parseMultiplicativeExpression();

        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            consume(currentToken.getType());
            parseMultiplicativeExpression();
        }
    }

    private void parseMultiplicativeExpression() {
        parseUnaryExpression();

        while (match(TokenType.MULTIPLY) || match(TokenType.DIVIDE)) {
            consume(currentToken.getType());
            parseUnaryExpression();
        }
    }

    private void parseAssignmentWithArithmetic() {
        Token identifier = consume(TokenType.IDENTIFIER);
        consume(TokenType.ASSIGN);

        parseExpression();

        consume(TokenType.SEMICOLON);
    }



    private void parseUnaryExpression() {
        if (match(TokenType.MINUS)) {
            consume(TokenType.MINUS);
        } else if (match(TokenType.IDENTIFIER)) {
            //consume(TokenType.IDENTIFIER);

            if (match(TokenType.INCREMENT)) {
                consume(TokenType.INCREMENT);
            }
        }
        parsePrimaryExpression();
    }

    private void parsePrimaryExpression() {
        if (match(TokenType.INTEGER_LITERAL) || match(TokenType.FLOAT_LITERAL) || match(TokenType.STRING_LITERAL)) {
            consume(currentToken.getType());
        } else if (match(TokenType.IDENTIFIER)) {
            consume(TokenType.IDENTIFIER);

            if (match(TokenType.EQUAL)) {
                consume(TokenType.EQUAL);
                parseExpression();
            } else if (match(TokenType.LEFT_BRACKET)) {
                consume(TokenType.LEFT_BRACKET);
                parseExpression();
                consume(TokenType.RIGHT_BRACKET);
            }
        } else if (match(TokenType.NEW)) {
            consume(TokenType.NEW);
            consume(TokenType.IDENTIFIER);
            consume(TokenType.LEFT_PAREN);
            consume(TokenType.RIGHT_PAREN);
        } else if (match(TokenType.LEFT_PAREN)) {
            consume(TokenType.LEFT_PAREN);
            parseExpression();
            consume(TokenType.RIGHT_PAREN);
        } else {
            throw new RuntimeException("Unexpected token: " + currentToken.getType());
        }
    }


    private Token consume(TokenType expectedType) {
        if (currentToken.getType() == expectedType) {
            Token token = currentToken;
            currentTokenIndex++;
            if (currentTokenIndex < tokens.size()) {
                currentToken = tokens.get(currentTokenIndex);
            } else {
                currentToken = new Token(TokenType.EOF, "");
            }
            return token;
        } else {
            throw new RuntimeException("Expected token type " + expectedType + " but found " + currentToken.getType());
        }
    }

    private boolean match(TokenType expectedType) {
        return currentToken.getType() == expectedType;
    }

    private Token peek() {
        if (currentTokenIndex + 1 < tokens.size()) {
            return tokens.get(currentTokenIndex + 1);
        } else {
            return new Token(TokenType.EOF, "");
        }
    }
}
