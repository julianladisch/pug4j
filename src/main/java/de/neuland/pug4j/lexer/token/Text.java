package de.neuland.pug4j.lexer.token;


public class Text extends Token {

    public Text(String value, int lineNumber) {
        super(value, lineNumber);
    }

    public Text() {

    }

    public Text(String value) {
        super(value);
    }
}
