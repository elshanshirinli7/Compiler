public enum TokenType {
    // Keywords
    VAR,
    INT,
    FLOAT,
    IF,
    ELSE,
    FOR,
    FUNC,
    RETURN,
    ARRAY,
    ERROR,
    // ...

    // Identifiers
    IDENTIFIER,

    // Literals
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    // ...

    // Operators
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    ASSIGN,
    EQUAL,
    NOT_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    NOT,
    OR,         // Logical OR operator
    AND,        // Logical AND operator
    INCREMENT,// ++
    DECREMENT,//--
    TRUE,
    FALSE,
    // ...

    // Punctuation
    SEMICOLON,
    COMMA,
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    // ...


    // Special token types
    EOF,

    // Custom token
    NEW
}