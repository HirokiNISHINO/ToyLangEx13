package kut.compiler.parser.ast;


import kut.compiler.compiler.CodeGenerator;

public class AstEmptyStatement extends AstNode 
{

	
	/**
	 * @param t
	 */
	public AstEmptyStatement()
	{
	}

	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "empty statement: ;");
	}

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen)
	{	
		return;
	}
	

}
