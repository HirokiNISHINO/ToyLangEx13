package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.lexer.Token;

public class AstIntLiteral extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	/**
	 * @param t
	 */
	public AstIntLiteral(Token t)
	{
		this.t = t;
	}
	

	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "int literal:" + t);
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException
	{	
		gen.printCode("mov rax, " + this.t.getL());

		return;
	}
	

}
