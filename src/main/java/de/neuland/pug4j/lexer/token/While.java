package de.neuland.pug4j.lexer.token;

public class While extends Token {
    public While(String value) {
        super(value);
    }

    public While(String value, int lineNumber) {

        super(value, lineNumber);
    }
}
