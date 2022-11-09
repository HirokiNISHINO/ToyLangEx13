package kut.compiler.lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import kut.compiler.exception.CompileErrorException;


/**
 * @author hnishino
 *
 */
public class Lexer 
{
	/**
	 * the filename of a program to load.
	 */
	protected File				file	;	
	
	protected FileReader 		reader	;
	protected Stack<Integer>	unreadCharacters;
	
	protected int				lineNo	;
	/**
	 * @param program
	 */
	public Lexer(String filename) throws CompileErrorException
	{
		this.unreadCharacters = new Stack<Integer>();
		
		this.file = new File(filename);
		
		reader = null;
		try {
			reader = new FileReader(file);
		}
		catch (FileNotFoundException e) {
			throw new CompileErrorException("file not found: " + this.file.getAbsolutePath());
		}
		
		this.lineNo = 0;
		
		return;
	}
	


	/**
	 * @return
	 * @throws IOException
	 */
	protected int read() throws IOException
	{
		int i = 0;
		if (!unreadCharacters.isEmpty()) {
			i = unreadCharacters.pop();
		}
		else {
			i = reader.read();
		}
		
		if (i == '\n') {
			lineNo++;
		}
		
		return i;
	}
	
	/**
	 * @param i
	 * @throws IOException
	 */
	protected void unread(int i) throws IOException
	{
		if (i == '\n') {
			lineNo--;
		}
		unreadCharacters.push(i);
	}
	
	/**
	 * @return
	 */
	public Token getNextToken() throws IOException, CompileErrorException
	{
		if (this.reader == null) {
			return null;
		}

		int i = this.read();

		if (i < 0) {
			return new Token(-1, "EOF", lineNo);
		}

		char c = (char)i;


		//skip the white space character.
		if (Character.isWhitespace(c)) {
			//tail-call optimization.
			return getNextToken();
		}

		//if it is a digit, then get a integer number token.
		if (Character.isDigit(c)) {
			this.unread(i); 
			return this.getNextTokenInteger();
		}

		//+-/*
		switch(c) {
		case '+':
		case '-':
		case '*':
		case '/':
			return new Token(i, "" + c, lineNo);
		default:
			break;
		}
		
		//we will handle '==' later on, so we separate this code 
		//from the above switch statement for one character operator.
		if (c == '=') {
			return new Token(i, "" + c, lineNo);
		}

		//check if the character is an identifier start or not.
		if (!Character.isJavaIdentifierStart(i)) {
			return new Token(i, "" + c, lineNo);
		}
		
		this.unread(i);
		String lexeme = this.identifierOrKeyword();
		
		//reserved keywords  
		if (lexeme.equals("global")) {
			return new Token(TokenClass.GLOBAL, lexeme, lineNo);	
		}
		
		if (lexeme.equals("local")) {
			return new Token(TokenClass.LOCAL, lexeme, lineNo);	
		}
		
		if (lexeme.equals("int")) {
			return new Token(TokenClass.INT, lexeme, lineNo);
		}
		
		//if none of above, it's an identifier.
		return new Token(TokenClass.Identifier, lexeme, lineNo);		
	}
	
	/**
	 * @return
	 */
	public String identifierOrKeyword() throws IOException
	{
		StringBuffer sb = new StringBuffer();

		//we know that the first character is already a　valid identifier start character.
		//when this method is called.
		while(true) {
			int i = this.read();
			char c = (char)i;
			if (!Character.isJavaIdentifierPart(c)) {
				this.unread(i);
				break;
			}
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	public Token getNextTokenInteger() throws IOException
	{
		StringBuffer sb = new StringBuffer();
		
		int i;
		do {
			i = this.read();
			if (i < 0) {
				break;
			}
			char c = (char)i;
			if (!Character.isDigit(c)) {
				break;
			}
			
			sb.append(c);
		} while(true);

		this.unread(i);

		Token t = new Token(TokenClass.IntLiteral, sb.toString(), lineNo);
		
		return t;
	}
}
