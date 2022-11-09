package kut.compiler.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import kut.compiler.exception.CompileErrorException;
import kut.compiler.exception.SyntaxErrorException;
import kut.compiler.parser.ast.AstNode;
import kut.compiler.parser.ast.AstGlobal;
import kut.compiler.parser.ast.AstLocal;
import kut.compiler.symboltable.SymbolTable;
import kut.compiler.symboltable.SymbolType;

/**
 * @author hnishino
 *
 */
/**
 * @param program
 */
public class CodeGenerator 
{
	protected Platform		platform;
	protected String 		filename;
	protected AstNode			program	;
	protected PrintWriter 	writer	;
	
	protected SymbolTable	symbolTable;
		
	/**
	 * @param program
	 * @param filename
	 */
	public CodeGenerator(AstNode program, String filename, Platform platform) 
	{
		this.program 	= program;
		this.filename 	= filename;
		this.platform	= platform;
		
		this.symbolTable = new SymbolTable();
	}
	
	
	/**
	 * @return
	 */
	public String getExitSysCallNum()
	{
		return (this.platform == Platform.MAC ? "0x2000001" : "0x01");
	}
	
	/**
	 * @param funcname
	 * @return
	 */
	public String getExternalFunctionName(String funcname)
	{
		return (this.platform == Platform.MAC ? "_" + funcname : funcname);
	}
	
	/**
	 * @return
	 */
	public String getEntryPointLabelName()
	{
		return (this.platform == Platform.MAC ? "_main" : "_start");
	}
	
	
	/**
	 * @param varname
	 */
	public void declareGlobalVariable(AstGlobal gvar)
	{
		this.symbolTable.declareGlobalVariable(gvar);
	}
	
	/**
	 * @param varname
	 */
	public String getGlobalVariableLabel(String varname)
	{
		return "global_variable#" + varname;
	}
	
	/**
	 * @param identifier
	 * @return
	 */
	public SymbolType getSymbolType(String identifier)
	{
		SymbolType t = this.symbolTable.getSymbolType(identifier);	
		return t;
	}
	
	/**
	 * @param varname
	 * @throws IOException
	 */
	public void allocateGlobalVariables() throws IOException
	{
		this.printComment("; global variables");
		this.printSection("section .data");
		
		List<String> gvs=  symbolTable.getGlobalVariables();
		for (String gvarname: gvs) {
			this.printCode(	this.getGlobalVariableLabel(gvarname) + ": db 0, 0, 0, 0, 0, 0, 0, 0 ; allocate 64bits for each global variables. Initialize with zeros.", 1);
		}
		
		return;
	}
	
	/**
	 * 
	 */
	public void resetLocalVariableTable()
	{
		this.symbolTable.resetLocalVariableTable();
	}
	
	/**
	 * @throws IOException
	 */
	public void declareLocalVariable(AstLocal lvar) throws SyntaxErrorException
	{
		this.symbolTable.declareLocalVariable(lvar);
	}
	
	/**
	 * 
	 */
	public void assignLocalVariableIndices() {
		this.symbolTable.assignLocalVariableIndices();
	}
	
	
	/**
	 * @return
	 */
	public int getStackFrameExtensionSize() {
		return this.symbolTable.getStackFrameExtensionSize();
	}
	
	/**
	 * @param id
	 * @throws SyntaxErrorException
	 */
	public int getStackIndexOfLocalVariable(String id) throws SyntaxErrorException
	{
		int idx = this.symbolTable.getStackIndexOfLocalVariable(id);
		if (idx == 0) {
			throw new SyntaxErrorException("undeclared local variable found: " + this);
		}
		return idx;
	}
	
	/**
	 * 
	 */
	/**
	 * 
	 */
	public void preprocess() 
	{
		this.program.preprocessGlobalVariables(this);
	}
	
	/**
	 * @throws IOException
	 */
	public void generateCode() throws IOException, CompileErrorException
	{
		File f = new File(filename);
		writer = new PrintWriter(f);
		
		//--------------------------------------
		//extern 
		this.printComment("; 64 bit code.");
		this.printCode	("bits 64", 0);
		
		//--------------------------------------
		//extern 
		this.printComment("; to use the printf() function.");

		this.printCode	("extern " + this.getExternalFunctionName("printf"), 0);
		this.printCode	();
		
		//--------------------------------------
		//data section
		this.printComment("; data section.");
		this.printSection("section .data");
		
		this.printCode	(	"fmt:    db \"exit code:%d\", 10, 0 ; the format string for the exit message.");

		this.printCode();
		this.allocateGlobalVariables();
		this.printCode();
		
		//--------------------------------------
		//text section
		this.printComment("; text section");
		this.printSection("section .text");
		this.printCode	(	"global " + this.getEntryPointLabelName() + " ; the entry point.");
		this.printCode();
		
		
		//the exit_program subroutine.		
		this.printComment("; the subroutine for sys-exit. rax will be the exit code.");
		this.printLabel("exit_program");				// where we exit the program.
		
		this.printCode(	"and rsp, 0xFFFFFFFFFFFFFFF0 ; stack must be 16 bytes aligned to call a C function.");
		this.printCode(	"push rax ; we need to preserve rax here.");
		this.printCode();
		this.printCode(	"; call printf to print out the exti code.");
		this.printCode(	"lea rdi, [rel fmt] ; the format string");
		this.printCode(	"mov rsi, rax		; the exit code ");
		this.printCode(  "mov rax, 0			; no xmm register is used.");
		this.printCode(	"call " + this.getExternalFunctionName("printf"));
		this.printCode();
		this.printCode(	"mov rax, "+ this.getExitSysCallNum() + "; specify the exit sys call.");
		this.printCode(	"pop rdi ; this is the rax value we pushed at the entry of this sub routine");
		this.printCode(	"syscall ; exit!");
		this.printCode();
		
		//main function
		this.printLabel	(this.getEntryPointLabelName());
		this.printCode(	"mov rax, 0 ; initialize the accumulator register.");
		
		
		this.program.cgen(this);
		
		//epilogue
		this.printCode();
		this.printCode(	"jmp exit_program ; exit the program, rax should hold the exit code.");

		writer.flush();
		writer.close();
		
		return;
	}
	
	
	/**
	 * 
	 */
	public void printCode()
	{
		writer.println("");
		return;		
	}
	
	/**
	 * @param buf
	 * @param code
	 * @param indent
	 */
	public void printCode(String code) throws IOException
	{
		this.printCode(code, 1);
		return;
	}
	
	/**
	 * @param label
	 */
	public void printLabel(String label) throws IOException
	{
		this.printCode(label + ":", 0);
	}
	
	/**
	 * @param comment
	 */
	public void printComment(String comment) throws IOException
	{
		this.printCode(comment, 0);
	}
	
	/**
	 * @param section
	 */
	public void printSection(String section) throws IOException
	{
		this.printCode(section, 0);
	}
	
	/**
	 * @param buf
	 * @param code
	 * @param indent
	 */
	public void printCode(String code, int indent) throws IOException
	{
		for (int i = 0; i < indent; i++) {
			writer.print("\t");
		}
		
		writer.println(code);
		
		return;
	}
	
	/**
	 * 
	 */
	public void printGlobalVariables() {
		this.symbolTable.printGlobalVariables();
	}

}
