package kut.compiler.parser.ast;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Token;

public class AstGlobal extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	protected AstIdentifier	varname	;
	protected Token			type	;
	
	/**
	 * @param t
	 */
	public AstGlobal(AstIdentifier varname, Token type, Token t)
	{
		this.varname 	= varname;
		this.type		= type;
		this.t 			= t;
	}
	
	

	/**
	 *
	 */
	@Override
	protected void printTree(int indent) {
		this.println(indent, "global (type: " + type + ")");
		varname.printTree(indent + 1);
	}



	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws CompileErrorException
	{	
		//there is nonthing to do here.
		return; 
	}
	
	/**
	 * @return
	 */
	public AstIdentifier getVarName() {
		return this.varname;
	}
	/**
	 *
	 */
	/**
	 *
	 */
	public void preprocessGlobalVariables(CodeGenerator gen)
	{
		gen.declareGlobalVariable(this);
		return;
	}
	
	/**
	 *
	 */
	public String toString() {
		return this.getVarName().getIdentifier() + ":" + this.type;
	}

}
