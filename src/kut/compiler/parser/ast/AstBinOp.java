package kut.compiler.parser.ast;

import java.io.IOException;

import kut.compiler.compiler.CodeGenerator;
import kut.compiler.exception.CompileErrorException;
import kut.compiler.lexer.Token;

public class AstBinOp extends AstNode 
{
	/**
	 * 
	 */
	protected Token t;
	
	protected AstNode	lhs;
	protected AstNode	rhs;
	
	/**
	 * @param t
	 */
	public AstBinOp(AstNode lhs, AstNode rhs, Token t)
	{
		this.lhs = lhs;
		this.rhs = rhs;
		this.t = t;
	}
	
	/**
	 *
	 */
	public void printTree(int indent) {
		this.println(indent, "binop:" + t);
		lhs.printTree(indent + 1);
		rhs.printTree(indent + 1);
	}

	
	

	/**
	 *
	 */
	@Override
	public void cgen(CodeGenerator gen) throws IOException, CompileErrorException
	{	
		lhs.cgen(gen);
		gen.printCode("push rax");
		rhs.cgen(gen);
		
		switch(t.getC())
		{
		case '+':
			gen.printCode("add rax, [rsp]");
			gen.printCode("add rsp, 8");
			break;
			
		case '-':
			gen.printCode("mov rbx, rax");
			gen.printCode("pop rax");
			gen.printCode("sub rax, rbx");
			break;
			
		case '*':
			gen.printCode("imul rax, [rsp]");
			gen.printCode("add rsp, 8");
			break;
			
		case '/':
			gen.printCode("mov rbx, rax");
			gen.printCode("mov rdx, 0");
			gen.printCode("mov rax, [rsp]");
			gen.printCode("add rsp, 8");
			gen.printCode("idiv rbx");
			break;
						
		default:
			throw new CompileErrorException("the code shouldn't reach here. There may be a bug in the parser.");
			
		}
		
		return;
	}
	

	/**
	 *
	 */
	public void preprocessLocalVariables(CodeGenerator gen) throws CompileErrorException
	{
		this.lhs.preprocessLocalVariables(gen);
		this.rhs.preprocessLocalVariables(gen);
	}


}
