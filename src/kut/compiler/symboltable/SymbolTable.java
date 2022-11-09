package kut.compiler.symboltable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kut.compiler.exception.SyntaxErrorException;
import kut.compiler.parser.ast.AstGlobal;
import kut.compiler.parser.ast.AstLocal;

public class SymbolTable 
{	
	protected Map<String, AstGlobal> 			globalVariables		;
	protected Map<String, LocalVariableInfo>	localVariables		;
	
	/**
	 * 
	 */
	public SymbolTable() {
		globalVariables = new HashMap<String, AstGlobal>();
	}
	
	/**
	 * @param varname
	 * @throws SyntaxErrorException
	 */
	public void declareGlobalVariable(AstGlobal gvar) 
	{
		String varname = gvar.getVarName().getIdentifier();
		globalVariables.put(varname, gvar);
	}
	
	/**
	 * @param id
	 * @return
	 */
	public SymbolType getSymbolType(String id)
	{
		if (globalVariables.containsKey(id)) {
			return SymbolType.GlobalVariable; 
		}		

		return SymbolType.Unknown;
	}
	
	/**
	 * @return
	 */
	public List<String> getGlobalVariables()
	{
		return new LinkedList<String>(globalVariables.keySet());
	}
	

	/**
	 * 
	 */
	public void printGlobalVariables() 
	{	
		System.out.println("the list of global variables");
		for (String id: globalVariables.keySet()) {
			System.out.println(globalVariables.get(id));
		}
	}
	
	/**
	 * @param t
	 */
	public void declareLocalVariable(AstLocal lvar) throws SyntaxErrorException
	{
		String id = lvar.getVarName().getIdentifier();
		if (localVariables.containsKey(id)){
			throw new SyntaxErrorException("duplicate local variable declarations : " + lvar.getVarName());
		}
		
		LocalVariableInfo i = new LocalVariableInfo();
		i.node = lvar;
		i.stackIndex = 0;
		
		this.localVariables.put(id, i);
		return;
	}
	/**
	 * 
	 */
	public void resetLocalVariableTable() {
		this.localVariables = new HashMap<String, LocalVariableInfo>();
	}
	
	
	
	/**
	 * @param vname
	 * @return
	 */
	public int getStackIndexOfLocalVariable(String vname)
	{
		if (!this.localVariables.containsKey(vname)) {
			return 0;
		}
		LocalVariableInfo info = this.localVariables.get(vname);
		
		return info.stackIndex;
	}
	
	/**
	 * @return
	 */
	public int getStackFrameExtensionSize()
	{
		int min = 0;
		for (LocalVariableInfo s: this.localVariables.values()) {
			min = min > s.stackIndex ? s.stackIndex : min;
		}
				
		return -min;
	}
	

	/**
	 * 
	 */
	public void assignLocalVariableIndices() {
		int idx = -8; // the previous rbp is located at rbp + 0, so got to start from -8.
		for (LocalVariableInfo s: this.localVariables.values()) {
			s.stackIndex = idx;
			idx -= 8;
		}
	}
}
